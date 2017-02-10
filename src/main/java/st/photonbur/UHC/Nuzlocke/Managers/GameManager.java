package st.photonbur.UHC.Nuzlocke.Managers;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
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
    public int teamCapBonus;

    public GameManager(Nuzlocke nuz) {
        this.nuz = nuz;

        settings = new Settings(nuz.getConfig());
        settings.loadSettings();

        scoreboard = nuz.getServer().getScoreboardManager().getMainScoreboard();

        overworld = nuz.getServer().getWorlds().stream().filter(world -> world.getEnvironment() == World.Environment.NORMAL).findFirst().get();
    }

    public void cleanUp() {
        teamCapBonus = 0;
        nuz.getPlayerManager().removeClasses();
        getScoreboard().getTeams().forEach(Team::unregister);
        getScoreboard().getObjectives().forEach(Objective::unregister);

        nuz.getServer().getOnlinePlayers().forEach(p -> {
            p.teleport(new Location(getOverworld(), 0, getOverworld().getHighestBlockYAt(0, 0), 0));
            p.setGameMode(GameMode.SURVIVAL);
        });

        if (nuz.getTaskManager().getWB() != null) {
            nuz.getTaskManager().getWB().reset();
        }

        nuz.getTaskManager().cancelAll();
        nuz.getTeamManager().getTeams().clear();

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20d);
            p.setHealth(20d);
            p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
        });

        nuz.getServer().getWorlds().forEach(w -> {
            w.setGameRuleValue("naturalRegeneration", "true");
            w.setGameRuleValue("keepInventory", "true");
            w.setGameRuleValue("doDaylightCycle", "true");
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

    public int getTeamCap() {
        return teamCapBonus + settings.getTeamSize();
    }

    public void initGame() {
        nuz.getDiscordBot().announce(DiscordBot.Event.START);
        nuz.getServer().broadcastMessage(StringLib.GameManager$MatchStart);

        preparePlayers();
        nuz.getTaskManager().registerTasks();
        nuz.getTaskManager().startLauncher();
        if (getSettings().isWbEnabled()) {
            nuz.getTaskManager().getLauncher().startWorldBorder();
        }

        nuz.getDiscordBot().prepareGame();
        nuz.getPlayerManager().divideRoles();
        setPlayerEffects();
        preparePlayers();

        spreadPlayers();
        if (getSettings().getCountDownLength() > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    nuz.getTaskManager().getLauncher().startCountdown();
                }
            }.runTaskLater(nuz, 100L);
        }
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public boolean isTruceActive() {
        return truceActive;
    }

    private void preparePlayers() {
        for (st.photonbur.UHC.Nuzlocke.Entities.Player p : nuz.getPlayerManager().getPlayers().stream().filter(player -> player.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT).collect(Collectors.toList())) {
            Player player = Bukkit.getPlayer(p.getName());
            player.setSaturation(5);
            player.setFoodLevel(20);
            player.setHealth(20d);
            player.setGameMode(GameMode.SURVIVAL);
            player.giveExpLevels(-9999);
            player.getInventory().setContents(new ItemStack[36]);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 10));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 40));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 40));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -2));
        }

        for (st.photonbur.UHC.Nuzlocke.Entities.Player p : nuz.getPlayerManager().getPlayers().stream().filter(player -> player.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.SPECTATOR).collect(Collectors.toList())) {
            Bukkit.getPlayer(p.getName()).setGameMode(GameMode.SPECTATOR);
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

    private void spreadPlayers() {
        Biome[] blacklistBiomes = {Biome.DEEP_OCEAN, Biome.OCEAN, Biome.RIVER, Biome.FROZEN_OCEAN};

        nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT).forEach(p -> {
            int x = r.nextInt(nuz.getSettings().getWbInitialSize()) - (int) (0.5 * nuz.getSettings().getWbInitialSize());
            int z = r.nextInt(nuz.getSettings().getWbInitialSize()) - (int) (0.5 * nuz.getSettings().getWbInitialSize());
            int y = nuz.getGameManager().getOverworld().getHighestBlockYAt(x, z);

            if (Arrays.asList(blacklistBiomes).contains(nuz.getGameManager().getOverworld().getBlockAt(x, y, z).getBiome())
                    || nuz.getGameManager().getOverworld().getBlockAt(x, y, z).isLiquid())
                spreadPlayers();
            else Bukkit.getPlayer(p.getName()).teleport(new Location(nuz.getGameManager().getOverworld(), x, y + 2, z));
        });
    }

    public void startGame() {
        gameInProgress = true;
        setPlayerEffects();
        nuz.getEffectManager().giveTypeEffects();
        if (nuz.getSettings().getGentlemenDuration() > 0) {
            nuz.getTaskManager().getLauncher().startTruceRegulator();
        }
        if (nuz.getSettings().getEternalDaylight() > -1) {
            nuz.getTaskManager().getLauncher().startDaylightManager();
        }
        nuz.getServer().getOnlinePlayers().forEach(p -> Arrays.asList(Achievement.values()).forEach(a -> {
            if (p.hasAchievement(a)) {
                p.removeAchievement(a);
            }
        }));

        nuz.getServer().getWorlds().forEach(w -> {
            w.setFullTime(0L);
            w.setGameRuleValue("naturalRegeneration", "false");
            w.setGameRuleValue("keepInventory", "false");
            w.setDifficulty(Difficulty.HARD);
        });
    }

    public void stopGame() {
        gameInProgress = false;
        cleanUp();
        nuz.getDiscordBot().announce(DiscordBot.Event.STOP);
        nuz.getDiscordBot().cleanUp();
    }
}