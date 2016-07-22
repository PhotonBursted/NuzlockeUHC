package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class GameManager {
    private boolean gameInProgress = false;
    private boolean truceActive = true;
    private final Nuzlocke nuz;
    private final Random r = new Random();
    private final Scoreboard scoreboard;
    private final Settings settings;
    private final World overworld;

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
        nuz.getServer().getOnlinePlayers().stream().forEach(p -> {
            p.teleport(new Location(getOverworld(), 0, getOverworld().getHighestBlockYAt(0, 0), 0));
            p.setGameMode(GameMode.SURVIVAL);
        });
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
        nuz.getServer().broadcastMessage(StringLib.GameManager$MatchStart);

        preparePlayers();
        nuz.getTaskManager().registerTasks();
        nuz.getTaskManager().startLauncher();
        if(getSettings().getEpisodeDuration() > 0) nuz.getTaskManager().getLauncher().startCountdown();
        if(getSettings().isWbEnabled()) nuz.getTaskManager().getLauncher().startWorldBorder();

        nuz.getDiscordBot().prepareGame();
        nuz.getPlayerManager().divideRoles();
        setPlayerEffects();

        spreadPlayers();
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public boolean isTruceActive() { return truceActive; }

    private void preparePlayers() {
        nuz.getPlayerManager().removeClasses();
        for(st.photonbur.UHC.Nuzlocke.Entities.Player p: nuz.getPlayerManager().getPlayers().stream().filter(player -> player.getRole() == Role.PARTICIPANT).collect(Collectors.toList())) {
            Player player = Bukkit.getPlayer(p.getName());
            player.setSaturation(5);
            player.setFoodLevel(20);
            player.setHealth(20d);
            player.setGameMode(GameMode.SURVIVAL);
            player.setTotalExperience(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getSettings().getCountDownLength() * 20, 10));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, getSettings().getCountDownLength() * 20, 40));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, getSettings().getCountDownLength() * 20, 40));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, getSettings().getCountDownLength() * 20, -2));
        }
        for(st.photonbur.UHC.Nuzlocke.Entities.Player p: nuz.getPlayerManager().getPlayers().stream().filter(player -> player.getRole() == Role.SPECTATOR).collect(Collectors.toList())) {
            Player player = Bukkit.getPlayer(p.getName());
            player.setGameMode(GameMode.SPECTATOR);
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getSettings().getResistanceLength() * 20 + getSettings().getCountDownLength() * 20, 10, true));
        });
    }

    public void setTruceActive(boolean b) {
        truceActive = b;
    }

    private void spreadPlayers() {
        Biome[] blacklistBiomes = {Biome.DEEP_OCEAN, Biome.OCEAN, Biome.RIVER, Biome.FROZEN_OCEAN};

        nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == Role.PARTICIPANT).forEach(p -> {
            int x = r.nextInt(nuz.getSettings().getWbInitialSize()) - (int) (0.5 * nuz.getSettings().getWbInitialSize());
            int z = r.nextInt(nuz.getSettings().getWbInitialSize()) - (int) (0.5 * nuz.getSettings().getWbInitialSize());
            int y = nuz.getGameManager().getOverworld().getHighestBlockYAt(x, z);

            if(Arrays.asList(blacklistBiomes).contains(nuz.getGameManager().getOverworld().getBlockAt(x, y, z).getBiome())
                    || nuz.getGameManager().getOverworld().getBlockAt(x, y, z).isLiquid())
                spreadPlayers();
            else Bukkit.getPlayer(p.getName()).teleport(new Location(nuz.getGameManager().getOverworld(), x, y + 2, z));
        });
    }

    public void startGame() {
        gameInProgress = true;
        nuz.getEffectManager().giveEffects();
        if(nuz.getSettings().getGentlemenDuration() > 0) nuz.getTaskManager().getLauncher().startTruceRegulator();
        if(nuz.getSettings().getEternalDaylight() > -1) nuz.getTaskManager().getLauncher().startDaylightManager();

        nuz.getServer().getWorlds().forEach(w -> {
            w.setTime(0);
            w.setGameRuleValue("naturalRegeneration", "false");
            w.setGameRuleValue("keepInventory", "false");
            w.setDifficulty(Difficulty.HARD);
        });
    }

    public void stopGame() {
        gameInProgress = false;
        cleanUp();
        nuz.getDiscordBot().cleanUp();
    }
}