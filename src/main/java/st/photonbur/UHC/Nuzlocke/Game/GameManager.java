package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class GameManager {
    private boolean gameInProgress = false;
    private boolean truceActive = true;
    private final Nuzlocke nuz;
    private final Scoreboard scoreboard;
    private final Settings settings;
    private World overworld;

    public GameManager(Nuzlocke nuz) {
        this.nuz = nuz;

        settings = new Settings(nuz.getConfig());
        settings.loadSettings();

        scoreboard = nuz.getServer().getScoreboardManager().getMainScoreboard();

        overworld = nuz.getServer().getWorlds().stream().filter(world -> world.getEnvironment() == World.Environment.NORMAL).findFirst().get();
    }

    public void cleanUp() {
        getScoreboard().getTeams().stream().forEach(Team::unregister);
        getScoreboard().getObjectives().stream().forEach(Objective::unregister);
        nuz.getTaskManager().getWB().reset();
        nuz.getTaskManager().cancelAll();
        nuz.getTeamManager().getTeams().clear();
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setMaxHealth(20d); p.setHealth(20d);
            p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
        });
    }

    public World getOverworld() {
        return overworld;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Settings getSettings() {
        return settings;
    }

    public void initGame() {
        nuz.getDiscordBot().announce(DiscordBot.Event.START);

        preparePlayers();
        nuz.getTaskManager().registerTasks();
        nuz.getTaskManager().startLauncher();
        if(getSettings().getEpisodeDuration() > 0) nuz.getTaskManager().getLauncher().startCountdown();
        if(getSettings().isWbEnabled()) nuz.getTaskManager().getLauncher().startWorldBorder();

        nuz.getDiscordBot().prepareGame();
        nuz.getPlayerManager().divideRoles();
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public boolean isTruceActive() { return truceActive; }

    private void preparePlayers() {
        nuz.getPlayerManager().removeClasses();
        for(Player player: nuz.getServer().getOnlinePlayers()) {
            player.setSaturation(5);
            player.setFoodLevel(10);
            player.setHealth(20);
            player.getInventory().clear();
            player.setTotalExperience(0);
        }
    }

    private void setPlayerEffects() {
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

    public void setTruceActive(boolean b) {
        truceActive = b;
    }

    public void startGame() {
        gameInProgress = true;
        nuz.getEffectManager().giveEffects();
        nuz.getTaskManager().getLauncher().startTruceRegulator();

        setPlayerEffects();
    }

    public void stopGame() {
        gameInProgress = false;
        cleanUp();
        nuz.getDiscordBot().cleanUp();
    }
}