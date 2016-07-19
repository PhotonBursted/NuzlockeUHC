package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;
import java.util.Random;

public class Dark extends Type implements Listener {
    HashMap<Player, Integer> light = new HashMap<>();
    HashMap<Player, Boolean> dizzy = new HashMap<>();
    Random r = new Random();

    //Buff: Pokey eyes! Chance to blind an opponent when attacking
    //Debuff: Too long in bright light gives nausea
    public Dark(Nuzlocke nuz) {
        super(nuz);
    }

    public void onPoke(EntityDamageByEntityEvent e) {
        if(e.getEntityType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
            if(nuz.getPlayerManager().getPlayer((Player) e.getDamager()) != null)
                if(nuz.getPlayerManager().getPlayer((Player) e.getDamager()) instanceof Pokemon)
                    if(nuz.getPlayerManager().getPlayer((Player) e.getDamager()).getType() == Pokemon.Type.DARK)
                        if(r.nextDouble() <= .4) {
                            ((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, true));
                            e.getEntity().sendMessage(StringLib.DarkType$PokeEyes);
                        }
        }
    }

    @Override
    void continuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.DARK))
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            Location l = player.getLocation();
                            if(light.containsKey(player)) light.replace(player, Math.min(Math.max(-300, light.get(player) + l.getBlock().getLightLevel() - 10), 1200)); else light.put(player, 0);

                            if(light.get(player) >= 750) {
                                if(!dizzy.getOrDefault(player, false)) player.sendMessage(StringLib.DarkType$Dizzy);
                                dizzy.replace(player, true);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                            } else if(dizzy.containsKey(player)) dizzy.replace(player, false); else dizzy.put(player, true);
                        });
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.DARK)) ||
                        !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                    dizzy.clear();
                    light.clear();
                }
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }

    @Override
    boolean hasEvent() { return true; }

    @Override
    void initialEffects() { }
}
