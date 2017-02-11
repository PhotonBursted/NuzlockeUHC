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

import java.util.List;

/**
 * Controls effects concerning Grass type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Chameleon"</td>
 *             <td>Hiding in grass grants invisibility</td>
 *         </tr>
 *         <tr>
 *             <td>"Shooting Roots"</td>
 *             <td>Hiding in grass grants invisibility</td>
 *         </tr>
 *         <tr>
 *             <td>"Chloroblade"</td>
 *             <td>Sword attacks are a bit weaker than usual. The thought of seeing relatives be cut lingers in your head...</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Grass extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Grass(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Gives one-shot effects specific to the Pokémon type
     *
     * @param startup Specifies if the effect was applied on startup
     */
    @Override
    void giveInitialEffects(boolean startup) {
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
                        // Get the player's location
                        Location l = player.getLocation();

                        // Get the block the player's in
                        if (l.getBlock().getType() == Material.DOUBLE_PLANT) {
                            // Double plant? Invisibility
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                        } else {
                            removePotionEffect(player, PotionEffectType.INVISIBILITY);
                        }

                        if (l.getBlock().getType() == Material.WATER || l.getBlock().getType() == Material.STATIONARY_WATER) {
                            // Water? Slowness and absorption
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 0));
                        } else {
                            removePotionEffect(player, PotionEffectType.SLOW);
                            removePotionEffect(player, PotionEffectType.ABSORPTION);
                        }

                        // If there's a sword in the main hand, apply weakness
                        if (player.getInventory().getItemInMainHand().getType() == Material.WOOD_SWORD ||
                                player.getInventory().getItemInOffHand().getType() == Material.WOOD_SWORD ||
                                player.getInventory().getItemInMainHand().getType() == Material.STONE_SWORD ||
                                player.getInventory().getItemInOffHand().getType() == Material.STONE_SWORD ||
                                player.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD ||
                                player.getInventory().getItemInOffHand().getType() == Material.IRON_SWORD ||
                                player.getInventory().getItemInMainHand().getType() == Material.GOLD_SWORD ||
                                player.getInventory().getItemInOffHand().getType() == Material.GOLD_SWORD ||
                                player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD ||
                                player.getInventory().getItemInOffHand().getType() == Material.DIAMOND_SWORD) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
                        } else {
                            removePotionEffect(player, PotionEffectType.WEAKNESS);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 1L);
    }
}
