package st.photonbur.UHC.Nuzlocke.Discord;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.ChannelManager;
import net.dv8tion.jda.managers.RoleManager;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordBot {
    public enum Event {
        DEATH("Well, someone died."),
        JOIN(StringLib.DiscordBot$WelcomeMessage),
        QUIT(StringLib.DiscordBot$GoodbyeMessage),
        START(StringLib.DiscordBot$AnnounceStart),
        TEAM_WIPE(StringLib.DiscordBot$TeamWipe),
        TRAINER_WIPE(StringLib.DiscordBot$TrainerWipe),
        WIN(StringLib.DiscordBot$Win);

        private final String message;

        Event(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private JDA bot;
    Nuzlocke nuz;

    ArrayList<RoleManager> roles = new ArrayList<>();
    ArrayList<ChannelManager> channels = new ArrayList<>();
    ChannelManager participantsHangout;
    Guild guild;
    final Object LOCK = new Object();
    RoleManager participants;
    TextChannel general;

    public DiscordBot (Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public void addTeam(String playerName, Color color) {
        synchronized(LOCK) {
            while (guild.getVoiceChannels().stream().noneMatch(vc -> vc.getName().equalsIgnoreCase("Participants Hangout"))) try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Permission[] serverAllowed = {
                Permission.NICKNAME_CHANGE, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE,
                Permission.MESSAGE_HISTORY, Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_USE_VAD
        };
        RoleManager newRole = guild.createRole()
                .give(serverAllowed)
                .revoke(invertPermissions(new ArrayList<>(Arrays.asList(serverAllowed)), true))
                .setName("Team "+ playerName)
                .setMentionable(true)
                .setColor(color);
        newRole.update();
        guild.getManager().addRoleToUser(getUser(playerName), newRole.getRole()).update();

        Permission[] channelAllowedTeam = {
                Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_USE_VAD
        };
        Permission[] channelAllowedSpec = {
                Permission.VOICE_CONNECT
        };
        ChannelManager newVC = guild.createVoiceChannel("Team "+ playerName);
        newVC.getChannel().createPermissionOverride(newRole.getRole())
                .grant(channelAllowedTeam)
                .deny(invertPermissions(new ArrayList<>(Arrays.asList(channelAllowedTeam)), false))
                .update();
        newVC.getChannel().createPermissionOverride(participants.getRole())
                .deny(Permission.VOICE_CONNECT)
                .update();
        newVC.getChannel().createPermissionOverride(guild.getPublicRole())
                .grant(channelAllowedSpec)
                .deny(invertPermissions(new ArrayList<>(Arrays.asList(channelAllowedSpec)), false))
                .update();
        newVC.update();
        if(getUser(playerName) != null) if(guild.getVoiceStatusOfUser(getUser(playerName)).inVoiceChannel()) {
            guild.getManager().moveVoiceUser(getUser(playerName), (VoiceChannel) newVC.getChannel());
        }

        channels.add(newVC);
        roles.add(newRole);
    }

    public void announce(Event e) {
        announce(e, e.getMessage());
    }

    public void announce(Event e, String msg) {
        logMessage(general, e.name(), msg);
    }

    public void cleanUp() {
        guild.getUsers().stream()
                .filter(u -> guild.getVoiceStatusOfUser(u).inVoiceChannel())
                .forEach(u -> guild.getManager().moveVoiceUser(u, guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase("general")).findFirst().get()));
        channels.forEach(ChannelManager::delete);
        channels.clear();
        guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase("general")).findFirst().get().createPermissionOverride(participants.getRole()).reset();
        roles.forEach(r -> {
            nuz.getLogger().info("Deleting role "+ r.getRole().getName());
            r.delete();
        });
        roles.clear();
    }

    public void deregisterPlayer(String p) {
        guild.getManager().removeRoleFromUser(getUser(p), participants.getRole()).update();

        if(nuz.getGameManager().isGameInProgress() && guild.getVoiceStatusOfUser(getUser(p)).inVoiceChannel()) {
            guild.getManager().moveVoiceUser(getUser(p), guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase("general")).findFirst().get());
        }
    }

    public JDA get() {
        return bot;
    }

    private User getUser(String name) {
        return bot.getUsersByName(name).stream().filter(u -> u.getUsername().equals(name)).findFirst().orElse(null);
    }

    public void logMessage(TextChannel tc, String purpose, String msg) {
        tc.sendMessage("**[" + purpose.toUpperCase() + "]** " + msg);
    }

    public Permission[] invertPermissions(ArrayList<Permission> allowed, boolean targetGuild) {
        List<Permission> denied = new ArrayList<>();
        for(Permission p: Permission.values()) {
            if(!allowed.contains(p) && (targetGuild ? p.isGuild() : p.isChannel())) denied.add(p);
        }
        return denied.toArray(new Permission[denied.size()]);
    }

    public void prepareGame() {
        synchronized (LOCK) {
            Permission[] allowedEveryone = {Permission.VOICE_CONNECT};
            participantsHangout = guild.createVoiceChannel("Participants Hangout");
            participantsHangout.getChannel().createPermissionOverride(guild.getPublicRole())
                    .grant(allowedEveryone)
                    .deny(invertPermissions(new ArrayList<>(Arrays.asList(allowedEveryone)), false))
                    .update();

            channels.add(participantsHangout);
            guild.getUsersWithRole(participants.getRole()).stream()
                    .filter(u -> guild.getVoiceStatusOfUser(u).inVoiceChannel())
                    .forEach(
                            u -> guild.getManager().moveVoiceUser(u, (VoiceChannel) participantsHangout.getChannel())
                    );

            Permission[] allowedParticipant = {};
            guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase("general")).findFirst().get()
                    .createPermissionOverride(participants.getRole())
                    .grant(allowedParticipant)
                    .deny(invertPermissions(new ArrayList<>(Arrays.asList(allowedParticipant)), true))
                    .update();

            LOCK.notifyAll();
        }
    }

    public void registerPlayer(String p) {
        guild.getManager().addRoleToUser(getUser(p), participants.getRole()).update();
        if (nuz.getGameManager().isGameInProgress() && guild.getVoiceStatusOfUser(getUser(p)).inVoiceChannel()) {
            if (nuz.getGameManager().getScoreboard().getEntryTeam(p) != null) {
                VoiceChannel teamChannel = guild.getVoiceChannels().stream()
                        .filter(vc -> vc.getName().equals(nuz.getGameManager().getScoreboard().getEntryTeam(p).getName()))
                        .findFirst().orElse(null);
                if(teamChannel != null) guild.getManager().moveVoiceUser(getUser(p), teamChannel);
            } else {
                guild.getManager().moveVoiceUser(
                        getUser(p), (VoiceChannel) participantsHangout.getChannel()
                );
            }
        }
    }

    public void start() {
        try {
            bot = new JDABuilder().setBotToken("MjAyNjY1MjE0NzQxNTc3NzI4.CmdqMA.6FnFJgLRxoHxWoenLabblZxu5Tk").addListener(new DiscordListener(nuz)).buildBlocking();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        participants.delete();
        bot.shutdown();
    }

}
