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
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Random;

class Fire extends Type implements Listener {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private final Random r = new Random();
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    //Buff: Smelts blocks upon mining
    //Debuff: Melts ice and snow under feet
    Fire(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(e.getPlayer());
        if (p.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT) {
            if (p instanceof Pokemon) {
                if (p.getType() == Pokemon.Type.FIRE) {
                    if (r.nextDouble() <= .05) {
                        e.getPlayer().playSound(e.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
                        Block b = e.getBlock();

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
                        Block b = player.getLocation().add(0, -1, 0).getBlock();

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
                                    }
                                }.runTaskLater(nuz, 40L);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0, 1L);
    }
}
