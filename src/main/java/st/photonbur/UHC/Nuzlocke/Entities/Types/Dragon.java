package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class Dragon extends Type implements CommandExecutor {
    //Buff: At 25 levels, get access to /redeem to get an elytra
    //Debuff: Random freeze when in cold biome
    public Dragon(Nuzlocke nuz) {
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
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.DRAGON)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                nuz.getPlayerManager().getPlayers().stream()
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
                        });
            }
        }.runTaskTimer(nuz, 0L, 10 * 20L);
    }

    @Override
    boolean hasEvent() { return false; }

    @Override
    void initialEffects() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("redeem")) {
            st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(sender.getName());
            if(p.getRole() == Role.PARTICIPANT)
                if(p instanceof Pokemon)
                    if(p.getType() == Pokemon.Type.DRAGON)
                        if(((Player) sender).getLevel() >= 25) {
                            sender.sendMessage(StringLib.Dragon$RedeemedElytra);
                            ((Player) sender).getInventory().addItem(new ItemStack(Material.ELYTRA));
                            ((Player) sender).giveExpLevels(-25);
                        } else sender.sendMessage(StringLib.Dragon$NotEnoughXP);
        }
        return true;
    }
}
