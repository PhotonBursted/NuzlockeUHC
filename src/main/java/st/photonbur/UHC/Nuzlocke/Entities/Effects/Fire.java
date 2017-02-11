package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Fire type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Molten Pick Forger"</td>
 *             <td>Smelts every block mined. Even wood turns into charcoal</td>
 *         </tr>
 *         <tr>
 *             <td>"Hot Feet"</td>
 *             <td>Melts ice and snow upon which is walked</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Fire extends Type implements Listener {
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

    Fire(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Activates when a block gets broken
     *
     * @param e The event which gets triggered by breaking a block
     */
    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        // Retrieve the player breaking a block
        st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(e.getPlayer());
        // Refresh the player pool
        pp = getPlayerPool(_TYPE);

        // Check if the player is an actual participant
        if (p.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT) {
            // Check if the player is a Pokémon
            if (p instanceof Pokemon) {
                // Check if the player pool contains the player breaking the block
                if (pp.contains(p)) {
                    // Give the player a certain chance to multiply their mining output
                    if (r.nextDouble() <= .2) {
                        // Cancel the block break event
                        e.setCancelled(true);

                        // Get the locatoin of the broken block
                        Block b = e.getBlock();
                        // Get the type of the broken block and drop their smelted components
                        switch (b.getType()) {
                            case GOLD_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
                                break;
                            case IRON_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
                                break;
                            case LOG:
                            case LOG_2:
                                b.getWorld().dropItemNaturally(b.getLocation(), new Coal(CoalType.CHARCOAL).toItemStack());
                                break;
                            case STONE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STONE));
                                break;
                        }

                        // Emulate breaking the block
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, 0.8f, 1);
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.5f, 1);
                        // Actually shows the block particles!
                        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
                        b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                        b.setType(Material.AIR);
                        e.setExpToDrop(e.getExpToDrop());
                    }
                }
            }
        }
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
                        // Get the block underneath the player
                        Block b = player.getLocation().subtract(0, 1, 0).getBlock();

                        // Switch the type of the block underneath the player to water if it's a cold block
                        switch (b.getType()) {
                            case SNOW_BLOCK:
                            case SNOW:
                            case ICE:
                            case FROSTED_ICE:
                            case PACKED_ICE:
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        b.setType(Material.WATER);
                                // Runs 2 seconds later
                                    }
                                }.runTaskLater(nuz, 40L);
                        }
                    });
                }
        // Runs every tick
            }
        }.runTaskTimer(nuz, 0, 1L);
    }
}
