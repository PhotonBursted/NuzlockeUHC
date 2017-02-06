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
import java.util.HashMap;
import java.util.List;

class Ice extends Type {
    private final HashMap<Player, ArrayList<Block>> frozenBlocks = new HashMap<>();
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Ice(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                pp = getPlayerPool(_TYPE);

                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();

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
                                applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                                applyPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
                                break;
                            default:
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.removePotionEffect(PotionEffectType.WEAKNESS);
                        }

                        if (!frozenBlocks.containsKey(player)) {
                            frozenBlocks.put(player, new ArrayList<>(0));
                        }
                        for (int x = l.getBlockX() - 2; x <= l.getBlockX() + 2; x++) {
                            for (int z = l.getBlockZ() - 2; z <= l.getBlockZ() + 2; z++) {
                                Block b = l.getWorld().getBlockAt(x, l.getBlockY() - 1, z);
                                if ((b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.FROSTED_ICE)
                                        && b.getWorld().getBlockAt(x, l.getBlockY(), z).getType() == Material.AIR &&
                                        Math.sqrt(Math.pow(x - l.getBlockX(), 2) + Math.pow(z - l.getBlockZ(), 2)) < 2.25
                                        ) {
                                    frozenBlocks.get(player).add(b);
                                }
                            }
                        }

                        frozenBlocks.get(player).forEach(b -> b.setType(Material.FROSTED_ICE));
                        frozenBlocks.get(player).clear();
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 1L);
    }
}
