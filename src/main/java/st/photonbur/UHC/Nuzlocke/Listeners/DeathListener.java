package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class DeathListener implements Listener {
    private final Nuzlocke nuz;

    public DeathListener(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(nuz.getGameManager().isGameInProgress()) {
            Player p = e.getEntity().getPlayer();

            if(nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.PARTICIPANT) {
                if(nuz.getSettings().getDeathHandleDelay() != -1) {
                    p.sendMessage(String.format(StringLib.DeathListener$DeathMove, nuz.getSettings().getDeathHandleDelay()));
                    nuz.getPlayerManager().getPlayer(p.getName()).setRole(Role.SPECTATOR);
                    new HandleDeadPlayer(nuz, p).runTaskLater(nuz, nuz.getSettings().getDeathHandleDelay() * 20L);
                }

                nuz.getTaskManager().getSBU().updateScores();
                nuz.getDiscordBot().announce(DiscordBot.Event.DEATH, e.getDeathMessage().replaceAll(".*§..*", ""));
            }
        }
    }

    private class HandleDeadPlayer extends BukkitRunnable {
        final Nuzlocke nuz;
        final Player p;

        public HandleDeadPlayer(Nuzlocke nuz, Player p) {
            this.nuz = nuz;
            this.p = p;
        }

        @Override
        public void run() {

        }
    }
}