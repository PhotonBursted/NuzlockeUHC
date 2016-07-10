package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class PlayerConnectListener implements Listener {
    Nuzlocke nuz;

    public PlayerConnectListener (Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(nuz.getPlayerManager().getPlayer(p) == null) {
            if(!nuz.getGameManager().isGameInProgress()) {
                nuz.getPlayerManager().registerPlayer(p.getName());
            } else {
                nuz.getPlayerManager().unregisterPlayer(p.getName());
            }
        }
    }
}
