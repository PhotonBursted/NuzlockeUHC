package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class DeathListener implements Listener {
    Nuzlocke nuz;

    public DeathListener(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        if(nuz.getSettings().getDeathHandleDelay() != -1) {
            p.sendMessage("" + ChatColor.RED + "[!] You will be moved over to the spectator channel within " + nuz.getSettings().getDeathHandleDelay() + " seconds");
            nuz.getPlayerManager().getPlayer(p.getName()).setRole(Role.SPECTATOR);
            new HandleDeadPlayer(nuz, p).runTaskLater(nuz, nuz.getSettings().getDeathHandleDelay() * 20L);
        }

        nuz.getTaskManager().getSBU().updateScores();
    }

    private class HandleDeadPlayer extends BukkitRunnable {
        Nuzlocke nuz;
        Player p;

        public HandleDeadPlayer(Nuzlocke nuz, Player p) {
            this.nuz = nuz;
            this.p = p;
        }

        @Override
        public void run() {

        }
    }
}