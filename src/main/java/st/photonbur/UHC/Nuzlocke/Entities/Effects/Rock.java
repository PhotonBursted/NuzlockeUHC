package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Rock type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Fortune"</td>
 *             <td>20% chance of increasing mining drops</td>
 *         </tr>
 *         <tr>
 *             <td>"Fortification"</td>
 *             <td>Carrying 6 stacks of (cobble)stone in total gives permanent strength, resistance and slowness</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Rock extends Type implements Listener {
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

    Rock(Nuzlocke nuz) {
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

                        // Get the location of the broken block
                        Block b = e.getBlock();
                        // Get the type of the broken block, then drop its drops as an itemstack
                        switch (b.getType()) {
                            case GOLD_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT, r.nextInt(3) + 1));
                                break;
                            case IRON_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT, r.nextInt(3) + 1));
                                break;
                            case DIAMOND_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.DIAMOND, r.nextInt(3) + 1));
                                break;
                            case LAPIS_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.INK_SACK, r.nextInt(9) + 4, (short) 4));
                                break;
                            case REDSTONE_ORE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.REDSTONE, r.nextInt(5) + 6));
                                break;
                            case STONE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STONE, r.nextInt(3) + 1));
                        }

                        // Emulate breaking the block
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, 0.8f, 1);
                        // Actually shows the block particles!
                        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
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
                        int stoneAmount = 0;

                        // Check the player's inventory for stone and count it all
                        for (ItemStack inventorySlot : player.getInventory().getContents()) {
                            if (inventorySlot != null) {
                                if (inventorySlot.getType() == Material.STONE || inventorySlot.getType() == Material.COBBLESTONE) {
                                    stoneAmount += inventorySlot.getAmount();
                                }
                            }
                        }

                        // If enough stone is present, apply all the potion effects
                        if (stoneAmount >= 6 * 64) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                        } else {
                            removePotionEffect(player, PotionEffectType.INCREASE_DAMAGE);
                            removePotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
                            removePotionEffect(player, PotionEffectType.SLOW);
                        }
                    });
                }
        // Run every 10 seconds
            }
        // Runs the check every 10 seconds
        }.runTaskTimer(nuz, 0L, 200L);
    }
}
