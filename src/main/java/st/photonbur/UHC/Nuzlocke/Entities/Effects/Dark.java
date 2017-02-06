package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Dark extends Type implements Listener {
    private final HashMap<Player, Double> light = new HashMap<>();
    private final HashMap<Player, Boolean> weak = new HashMap<>();
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private final Random r = new Random();
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    //Buff: Pokey eyes! Chance to blind an opponent when attacking
    //Debuff: Too long in bright light gives nausea
    Dark(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    public void onPoke(EntityDamageByEntityEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
            if (nuz.getPlayerManager().getPlayer((Player) e.getDamager()) != null)
                if (nuz.getPlayerManager().getPlayer((Player) e.getDamager()) instanceof Pokemon)
                    if (nuz.getPlayerManager().getPlayer((Player) e.getDamager()).getType() == Pokemon.Type.DARK)
                        if (r.nextDouble() <= .4) {
                            applyPotionEffect((Player) e.getEntity(), new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                            e.getDamager().sendMessage(StringLib.Dark$PokeEyesLanded);
                            e.getEntity().sendMessage(StringLib.Dark$PokeEyesVictim);
                        }
        }
    }

    @Override
    void giveInitialEffects(boolean startup) {
    }

    @Override
    boolean hasEvent() {
        return true;
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
                    weak.clear();
                    light.clear();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();
                        if (light.containsKey(player)) {
                            light.replace(player, Math.min(Math.max(-600, light.get(player) + l.getBlock().getLightLevel() - 12), 2000));
                        } else {
                            light.put(player, 0d);
                        }

                        if (light.get(player) >= 1750) {
                            if (!weak.getOrDefault(player, false)) {
                                player.sendMessage(StringLib.Dark$Weakened);
                            }
                            weak.replace(player, true);
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
                        } else if (weak.containsKey(player)) {
                            weak.replace(player, false);
                            player.removePotionEffect(PotionEffectType.WEAKNESS);
                        } else {
                            weak.put(player, false);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0, 20L);
    }
}
