package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;

public class Steel extends Type {
    HashMap<Player, Double> water = new HashMap<>();
    HashMap<Player, Boolean> weak = new HashMap<>();
    public Steel(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == Role.PARTICIPANT)
                .filter(p -> p instanceof Pokemon)
                .filter(p -> p.getType() == Pokemon.Type.STEEL)
                .forEach(p -> {
                    Player player = Bukkit.getPlayer(p.getName());
                    player.addPotionEffect(
                            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, true, false)
                    );
                    player.setMaxHealth(22d);
                    if(startup) player.setHealth(22d);
                });
    }

    @Override
    boolean hasEvent() { return false; }

    @Override
    public void redeem(CommandSender sender, int levelsIn) { }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.STEEL)) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                    water.clear();
                    weak.clear();
                }
                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.STEEL))
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            Location l = player.getLocation();
                            if(water.containsKey(player)) water.replace(player,
                                    Math.min(Math.max(-600, (l.getBlock().isLiquid() ||
                                            (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) ?
                                            (water.get(player) + 3) : (water.get(player) - 10)), 2000));
                            else water.put(player, 0d);

                            if(water.get(player) >= 1750) {
                                if(!weak.getOrDefault(player, false)) player.sendMessage(StringLib.Steel$Rusty);
                                weak.replace(player, true);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, true, false));
                            } else if(weak.containsKey(player)) {
                                weak.replace(player, false);
                                player.removePotionEffect(PotionEffectType.SLOW);
                            } else weak.put(player, false);
                        });
                nuz.getLogger().info(String.format("Heartbeat Steel: %s", water.values().toString()));
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
