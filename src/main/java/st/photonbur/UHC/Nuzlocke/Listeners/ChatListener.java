package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class ChatListener implements Listener {
    private final Nuzlocke nuz;

    public ChatListener(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    private void addPlayersBeing(PlayerState ps, AsyncPlayerChatEvent e) {
        switch (ps) {
            case ONLINE:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    e.getRecipients().add(p);
                }
                break;
            case TEAMMEMBER:
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(p -> nuz.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName()) != null)
                        .filter(p -> nuz.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName()).equals(nuz.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(e.getPlayer().getName())))
                        .forEach(e.getRecipients()::add);
                break;
            case SELF:
                e.getRecipients().add(e.getPlayer());
                break;
            case SPECTATOR:
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(p -> nuz.getPlayerManager().getPlayer(p).getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.SPECTATOR)
                        .forEach(e.getRecipients()::add);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Team team = nuz.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        String message = e.getMessage();
        String prefix = nuz.getSettings().getGlobalChatPrefix();

        if (nuz.getGameManager().isGameInProgress()) {
            e.getRecipients().clear();

            if (nuz.getPlayerManager().getPlayer(player).getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT) {
                if (message.startsWith(prefix)) {
                    addPlayersBeing(PlayerState.ONLINE, e);
                    setFormat(ChatMessageFormat.GLOBAL, e);
                } else {
                    addPlayersBeing(team == null ? PlayerState.SELF : PlayerState.TEAMMEMBER, e);
                    if (nuz.getSettings().doSpectatorSeeAll()) {
                        addPlayersBeing(PlayerState.SPECTATOR, e);
                    }
                    setFormat(ChatMessageFormat.TEAM, e);
                }
            } else {
                if (message.startsWith(prefix)) {
                    if (nuz.getSettings().doSpectatorGlobalTalk()) {
                        addPlayersBeing(PlayerState.ONLINE, e);
                        setFormat(ChatMessageFormat.GLOBAL, e);
                    } else {
                        addPlayersBeing(PlayerState.SELF, e);
                        e.setFormat(StringLib.ChatListener$SpectatorNotAllowed);
                    }
                } else {
                    addPlayersBeing(PlayerState.SPECTATOR, e);
                    setFormat(ChatMessageFormat.SPECTATOR, e);
                }
            }
        } else {
            setFormat(ChatMessageFormat.GLOBAL, e);
        }
    }

    private void setFormat(ChatMessageFormat cmf, AsyncPlayerChatEvent e) {
        String messageStart = "";
        Team team = nuz.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(e.getPlayer().getName());
        String prefix = "", suffix = "";
        if (team != null) {
            prefix = team.getPrefix();
            suffix = team.getSuffix();
        }

        switch (cmf) {
            case GLOBAL:
                messageStart += "[G]";
                break;
            case TEAM:
                messageStart += "[T]";
                break;
            case SPECTATOR:
                messageStart += "[S]";
                break;
            default:
        }

        String message = e.getMessage().substring(
                e.getMessage().startsWith(nuz.getSettings().getGlobalChatPrefix()) ? nuz.getSettings().getGlobalChatPrefix().length() : 0,
                e.getMessage().length()
        );
        if (message.length() == 0) {
            e.setCancelled(true);
        } else {
            e.setFormat(messageStart
                    + " <" + prefix + e.getPlayer().getName() + suffix + "> "
                    + message
            );
        }
    }

    public enum ChatMessageFormat {
        GLOBAL, TEAM, SPECTATOR
    }

    public enum PlayerState {
        ONLINE, TEAMMEMBER, SPECTATOR, SELF
    }
}