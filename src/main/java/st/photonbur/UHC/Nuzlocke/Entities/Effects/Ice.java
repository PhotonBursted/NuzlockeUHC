package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls effects concerning Ice type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Hothead"</td>
 *             <td>Desert biomes will slow and weaken</td>
 *         </tr>
 *         <tr>
 *             <td>"Frost Walker"</td>
 *             <td>Creates frosted ice where the player is walking, even without boots</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Ice extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Ice(Nuzlocke nuz) {
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
                        Location l = player.getLocation();

                        // Check if the block is in a warm biome
                        switch (l.getBlock().getBiome()) {
                            case DESERT:
                            case DESERT_HILLS:
                            case HELL:
                            case MESA:
                            case MESA_CLEAR_ROCK:
                            case MESA_ROCK:
                            case MUTATED_DESERT:
                            case MUTATED_MESA:
                            case MUTATED_MESA_CLEAR_ROCK:
                            case MUTATED_MESA_ROCK:
                            case MUTATED_SAVANNA:
                            case MUTATED_SAVANNA_ROCK:
                            case SAVANNA:
                            case SAVANNA_ROCK:
                                // If so, apply slowness and weakness
                                applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                                applyPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
                                break;
                            default:
                                removePotionEffect(player, PotionEffectType.SLOW);
                                removePotionEffect(player, PotionEffectType.WEAKNESS);
                        }

                        // Initialize a list of frozen blocks
                        ArrayList<Block> frozenBlocks = new ArrayList<>(0);

                        // Create a circle of frosted ice around the player
                        for (int x = l.getBlockX() - 2; x <= l.getBlockX() + 2; x++) {
                            for (int z = l.getBlockZ() - 2; z <= l.getBlockZ() + 2; z++) {
                                Block b = l.subtract(0, 1, 0).getBlock();
                                if ((b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.FROSTED_ICE)
                                        && b.getWorld().getBlockAt(x, l.getBlockY(), z).getType() == Material.AIR &&
                                        Math.sqrt(Math.pow(x - l.getBlockX(), 2) + Math.pow(z - l.getBlockZ(), 2)) < 2.25
                                        ) {
                                    frozenBlocks.add(b);
                                }
                            }
                        }

                        // Set the blocks in the ArrayList to frosted ice, then clear it
                        frozenBlocks.forEach(b -> b.setType(Material.FROSTED_ICE));
                    });
                }
        // Run every tick
            }
        }.runTaskTimer(nuz, 0L, 1L);
    }
}
