package st.photonbur.UHC.Nuzlocke.Discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordBot {
    private final Nuzlocke nuz;
    private final ArrayList<Channel> channels = new ArrayList<>();
    private final ArrayList<Role> roles = new ArrayList<>();
    private final Object LOCK = new Object();
    Guild guild;
    Role participants;
    TextChannel general;
    private JDA bot;
    private String commandPrefix;
    private String token;
    private boolean running;
    private Channel participantsHangout;

    public DiscordBot(Nuzlocke nuz) {
        this.nuz = nuz;
        this.running = false;
    }

    public void addTeam(String playerName, Color color) {
        synchronized (LOCK) {
            while (guild.getVoiceChannels().stream().noneMatch(vc -> vc.getName().equalsIgnoreCase("Participants Hangout")))
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        Permission[] serverAllowed = {
                Permission.NICKNAME_CHANGE, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE,
                Permission.MESSAGE_HISTORY, Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_USE_VAD
        };
        Role newRole = guild.getController().createRole()
                .setPermissions(serverAllowed)
                .setPermissions(invertPermissions(new ArrayList<>(Arrays.asList(serverAllowed)), true))
                .setName("Team " + playerName)
                .setMentionable(true)
                .setColor(color)
                .setHoisted(true)
                .complete();
        addRole(getMemberByLink(playerName), newRole);

        List<Permission> channelAllowedTeam = Arrays.asList(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_USE_VAD);
        List<Permission> channelAllowedSpec = Collections.singletonList(Permission.VOICE_CONNECT);
        Channel newVC = guild.getController().createVoiceChannel("Team " + playerName)
                .addPermissionOverride(newRole, channelAllowedTeam, invertPermissions(channelAllowedTeam, false))
                .addPermissionOverride(participants, 0, Permission.getRaw(Permission.VOICE_CONNECT))
                .addPermissionOverride(guild.getPublicRole(), channelAllowedSpec, invertPermissions(channelAllowedSpec, false))
                .complete();
        if (getMemberByLink(playerName) != null) {
            if (getMemberByLink(playerName).getVoiceState().inVoiceChannel()) {
                movePlayer(getMemberByLink(playerName), (VoiceChannel) newVC);
            }
        }

        channels.add(newVC);
        roles.add(newRole);
    }

    public void addRole(String playerName, String teamName) {
        addRole(getMemberByLink(playerName), teamName);
    }

    private void addRole(Member member, String teamName) {
        addRole(member, roles.stream().filter(r -> r.getName().equalsIgnoreCase(teamName)).findFirst().orElse(null));
    }

    private void addRole(Member member, Role role) {
        guild.getController().addRolesToMember(member, role);
    }

    public void announce(Event e) {
        announce(e, e.getMessage());
    }

    public void announce(Event e, String msg) {
        logMessage(general, e.name(), msg);
    }

    public void cleanUp() {
        guild.getMembers().stream()
                .filter(m -> m.getVoiceState().inVoiceChannel())
                .forEach(m -> guild.getController().moveVoiceMember(m, guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase("general")).findFirst().get()));
        channels.forEach(c -> c.delete().queue());
        channels.clear();

        guild.getVoiceChannels().stream()
                .filter(vc -> vc.getName().equalsIgnoreCase("general"))
                .findFirst().get().getPermissionOverride(participants).delete().queue();

        roles.forEach(r -> {
            nuz.getLogger().info("Deleting role " + r.getName());
            r.delete();
        });
        roles.clear();
    }

    public void deregisterPlayer(String p) {
        guild.getController().removeRolesFromMember(getMemberByLink(p), participants).queue();

        if (nuz.getGameManager().isGameInProgress() && getMemberByLink(p).getVoiceState().inVoiceChannel()) {
            guild.getController().moveVoiceMember(getMemberByLink(p), guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase("general")).findFirst().get());
        }
    }

    public JDA getJDA() {
        return bot;
    }

    private Member getMemberByLink(String ign) {
        return guild.getMember(nuz.getServerLinkManager().getLinkedUser(ign));
    }

    private void logMessage(TextChannel tc, String purpose, String msg) {
        tc.sendMessage("**[" + purpose.toUpperCase() + "]** " + msg).queue();
    }

    private List<Permission> invertPermissions(List<Permission> allowed, boolean targetGuild) {
        List<Permission> denied = new ArrayList<>();
        for (Permission p : Permission.values()) {
            if (!allowed.contains(p) && (targetGuild ? p.isGuild() : p.isChannel())) {
                denied.add(p);
            }
        }
        return denied;
    }

    public void movePlayer(String playerName, String teamName) {
        movePlayer(getMemberByLink(playerName), teamName);
    }

    private void movePlayer(Member member, String teamName) {
        movePlayer(member, guild.getVoiceChannels().stream().filter(vc -> vc.getName().equalsIgnoreCase(teamName)).findFirst().orElse(null));
    }

    private void movePlayer(Member member, VoiceChannel vc) {
        if (member.getVoiceState().inVoiceChannel()) {
            guild.getController().moveVoiceMember(member, vc);
        }
    }

    public void prepareGame() {
        synchronized (LOCK) {
            List<Permission> allowedEveryone = Collections.singletonList(Permission.VOICE_CONNECT);
            participantsHangout = guild.getController().createVoiceChannel("Participants Hangout")
                    .addPermissionOverride(guild.getPublicRole(), allowedEveryone, invertPermissions(allowedEveryone, false))
                    .complete();
            channels.add(participantsHangout);

            guild.getMembersWithRoles(participants).stream()
                    .filter(m -> m.getVoiceState().inVoiceChannel())
                    .forEach(m -> movePlayer(m, (VoiceChannel) participantsHangout));

            List<Permission> allowedParticipant = Collections.emptyList();
            guild.getVoiceChannels().stream().filter(vc -> vc.getName().equals("General")).findFirst().get()
                    .createPermissionOverride(participants).complete().getManagerUpdatable().grant(allowedParticipant).deny(invertPermissions(allowedParticipant, false))
                    .update().complete();

            LOCK.notifyAll();
        }
    }

    public void registerPlayer(String p) {
        guild.getController().addRolesToMember(getMemberByLink(p), participants).queue();

        if (nuz.getGameManager().isGameInProgress() && getMemberByLink(p).getVoiceState().inVoiceChannel()) {
            if (nuz.getGameManager().getScoreboard().getEntryTeam(p) != null) {
                VoiceChannel teamChannel = guild.getVoiceChannels().stream()
                        .filter(vc -> vc.getName().equals(nuz.getGameManager().getScoreboard().getEntryTeam(p).getName()))
                        .findFirst().orElse(null);
                if (teamChannel != null) {
                    guild.getController().moveVoiceMember(guild.getMember(nuz.getServerLinkManager().getLinkedUser(p)), teamChannel);
                }
            } else {
                guild.getController().moveVoiceMember(getMemberByLink(p), (VoiceChannel) participantsHangout);
            }
        }
    }

    void showHelp(MessageChannel c) {
        c.sendMessage("" +
                "This bot is directly connected with the UHC Minecraft server, and will mirror the actions on the server back to here.\n" +
                "Additionally, this bot is used as a registration mechanism to be able to enter the server using `+link <MCIGN>`." +
                "").queue();
    }

    public void start() {
        try {
            bot = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addListener(new DiscordEventListener(nuz))
                    .addListener(new DiscordChatListener(nuz))
                    .buildBlocking();

            running = true;
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        participants.delete().complete();
        bot.shutdown(false);

        running = false;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    void setCommandPrefix(String newPrefix) {
        setCommandPrefix(newPrefix, true);
    }

    public Guild getGuild() {
        return guild;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String newToken) {
        this.token = newToken;
    }

    public boolean isRunning() {
        return running;
    }

    public void setCommandPrefix(String newPrefix, boolean doSave) {
        this.commandPrefix = newPrefix;

        if (doSave) {
            nuz.getJSONManager().writeDiscordConfig();
        }
    }

    public enum Event {
        DEATH("Well, someone died."),
        JOIN(StringLib.DiscordBot$WelcomeMessage),
        QUIT(StringLib.DiscordBot$GoodbyeMessage),
        START(StringLib.DiscordBot$AnnounceStart),
        STOP(StringLib.DiscordBot$AnnounceStop),
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
}
