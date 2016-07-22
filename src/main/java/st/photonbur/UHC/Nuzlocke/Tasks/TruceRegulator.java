package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class TruceRegulator extends BukkitRunnable {
    private Nuzlocke nuz;

    public TruceRegulator(Nuzlocke nuz) {
        this.nuz = nuz;
        nuz.getGameManager().setTruceActive(true);
    }

    @Override
    public void run() {
        nuz.getGameManager().setTruceActive(false);
    }
}
