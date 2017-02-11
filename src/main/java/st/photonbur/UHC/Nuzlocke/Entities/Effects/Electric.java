package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Electric type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Static"</td>
 *             <td>Depending on the amount of XP, have a certain chance of paralyzing people and mobs on damage; giving or taking</td>
 *         </tr>
 *         <tr>
 *             <td>"Electrifying"</td>
 *             <td>Randomly charge creepers close by on every melee hit</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Electric extends Type implements Listener {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * The randomizer instance
     */
    private final Random r = new Random();
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Electric(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Fired when an entity hits another entity
     *
     * @param e The caught event
     */
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        // Refresh the player pool
        pp = getPlayerPool(_TYPE);

        // Initialize the player variables
        Player victim = null;
        Player damager = null;

        // Identify the players involved
        if (e.getEntity().getType() == EntityType.PLAYER) {
            victim = (Player) e.getEntity();

            if (e.getDamager().getType() == EntityType.PLAYER) {
                damager = (Player) e.getDamager();
            }

            // In case the damager was actually a projectile (arrow, snowball, etc.)
            if (e.getDamager() instanceof Projectile) {
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) e.getDamager()).getShooter();
                }
            }
        }

        if (!(victim == null || damager == null)) {
            // Get the Nuzlocke Player instances
            st.photonbur.UHC.Nuzlocke.Entities.Player d = nuz.getPlayerManager().getPlayer(damager);
            st.photonbur.UHC.Nuzlocke.Entities.Player v = nuz.getPlayerManager().getPlayer(victim);

            // If the victim was found and is a participant of type Electric...
            if (v != null) {
                if (v.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT) {
                    if (v instanceof Pokemon) {
                        if (pp.contains(v)) {
                            // Paralyze the attacker if attacked
                            if (r.nextDouble() <= (10 + victim.getLevel() * 2) / 100F) {
                                paralyze(damager);
                            }
                        }
                    }
                }
            }

            // If the damager was found and is a participant of type Electric...
            if (d != null) {
                if (d.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT) {
                    if (d instanceof Pokemon) {
                        if (pp.contains(d)) {
                            // Paralyze the victim and surrounding entities if attacked
                            if (r.nextDouble() <= (10 + damager.getLevel() * 2) / 100F) {
                                // Get the nearby entities
                                List<Entity> nearbyEntities = victim.getNearbyEntities(3, 3, 3);
                                nearbyEntities.add(victim);
                                nearbyEntities.remove(damager);

                                // Paralyze all nearby entities
                                paralyze(nearbyEntities.toArray(new Entity[nearbyEntities.size()]));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gives one-shot effects specific to the Pokémon type
     *
     * @param startup Specifies if the effect was applied on startup
     */
    @Override
    void giveInitialEffects(boolean startup) {
    }

    /**
     * Applies effects which have to be running or checked continuously
     */
    @Override
    void runContinuousEffect() {
    }

    /**
     * Paralyzes a set of entities. Overloading method preparing for paralyzing LivingEntity instances.
     *
     * @param entities The entities to paralyze
     */
    private void paralyze(Entity... entities) {
        // Cast all entities into the LivingEntity class
        ArrayList<LivingEntity> toParalyze = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                toParalyze.add((LivingEntity) e);
            }
        }

        // Paralyze all entities in the entities list
        paralyze(toParalyze.toArray(new LivingEntity[toParalyze.size()]));
    }

    /**
     * Paralyzes a set of entities. Overloaded method actually implementing the paralysis.
     *
     * @param entities The entities to paralyze
     */
    private void paralyze(LivingEntity... entities) {
        // Loop over all passed entities
        for (LivingEntity le : entities) {
            // Apply extreme slowness and mining fatigue (emulate paralysis)
            applyPotionEffect(le, new PotionEffect(PotionEffectType.SLOW, 50, 30));
            applyPotionEffect(le, new PotionEffect(PotionEffectType.SLOW_DIGGING, 50, 3));
            le.sendMessage(StringLib.Electric$Paralysis);
        }
    }
}
