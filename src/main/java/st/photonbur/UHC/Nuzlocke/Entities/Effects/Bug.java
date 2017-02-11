package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Bug type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Superfood"</td>
 *             <td>
 *                 Being surrounded by 16 or more leaves will slowly regenerate health and food.<br>
 *                 Leaves around the player will decay faster than usual.
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>"Squishy"</td>
 *             <td>Permanent maximum health deficiency of 2 hearts</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Bug extends Type {
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

    Bug(Nuzlocke nuz) {
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

        // Set all the online player's maximum health to 16 (8 hearts)
        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
            Bukkit.getPlayer(p.getName()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(16d);
            if (startup) {
                Bukkit.getPlayer(p.getName()).setHealth(16d);
            }
        });
    }

    /**
     * Applies effects which have to be running or checked continuously
     */
    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            int time = 0;

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
                        int leafcount = 0;

                        // Search in all the blocks directly around the player for leaf blocks
                        for (int x = l.getBlockX() - 1; x < l.getBlockX() + 2; x++) {
                            for (int y = l.getBlockY() - 1; y < l.getBlockY() + 3; y++) {
                                for (int z = l.getBlockZ() - 1; z < l.getBlockZ() + 2; z++) {
                                    if (l.getWorld().getBlockAt(x, y, z).getType() == Material.LEAVES ||
                                            l.getWorld().getBlockAt(x, y, z).getType() == Material.LEAVES_2) {
                                        leafcount++;

                                        // Add an additional chance of the leaf naturally decaying.
                                        if (r.nextDouble() <= 0.0085) {
                                            l.getWorld().getBlockAt(x, y, z).breakNaturally();
                                        }
                                    }
                                }
                            }
                        }

                        // If there's enough leaves around the player, heal them and add a little bit of food.
                        if (leafcount >= 16 && time % 15 == 0) {
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
                            player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + .5d));
                        }
                    });
                    time++;
                }
            }
        // Run every second
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
