package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

class Rock extends Type implements Listener {
    private final int[] stoneAmount = {0};
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private final Random r = new Random();
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Rock(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(e.getPlayer());
        if (p.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT) {
            if (p instanceof Pokemon) {
                if (p.getType() == Pokemon.Type.ROCK) {
                    if (r.nextDouble() <= .2) {
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, 0.8f, 1);
                        e.setCancelled(true);
                        Block b = e.getBlock();

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
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.REDSTONE, r.nextInt(6) + 5));
                                break;
                            case STONE:
                                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STONE));
                        }

                        b.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    void giveInitialEffects(boolean startup) {
    }

    @Override
    boolean hasEvent() {
        return true;
    }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                pp = getPlayerPool(_TYPE);

                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() || !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        stoneAmount[0] = 0;
                        Arrays.stream(player.getInventory().getContents())
                                .filter(Objects::nonNull)
                                .filter(slot -> slot.getType() == Material.STONE || slot.getType() == Material.COBBLESTONE)
                                .forEach(slot -> stoneAmount[0] += slot.getAmount());
                        if (stoneAmount[0] >= 6 * 64) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                        } else {
                            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            player.removePotionEffect(PotionEffectType.SLOW);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 200L);
    }
}
