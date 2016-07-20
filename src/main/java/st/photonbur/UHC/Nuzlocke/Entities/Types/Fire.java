package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.CoalType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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

import java.util.Collection;
import java.util.Random;

public class Fire extends Type implements Listener {
    Random r = new Random();

    //Buff: Smelts blocks upon mining
    //Debuff: Melts ice and snow under feet
    public Fire(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(e.getPlayer());
        if(p.getRole() == Role.PARTICIPANT) if(p instanceof Pokemon) if(p.getType() == Pokemon.Type.FIRE)
            if(r.nextDouble() <= .05) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
                Collection<ItemStack> drops = e.getBlock().getDrops(); drops.clear();
                if(e.getBlock().getType() == Material.GOLD_ORE) drops.add(new ItemStack(Material.GOLD_INGOT));
                if(e.getBlock().getType() == Material.IRON_ORE) drops.add(new ItemStack(Material.IRON_INGOT));
                if(e.getBlock().getType() == Material.LOG || e.getBlock().getType() == Material.LOG_2) drops.add(new Coal(CoalType.CHARCOAL).toItemStack());
            }
    }

    @Override
    void giveInitialEffects() { }

    @Override
    boolean hasEvent() { return true; }

    @Override
    public void redeem(CommandSender sender, int levelsIn) { }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                nuz.getLogger().info("Heartbeat "+ this.getClass().getSimpleName());
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.FIRE)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.FIRE))
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
        }.runTaskTimer(nuz, 0, 20);
    }
}
