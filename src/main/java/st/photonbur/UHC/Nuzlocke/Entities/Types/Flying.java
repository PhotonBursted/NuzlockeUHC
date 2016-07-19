package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.Random;

public class Flying extends Type {
    Random r = new Random();

    //Buff: Feathers. Lots of em.
    //Debuff: They fall randomly on the ground, as entities. Full inventory/not paying attention might lead to breadcrumb trail
    public Flying(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void continuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.FLYING)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.FLYING))
                        .forEach(p -> {
                            if(r.nextDouble() <= 0.02) {
                                Location l = nuz.getServer().getPlayer(p.getName()).getLocation();
                                l.getWorld().dropItemNaturally(l, new ItemStack(Material.FEATHER, r.nextInt(3)));
                            }
                        });
            }
        }.runTaskTimer(nuz, 0L, 300L);
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    @Override
    void initialEffects() { }
}
