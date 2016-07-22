package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("ALL")
public class Electric extends Type implements Listener {
    private Player victim = null;
    private Player damager = null;
    private final Random r = new Random();

    //Buff: Paralysis AoE on hit
    //Debuff: Chance of charging creepers close by on every hit
    public Electric(Nuzlocke nuz) {
        super(nuz);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        identifyPlayers(e);
        if(!(victim == null || damager == null)) {
            st.photonbur.UHC.Nuzlocke.Entities.Player d = nuz.getPlayerManager().getPlayer(damager);
            st.photonbur.UHC.Nuzlocke.Entities.Player v = nuz.getPlayerManager().getPlayer(victim);

            if(v.getRole() == Role.PARTICIPANT) if(v instanceof Pokemon) if(v.getType() == Pokemon.Type.ELECTRIC)
                if(r.nextDouble() <= (10 + victim.getLevel() * 2) / 100F) paralyze(damager);
            if(d.getRole() == Role.PARTICIPANT) if(d instanceof Pokemon) if(d.getType() == Pokemon.Type.ELECTRIC)
                if(r.nextDouble() <= (10 + damager.getLevel() * 2) / 100F) {
                    List<Entity> nearbyEntities = victim.getNearbyEntities(3, 3, 3);
                    nearbyEntities.add(victim);
                    nearbyEntities.remove(damager);
                    paralyze(nearbyEntities.toArray(new Entity[nearbyEntities.size()]));
                }
        }
    }

    @Override
    void giveInitialEffects(boolean startup) { }

    @Override
    boolean hasEvent() { return true; }

    @Override
    void runContinuousEffect() { }

    private void identifyPlayers(EntityDamageByEntityEvent e) {
        if(e.getEntity().getType() == EntityType.PLAYER) {
            victim = (Player) e.getEntity();

            if(e.getDamager().getType() == EntityType.PLAYER) damager = (Player) e.getDamager();
            if(e.getDamager() instanceof Projectile)
                if(((Projectile) e.getDamager()).getShooter() instanceof LivingEntity) damager = (Player) ((Projectile) e.getDamager()).getShooter();
        }
    }

    private void paralyze(Entity... entities) {
        ArrayList<LivingEntity> toParalyze = new ArrayList<>();
        for(Entity e: entities) {
            if(e instanceof LivingEntity) toParalyze.add((LivingEntity) e);
        }
        paralyze(toParalyze.toArray(new LivingEntity[toParalyze.size()]));
    }

    private void paralyze(LivingEntity... entities) {
        for(LivingEntity le: entities) {
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 30));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 50, 3));
            le.sendMessage(StringLib.Electric$Paralysis);
        }
    }
}
