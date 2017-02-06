package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Random;

class Flying extends Type {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private final Random r = new Random();
    private List<Player> pp;

    //Buff: Feathers. Lots of em.
    //Debuff: They fall randomly on the ground, as entities. Full inventory/not paying attention might lead to breadcrumb trail
    Flying(Nuzlocke nuz) {
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
                        if (r.nextDouble() <= 0.1) {
                            Location l = nuz.getServer().getPlayer(p.getName()).getLocation();
                            l.getWorld().dropItemNaturally(l, new ItemStack(Material.FEATHER, r.nextInt(3)));
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 300L);
    }
}
