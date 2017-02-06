package st.photonbur.UHC.Nuzlocke.Listeners;

import net.dv8tion.jda.core.entities.User;
import org.bukkit.GameMode;
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

    public PlayerConnectListener(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onPlayerConnect(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        User discordUser = nuz.getServerLinkManager().getLinkedUser(p.getName());
        if (discordUser == null) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You haven't linked your Discord name to your Minecraft account yet!");
        } else if (nuz.getDiscordBot().getGuild().getMember(discordUser) == null) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You aren't logged in on the Discord server! Make sure you are to join in on the fun :3");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (nuz.getPlayerManager().getPlayers().stream().noneMatch(player -> player.getName().equals(p.getName()))) {
            if (!nuz.getGameManager().isGameInProgress()) {
                nuz.getPlayerManager().registerPlayer(p.getName());
                p.setGameMode(GameMode.SURVIVAL);
            } else {
                nuz.getPlayerManager().deregisterPlayer(p.getName());
                p.setGameMode(GameMode.SPECTATOR);
            }
        } else {
            if (nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.SPECTATOR)
                p.setGameMode(GameMode.SPECTATOR);
            else {
                p.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!nuz.getGameManager().isGameInProgress() || nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.SPECTATOR) {
            nuz.getPlayerManager().getPlayers().remove(
                    nuz.getPlayerManager().getPlayers().stream().filter(player -> player.getName().equals(p.getName())).findFirst().orElse(null)
            );
        }
    }
}
