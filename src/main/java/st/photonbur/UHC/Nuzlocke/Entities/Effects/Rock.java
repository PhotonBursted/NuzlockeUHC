package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.Arrays;
import java.util.Random;

public class Rock extends Type implements Listener {
    private final Random r = new Random();
    private final int[] stoneAmount = {0};

    public Rock(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(e.getPlayer());
        if (p.getRole() == Role.PARTICIPANT) if (p instanceof Pokemon) if (p.getType() == Pokemon.Type.ROCK)
            if (r.nextDouble() <= .2) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, 0.8f, 1);
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                if (e.getBlock().getType() == Material.GOLD_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.GOLD_ORE, r.nextInt(3)));
                if (e.getBlock().getType() == Material.IRON_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT, r.nextInt(3)));
                if (e.getBlock().getType() == Material.DIAMOND_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.DIAMOND, r.nextInt(3)));
                if (e.getBlock().getType() == Material.LAPIS_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.INK_SACK, r.nextInt(9) + 4, (short) 4));
                if (e.getBlock().getType() == Material.REDSTONE_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.REDSTONE, r.nextInt(6) + 5));
                if (e.getBlock().getType() == Material.STONE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.STONE));
            }
    }

    @Override
    void giveInitialEffects(boolean startup) { }

    @Override
    boolean hasEvent() { return true; }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType() == Pokemon.Type.ROCK) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                else nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType() == Pokemon.Type.ROCK)
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            stoneAmount[0] = 0;
                            Arrays.asList(player.getInventory().getContents()).stream()
                                    .filter(s -> s != null)
                                    .filter(s -> s.getType() == Material.STONE || s.getType() == Material.COBBLESTONE)
                                    .forEach(s -> stoneAmount[0] += s.getAmount());
                            if(stoneAmount[0] >= 6 * 64) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                            } else {
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                player.removePotionEffect(PotionEffectType.SLOW);
                            }
                        });
            }
        }.runTaskTimer(nuz, 0L, 200L);
    }
}
