package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.List;
import java.util.Random;

public class Dragon extends Type {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private final Random r = new Random();
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    //Buff: At 25 levels, get access to /redeem to get an elytra
    //Debuff: Random freeze when in cold biome
    Dragon(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    public void redeem(CommandSender sender) {
        if (((Player) sender).getLevel() >= 25) {
            sender.sendMessage(StringLib.Dragon$RedeemedElytra);
            ((Player) sender).getInventory().addItem(new ItemStack(Material.ELYTRA));
            ((Player) sender).giveExpLevels(-10);
        } else {
            sender.sendMessage(StringLib.Dragon$NotEnoughXP);
        }
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
                            case COLD_BEACH:
                            case FROZEN_OCEAN:
                            case FROZEN_RIVER:
                            case ICE_FLATS:
                            case ICE_MOUNTAINS:
                            case MUTATED_ICE_FLATS:
                            case MUTATED_REDWOOD_TAIGA:
                            case MUTATED_REDWOOD_TAIGA_HILLS:
                            case MUTATED_TAIGA:
                            case MUTATED_TAIGA_COLD:
                            case REDWOOD_TAIGA:
                            case REDWOOD_TAIGA_HILLS:
                            case TAIGA:
                            case TAIGA_COLD:
                            case TAIGA_COLD_HILLS:
                            case TAIGA_HILLS:
                                if (r.nextDouble() <= 0.1) {
                                    float flyspeed = player.getFlySpeed();
                                    float walkspeed = player.getWalkSpeed();
                                    player.setFlySpeed(0F);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.setFlySpeed(flyspeed);
                                            player.setWalkSpeed(walkspeed);
                                        }
                                    }.runTaskLater(nuz, 60L);
                                }
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 10 * 20L);
    }
}
