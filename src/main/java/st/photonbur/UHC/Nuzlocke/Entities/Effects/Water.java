package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;
import java.util.List;

/**
 * Controls effects concerning Water type players.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Water Addiction"</td>
 *             <td>Needs to be inside of blocks of water at times to rehydrate. Suffers from hunger otherwise</td>
 *         </tr>
 *         <tr>
 *             <td>"Gills"</td>
 *             <td>Breathes water. Literally</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Water extends Type {
    /**
     * Holds the amount of water all players have encountered.
     */
    private final HashMap<Player, Double> water = new HashMap<>();
    /**
     * Holds all players which have been weakened as a cause of Endless Thirst.
     */
    private final HashMap<Player, Boolean> weak = new HashMap<>();
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Water(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Gives one-shot effects specific to the Pokémon type
     *
     * @param startup Specifies if the effect was applied on startup
     */
    @Override
    void giveInitialEffects(boolean startup) {
        // Refresh the player pool
        pp = getPlayerPool(_TYPE);

        // Set all the online player's maximum health to 22 (11 hearts) and give resistance
        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
            Player player = Bukkit.getPlayer(p.getName());

            applyPotionEffect(player, new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
        });
    }

    /**
     * Applies effects which have to be running or checked continuously
     */
    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Refresh the player pool
                pp = getPlayerPool(_TYPE);

                // If the player pool is 0 or the game isn't in progress, cancel the timers
                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() || !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    // Get all the online players in the type's player pool
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();

                        // If in water or in the rain, get haste and speed
                        if (l.getBlock().getType() == Material.WATER || l.getBlock().getType() == Material.STATIONARY_WATER ||
                                (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                        } else {
                            removePotionEffect(player, PotionEffectType.FAST_DIGGING);
                            removePotionEffect(player, PotionEffectType.SPEED);
                        }

                        // Calculate the amount of thirst the player has
                        if (water.containsKey(player)) {
                            water.replace(player,
                                    Math.min(Math.max(-600, (l.getBlock().isLiquid() ||
                                            (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) ?
                                            (water.get(player) - 30) : (water.get(player) + 3)), 2000));
                        } else {
                            water.put(player, 0d);
                        }

                        // If a threshold has been met, send a message and apply the hunger effect
                        if (water.get(player) >= 1750) {
                            if (!weak.getOrDefault(player, false)) player.sendMessage(StringLib.Water$Dehydrated);
                            weak.replace(player, true);
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 0));
                        } else if (weak.containsKey(player)) {
                            // If the player was already weak, but the threshold isn't met anymore, remove the effect
                            weak.replace(player, false);
                            removePotionEffect(player, PotionEffectType.HUNGER);
                        } else {
                            weak.put(player, false);
                        }
                    });
                }
            // Run every second
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
