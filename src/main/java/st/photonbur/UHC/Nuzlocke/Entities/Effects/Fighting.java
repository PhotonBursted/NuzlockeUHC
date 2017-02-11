package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;

/**
 * Controls effects concerning Fighting type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Bare Fists"</td>
 *             <td>Haste and strength when unarmed</td>
 *         </tr>
 *         <tr>
 *             <td>"Fortification"</td>
 *             <td>Carrying 6 stacks of (cobble)stone in total gives permanent strength, resistance and slowness</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Fighting extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Fighting(Nuzlocke nuz) {
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

                        // If the hands don't contain any items and foodlevels are above 6.5 haunches, give haste and strength
                        if (player.getInventory().getItemInMainHand().getAmount() == 0 &&
                                player.getInventory().getItemInOffHand().getAmount() == 0 &&
                                player.getFoodLevel() > 13) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                        } else {
                            removePotionEffect(player, PotionEffectType.FAST_DIGGING);
                            removePotionEffect(player, PotionEffectType.INCREASE_DAMAGE);
                        }
                    });
                }
        // Runs every 0.5 seconds
            }
        }.runTaskTimer(nuz, 0L, 10L);
    }
}
