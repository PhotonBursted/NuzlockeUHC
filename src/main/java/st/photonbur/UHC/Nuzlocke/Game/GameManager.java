package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.Tasks.EventMarkerAnnouncer;
import st.photonbur.UHC.Nuzlocke.Tasks.GameCountdown;
import st.photonbur.UHC.Nuzlocke.Tasks.ScoreboardUpdater;

public class GameManager {
    private boolean gameInProgress = false;
    EventMarkerAnnouncer ema;
    GameCountdown gcd;
    Nuzlocke nuz;
    Scoreboard scoreboard;
    ScoreboardUpdater sbu;
    Settings settings;


    public GameManager(Nuzlocke nuz) {
        this.nuz = nuz;

        settings = new Settings(nuz.getConfig());
        settings.loadSettings();

        scoreboard = nuz.getServer().getScoreboardManager().getMainScoreboard();
    }

    public void cleanUp() {
        getScoreboard().getTeams().stream().forEach(Team::unregister);
        getScoreboard().getObjectives().stream().forEach(Objective::unregister);
        nuz.getTaskManager().cancelAll();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Settings getSettings() {
        return settings;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    void preparePlayers() {
        for(Player player: nuz.getServer().getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10, 10, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 20, true));
        }
    }

    public void setPlayerEffects() {
        nuz.getServer().getOnlinePlayers()
                .stream()
                .filter(player -> nuz.getPlayerManager().getPlayers().stream().anyMatch(p -> player.getName().equals(p.getName())))
                .forEach(player -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getSettings().getResistanceLength() * 20, 10, true));
        });
    }

    public void initGame() {
        gameInProgress = true;

        preparePlayers();
        nuz.getTaskManager().registerTasks();
        nuz.getTaskManager().startCountDown();
    }

    public void startGame() {
        setPlayerEffects();
        nuz.getPlayerManager().divideRoles();
        nuz.getTaskManager().startEventMarkers();
        nuz.getTaskManager().startScoreboardUpdater();
    }

    public void stopGame() {
        gameInProgress = false;
        cleanUp();
    }
}