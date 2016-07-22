package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.CoalType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.Random;

public class Fire extends Type implements Listener {
    private final Random r = new Random();

    //Buff: Smelts blocks upon mining
    //Debuff: Melts ice and snow under feet
    public Fire(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(e.getPlayer());
        if (p.getRole() == Role.PARTICIPANT) if(p instanceof Pokemon) if(p.getType() == Pokemon.Type.FIRE)
            if (r.nextDouble() <= .05) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.8f, 1);
                e.getBlock().setType(Material.AIR);
                if (e.getBlock().getType() == Material.GOLD_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT));
                if (e.getBlock().getType() == Material.IRON_ORE)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
                if (e.getBlock().getType() == Material.LOG || e.getBlock().getType() == Material.LOG_2)
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new Coal(CoalType.CHARCOAL).toItemStack());
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
                        .noneMatch(p -> p.getType() == Pokemon.Type.FIRE) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                else nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType() == Pokemon.Type.FIRE)
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            Block b = player.getLocation().add(0, -1, 0).getBlock();
                            if(b.getType() == Material.SNOW_BLOCK || b.getType() == Material.SNOW || b.getType() == Material.ICE
                                    || b.getType() == Material.FROSTED_ICE || b.getType() == Material.PACKED_ICE)
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        b.setType(Material.WATER);
                                    }
                                }.runTaskLater(nuz, 40L);
                        });
            }
        }.runTaskTimer(nuz, 0, 1L);
    }
}
