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

class Bug extends Type {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private final Random r = new Random();
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    //Buff: Eats leaves, slowly regen health and hunger, decays faster
    //Debuff: Max health -2
    Bug(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        pp = getPlayerPool(_TYPE);

        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
            Bukkit.getPlayer(p.getName()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(16d);
            if (startup) {
                Bukkit.getPlayer(p.getName()).setHealth(16d);
            }
        });
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                pp = getPlayerPool(_TYPE);

                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() || !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();
                        int leafcount = 0;

                        // Search in all the blocks around the player for leaf blocks
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

                        // If there's enough leaves around the player, heal it and add a little bit of food.
                        if (leafcount >= 16 && time % 15 == 0) {
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
                            player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + .5d));
                        }
                    });
                    time++;
                }
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
