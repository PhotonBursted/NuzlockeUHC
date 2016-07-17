package st.photonbur.UHC.Nuzlocke.Listeners;

import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class PlayerConnectListener implements Listener {
    private final Nuzlocke nuz;

    public PlayerConnectListener (Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onPlayerConnect(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        User discordUser = nuz.getDiscordBot().get().getUsers().stream().filter(u -> u.getUsername().equals(p.getName())).findFirst().orElse(null);
        if(discordUser == null || discordUser.getOnlineStatus() != OnlineStatus.ONLINE) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You aren't logged in on the Discord server it seems! Make sure you are in order to join the fun :3");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(!nuz.getGameManager().isGameInProgress()) {
            nuz.getPlayerManager().registerPlayer(p.getName());
        } else {
            nuz.getPlayerManager().deregisterPlayer(p.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!nuz.getGameManager().isGameInProgress() || nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.SPECTATOR) {
            nuz.getPlayerManager().getPlayers().remove(
                    nuz.getPlayerManager().getPlayers().stream().filter(player -> player.getName().equals(p.getName())).findFirst().orElse(null)
            );
        }
    }
}
