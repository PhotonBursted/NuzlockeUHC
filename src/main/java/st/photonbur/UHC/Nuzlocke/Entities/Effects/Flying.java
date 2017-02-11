package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Flying type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Feathers Falling"</td>
 *             <td>Makes feathers fall onto the ground randomly, leaving a breadcrumb trail of future arrows</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Flying extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * The randomizer instance
     */
    private final Random r = new Random();
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Flying(Nuzlocke nuz) {
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
                        // Drop a feather every once in a while
                        if (r.nextDouble() <= 0.1) {
                            Location l = nuz.getServer().getPlayer(p.getName()).getLocation();
                            l.getWorld().dropItemNaturally(l, new ItemStack(Material.FEATHER, r.nextInt(3) + 1));
                        }
                    });
                }
        // Runs every 15 seconds
            }
        }.runTaskTimer(nuz, 0L, 300L);
    }
}
