package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.Collections;

public class TaskManager {
    private final ArrayList<BukkitRunnable> tasks = new ArrayList<>();
    private EventMarkerAnnouncer ema;
    private GameCountdown gcd;
    private final Nuzlocke nuz;
    private ScoreboardUpdater sbu;

    public TaskManager(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public void cancelAll() {
        tasks.stream().forEach(BukkitRunnable::cancel);
    }

    private void register(BukkitRunnable... brs) {
        Collections.addAll(tasks, brs);
    }

    public void registerTasks() {
        register(
                ema = new EventMarkerAnnouncer(nuz),
                gcd = new GameCountdown(nuz, nuz.getSettings().getCountDownLength()),
                sbu = new ScoreboardUpdater(nuz)
        );
    }

    public EventMarkerAnnouncer getEMA() {
        return ema;
    }

    public ScoreboardUpdater getSBU() {
        return sbu;
    }

    public void startCountDown() {
        gcd.runTaskTimer(nuz, 0L, 20L);
    }

    public void startEventMarkers() {
        ema.runTaskTimer(nuz, 0L, 20L);
    }

    public void startScoreboardUpdater() {
        sbu.runTaskTimer(nuz, 0L, 20L);
    }
}
