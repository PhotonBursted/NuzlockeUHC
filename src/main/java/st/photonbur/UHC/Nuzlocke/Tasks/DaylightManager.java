package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

class DaylightManager extends BukkitRunnable {
    private final Nuzlocke nuz;

    public DaylightManager(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public void run() {
        nuz.getServer().getWorlds().forEach(w -> w.setGameRuleValue("doDaylightCycle", "false"));
    }
}
