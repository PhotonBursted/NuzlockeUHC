package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
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
 * Controls effects concerning Steel type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Steel Skin"</td>
 *             <td>Slight permanent resistance and a maximum of 11 hearts</td>
 *         </tr>
 *         <tr>
 *             <td>"Rusty"</td>
 *             <td>Being in water or rain for too long inflicts slowness</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Steel extends Type {
    /**
     * Holds the amount of water all players have encountered.
     */
    private final HashMap<Player, Double> water = new HashMap<>();
    /**
     * Holds all players which have been weakened as a cause of Rusty.
     */
    private final HashMap<Player, Boolean> rust = new HashMap<>();
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Steel(Nuzlocke nuz) {
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

            applyPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, true, false));
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(22d);
            if (startup) {
                player.setHealth(22d);
            }
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
                    rust.clear();
                    water.clear();
                } else {
                    // Get all the online players in the type's player pool
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();

                        // Calculate the amount of water the Pokémon has suffered from
                        if (water.containsKey(player)) {
                            water.replace(player,
                                    Math.min(Math.max(-600, (l.getBlock().isLiquid() ||
                                            (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) ?
                                            (water.get(player) + 3) : (water.get(player) - 10)), 2000));
                        } else {
                            water.put(player, 0d);
                        }
                        // If a threshold has been met, send a message and apply the slowness effect
                        if (water.get(player) >= 1750) {
                            if (!rust.getOrDefault(player, false)) player.sendMessage(StringLib.Steel$Rusty);
                            rust.replace(player, true);
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                        } else if (rust.containsKey(player)) {
                            // If the player was already weak, but the threshold isn't met anymore, remove the effect
                            rust.replace(player, false);
                            removePotionEffect(player, PotionEffectType.SLOW);
                        } else {
                            rust.put(player, false);
                        }
                    });
                }
        // Run every second
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
