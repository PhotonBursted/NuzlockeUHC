package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;
import java.util.List;

class Steel extends Type {
    private final HashMap<Player, Double> water = new HashMap<>();
    private final HashMap<Player, Boolean> weak = new HashMap<>();
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Steel(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        pp = getPlayerPool(_TYPE);

        pp.forEach(p -> {
            Player player = Bukkit.getPlayer(p.getName());
            applyPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, true, false));
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(22d);
            if (startup) {
                player.setHealth(22d);
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
            @Override
            public void run() {
                pp = getPlayerPool(_TYPE);

                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() || !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                    water.clear();
                    weak.clear();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();
                        if (water.containsKey(player)) {
                            water.replace(player,
                                    Math.min(Math.max(-600, (l.getBlock().isLiquid() ||
                                            (l.getBlockY() >= l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) && l.getWorld().hasStorm())) ?
                                            (water.get(player) + 3) : (water.get(player) - 10)), 2000));
                        } else {
                            water.put(player, 0d);
                        }

                        if (water.get(player) >= 1750) {
                            if (!weak.getOrDefault(player, false)) player.sendMessage(StringLib.Steel$Rusty);
                            weak.replace(player, true);
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                        } else if (weak.containsKey(player)) {
                            weak.replace(player, false);
                            player.removePotionEffect(PotionEffectType.SLOW);
                        } else {
                            weak.put(player, false);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
