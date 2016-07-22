package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.Collections;

public class TaskManager {
    private final ArrayList<BukkitRunnable> tasks = new ArrayList<>();
    private DaylightManager dlm;
    private EventMarkerAnnouncer ema;
    private GameCountdown gcd;
    private Launcher launcher;
    private final Nuzlocke nuz;
    private ScoreboardUpdater sbu;
    private TruceRegulator tr;
    private Worldborder wb;

    public TaskManager(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public void cancelAll() {
        tasks.stream().filter(t -> Bukkit.getScheduler().isCurrentlyRunning(t.getTaskId())).forEach(BukkitRunnable::cancel);
    }

    private void register(BukkitRunnable... brs) {
        Collections.addAll(tasks, brs);
    }

    public void registerTasks() {
        register(
                dlm = new DaylightManager(nuz),
                ema = new EventMarkerAnnouncer(nuz),
                gcd = new GameCountdown(nuz, nuz.getSettings().getCountDownLength()),
                launcher = new Launcher(),
                sbu = new ScoreboardUpdater(nuz),
                tr = new TruceRegulator(nuz),
                wb = new Worldborder(nuz)
        );
    }

    public EventMarkerAnnouncer getEMA() {
        return ema;
    }

    public ScoreboardUpdater getSBU() {
        return sbu;
    }

    public Worldborder getWB() {
        return wb;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public void startLauncher() {
        launcher.runTaskTimerAsynchronously(nuz, 0L, 20L);
    }

    public class Launcher extends BukkitRunnable {
        private boolean startCountdown = false;
        private boolean startDaylightManager = false;
        private boolean startEventMarkers = false;
        private boolean startScoreboardUpdater = false;
        private boolean startTruceRegulator = false;
        private boolean startWorldBorder = false;

        @Override
        public void run() {
            if(startCountdown) {
                gcd.runTaskTimer(nuz, 0L, 20L);
                nuz.getLogger().info("Starting countdown...");
                startCountdown = false;
            }
            if(startDaylightManager) {
                dlm.runTaskLaterAsynchronously(nuz, nuz.getSettings().getEternalDaylight() * 60L * 20L * 20L);
                nuz.getLogger().info("Starting eternal daylight timer...");
                startDaylightManager = false;
            }
            if(startEventMarkers) {
                ema.runTaskTimerAsynchronously(nuz, 0L, 20L);
                nuz.getLogger().info("Starting event markers...");
                startEventMarkers = false;
            }
            if(startScoreboardUpdater) {
                sbu.runTaskTimerAsynchronously(nuz, 2L, 20L);
                nuz.getLogger().info("Starting scoreboard integration...");
                startScoreboardUpdater = false;
            }
            if(startTruceRegulator) {
                tr.runTaskLaterAsynchronously(nuz, nuz.getSettings().getGentlemenDuration() * 60L * 20L);
                nuz.getLogger().info("Starting truce task...");
                startTruceRegulator = false;
            }
            if(startWorldBorder) {
                wb.runTaskTimerAsynchronously(nuz, 0L, 20L);
                nuz.getLogger().info("Starting worldborder...");
                startWorldBorder = false;
            }
        }

        public void startCountdown() {
            startCountdown = true;
        }
        public void startDaylightManager() { startDaylightManager = true; }
        public void startEventMarkers() {
            startEventMarkers = true;
        }
        public void startScoreboardUpdater() {
            startScoreboardUpdater = true;
        }
        public void startTruceRegulator() {
            startTruceRegulator = true;
        }
        public void startWorldBorder() {
            startWorldBorder = true;
        }
    }
}
