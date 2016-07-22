package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.Random;

public class Dragon extends Type {
    Random r;

    //Buff: At 25 levels, get access to /redeem to get an elytra
    //Debuff: Random freeze when in cold biome
    public Dragon(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) { }

    @Override
    boolean hasEvent() { return false; }

    @Override
    public void redeem(CommandSender sender, int levelsIn) {
        if(((Player) sender).getLevel() >= 25) {
            sender.sendMessage(StringLib.Dragon$RedeemedElytra);
            ((Player) sender).getInventory().addItem(new ItemStack(Material.ELYTRA));
            ((Player) sender).giveExpLevels(-10);
        } else sender.sendMessage(StringLib.Dragon$NotEnoughXP);
    }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType() == Pokemon.Type.DRAGON) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                else nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType() == Pokemon.Type.DRAGON)
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            Location l = player.getLocation();
                            switch(l.getBlock().getBiome()) {
                                case COLD_BEACH: case FROZEN_OCEAN: case FROZEN_RIVER: case ICE_FLATS: case ICE_MOUNTAINS: case MUTATED_ICE_FLATS:
                                case MUTATED_REDWOOD_TAIGA: case MUTATED_REDWOOD_TAIGA_HILLS: case MUTATED_TAIGA: case MUTATED_TAIGA_COLD:
                                case REDWOOD_TAIGA: case REDWOOD_TAIGA_HILLS: case TAIGA: case TAIGA_COLD: case TAIGA_COLD_HILLS: case TAIGA_HILLS:
                                    if(r.nextDouble() <= 0.1) {
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
        }.runTaskTimer(nuz, 0L, 10 * 20L);
    }
}
