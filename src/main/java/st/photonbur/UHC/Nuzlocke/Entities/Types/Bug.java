package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.Random;

public class Bug extends Type {
    Random r = new Random();

    //Buff: Eats leaves, slowly regen health and hunger, decays faster
    //Debuff: Max health -2
    public Bug(Nuzlocke nuz) {
        super(nuz);
    }

    void continuousEffect() {
        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.BUG)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                nuz.getPlayerManager().getPlayers().stream()
                    .filter(p -> p.getRole() == Role.PARTICIPANT)
                    .filter(p -> p instanceof Pokemon)
                    .filter(p -> p.getType().equals(Pokemon.Type.BUG))
                    .forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();
                        int leafcount = 0;
                        for (int x = l.getBlockX() - 1; x < l.getBlockX() + 2; x++) {
                            for (int y = l.getBlockY() - 1; y < l.getBlockY() + 3; y++) {
                                for (int z = l.getBlockZ() - 1; z < l.getBlockZ() + 2; z++) {
                                    if (l.getWorld().getBlockAt(x, y, z).getType() == Material.LEAVES ||
                                            l.getWorld().getBlockAt(x, y, z).getType() == Material.LEAVES_2) {
                                        leafcount++;
                                        if (r.nextDouble() <= 0.0085) l.getWorld().getBlockAt(x, y, z).breakNaturally();
                                    }
                                }
                            }
                        }
                        if (leafcount > 16 && time % 15 == 0) {
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
                            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + .5d));
                        }
                    });
                time++;
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }

    @Override
    boolean hasEvent() { return false; }

    @Override
    void initialEffects() {
        nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == Role.PARTICIPANT)
                .filter(p -> p instanceof Pokemon)
                .filter(p -> p.getType() == Pokemon.Type.BUG)
                .forEach(p -> Bukkit.getPlayer(p.getName()).setMaxHealth(16d));
    }
}
