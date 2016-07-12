package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.Bukkit;
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
    private long time = 0;
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
        buffer.values().stream().forEach(scoreboard::resetScores);
    }

    private void displayScores() {
        for(int i=0; i < entries.size(); i++) {
            eventInfo.getScore(entries.get(entries.keySet().toArray(new String[entries.size()])[i])).setScore(entries.size()-i-1);
        }
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

        time++;
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
        entries.replace("playersLeft", "Players left:    " + ChatColor.BOLD + (int) nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == Role.PARTICIPANT).count());
        entries.replace("teamsLeft", "Teams left:      " + ChatColor.BOLD + (int) (nuz.getTeamManager().getTeams().stream().filter(team -> team.countStillAlive() > 0).count()
                                                                                 + Bukkit.getOnlinePlayers().stream().filter(p -> scoreboard.getEntryTeam(p.getName()) == null).count()));
        entries.replace("episode", "     Episode "+ ChatColor.BOLD + nuz.getTaskManager().getEMA().getEpisodeNo());
        entries.replace("timeLeft", "       .: "+ nuz.getTaskManager().getEMA().getEpisodeTimeLeft() +" :.");

        clearScores();
        displayScores();
    }
}