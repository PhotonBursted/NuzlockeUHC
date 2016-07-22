package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;

public class Water extends Type {
    private final HashMap<Player, Double> water = new HashMap<>();
    private final HashMap<Player, Boolean> weak = new HashMap<>();

    public Water(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == Role.PARTICIPANT)
                .filter(p -> p instanceof Pokemon)
                .filter(p -> p.getType() == Pokemon.Type.WATER)
                .forEach(p -> {
                    Player player = Bukkit.getPlayer(p.getName());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1, false, false));
                });
    }

    @Override
    boolean hasEvent() { return false; }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType() == Pokemon.Type.WATER) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                else nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType() == Pokemon.Type.WATER)
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            Location l = player.getLocation();

                            if(l.getBlock().getType() == Material.WATER || l.getBlock().getType() == Material.STATIONARY_WATER ||
                                    (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                            } else {
                                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                                player.removePotionEffect(PotionEffectType.SPEED);
                            }

                            if (water.containsKey(player)) water.replace(player,
                                    Math.min(Math.max(-600, (l.getBlock().isLiquid() ||
                                            (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) ?
                                            (water.get(player) -30) : (water.get(player) + 3)), 2000));
                            else water.put(player, 0d);

                            if (water.get(player) >= 1750) {
                                if (!weak.getOrDefault(player, false)) player.sendMessage(StringLib.Water$Dehydrated);
                                weak.replace(player, true);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 0));
                            } else if (weak.containsKey(player)) {
                                weak.replace(player, false);
                                player.removePotionEffect(PotionEffectType.HUNGER);
                            } else weak.put(player, false);
                        });
            }
        }.runTaskTimer(nuz, 0L, 10L);
    }
}
