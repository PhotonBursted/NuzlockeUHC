package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Set;

/**
 * Controls effects concerning Ground type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Accustomed Hands"</td>
 *             <td>Haste 2. Permanently</td>
 *         </tr>
 *         <tr>
 *             <td>"Acrophobia"</td>
 *             <td>The lower the level, the more the elevation you are looking at will determine how nauseous you get.</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Ground extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Ground(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        pp = getPlayerPool(_TYPE);

        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p ->
                applyPotionEffect(Bukkit.getPlayer(p.getName()), new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1))
        );
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

                        // Apply nausea when looking at too high heights
                        if (player.getLocation().getY() - player.getTargetBlock((Set<Material>) null, 100).getY() > 10 + player.getLevel())
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.CONFUSION, 140, 0));
                    });
                }
        // Runs every second
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
