package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.stream.Collectors;

/**
 * General root class upon which all Pokémon types are based.
 */
abstract class Type implements Listener {
    /**
     * The main plugin instance
     */
    final Nuzlocke nuz;

    Type(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    /**
     * Retrieves the players which have the specified type.
     *
     * @param t The type to scan for
     * @return The list of players which meet the requirements
     */
    List<Player> getPlayerPool(Pokemon.Type t) {
            return nuz.getPlayerManager().getPlayers().stream()
                    .filter(p -> p.getRole() == Player.Role.PARTICIPANT)
                    .filter(p -> p instanceof Pokemon)
                    .filter(p -> p.getType() == t)
                    .collect(Collectors.toList());
    }

    /**
     * Retrieves the players which have the specified type.
     *
     * @param t The type to scan for
     * @return The list of players which meet the requirements
     */
    <T extends Player> List<Player> getPlayerPool(T t) {
        return getPlayerPool(t.getType());
    }

    /**
     * Handles milk drinking. Normally this would clear all potion effects, but certain types have defaults to keep
     * @param e The event spawned by drinking/eating an item
     */
    @EventHandler
    public void onMilk(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            giveInitialEffects(false);
        }
    }

    /**
     * Method applying a potion effect. Mainly used so that manually applied potions won't be overwritten.
     *
     * @param entity The entity on which to apply the potion effect
     * @param potion The potion to be applied
     * @param <T>    Any entity inheriting LivingEntity's potionbearing mechanics
     */
    <T extends LivingEntity> void applyPotionEffect(T entity, PotionEffect potion) {
        if (!entity.hasPotionEffect(potion.getType()) || (
                entity.hasPotionEffect(potion.getType()) && (
                        entity.getPotionEffect(potion.getType()).getDuration() < potion.getDuration() &&
                        potion.getDuration() != Integer.MAX_VALUE
                )
        )) {
            entity.addPotionEffect(potion);
        }
    }

    /**
     * Method removing a potion effect. Mainly used so that removing a potion removes
     */
    <T extends LivingEntity> void removePotionEffect(T entity, PotionEffectType potionEffect) {
        if (entity.hasPotionEffect(potionEffect)) {
            entity.removePotionEffect(potionEffect);
        }
    }

    /**
     * Gives one-shot effects specific to the Pokémon type
     *
     * @param startup Specifies if the effect was applied on startup
     */
    abstract void giveInitialEffects(boolean startup);

    /**
     * Applies effects which have to be running continuously
     */
    abstract void runContinuousEffect();
}
