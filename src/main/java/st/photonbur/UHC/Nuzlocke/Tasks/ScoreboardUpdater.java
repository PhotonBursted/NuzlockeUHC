package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScoreboardUpdater extends BukkitRunnable {
    private Map<String, String> buffer = new LinkedHashMap<>();
    private final Map<String, String> entries = new LinkedHashMap<>();
    private Objective eventInfo;
    private final Nuzlocke nuz;
    private final Scoreboard scoreboard;

    public ScoreboardUpdater(Nuzlocke nuz) {
        this.nuz = nuz;
        this.scoreboard = nuz.getGameManager().getScoreboard();

        launchSidebar();
        setupEntries("playersLeft", "teamsLeft", "separator|---------------", "episode", "timeLeft");
    }

    private void clearScores() {
        buffer.values().forEach(scoreboard::resetScores);
    }

    private void displayScores() {
        for(int i=0; i < entries.size(); i++) {
            eventInfo.getScore(entries.get(entries.keySet().toArray(new String[entries.size()])[i])).setScore(entries.size()-i-1);
        }
    }

    private String getEpisodeTimeLeft() {
        long time = nuz.getTaskManager().getEMA().getRawTime();
        int min, sec;
        min = (int) (nuz.getSettings().getEpisodeDuration() - (time / 60) % nuz.getSettings().getEpisodeDuration() - 1);
        sec = (int) (59 - ((time - 1) % 60));
        return (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
    }


    private void launchSidebar() {
        eventInfo = scoreboard.registerNewObjective("eventInfo", "dummy");
        eventInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
        eventInfo.setDisplayName(nuz.getSettings().getEventName());
    }

    @Override
    public void run() {
        buffer = new LinkedHashMap<>(entries);

        updateScores();
    }

    private void setupEntries(String... scores) {
        for(String s: scores) {
            if(s.contains("|")) {
                String[] args = s.split("\\|");
                entries.put(args[0], args[1]);
            } else {
                entries.put(s, "");
            }
        }
    }

    public void updateScores() {
        entries.replace("playersLeft", "Players left: " + ChatColor.BOLD + (int) nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == Role.PARTICIPANT).count());
        entries.replace("teamsLeft", "Teams left: " + ChatColor.BOLD + nuz.getTeamManager().teamsAliveCount());
        entries.replace("episode", "     Episode "+ ChatColor.BOLD + nuz.getTaskManager().getEMA().getEpisodeNo());
        entries.replace("timeLeft", "       .: "+ getEpisodeTimeLeft() +" :.");

        clearScores();
        displayScores();
    }
}