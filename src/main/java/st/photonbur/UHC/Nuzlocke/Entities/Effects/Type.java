package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.stream.Collectors;

abstract class Type implements Listener {
    final Nuzlocke nuz;

    Type(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    List<Player> getPlayerPool(Pokemon.Type t) {
        return nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == Role.PARTICIPANT)
                .filter(p -> p instanceof Pokemon)
                .filter(p -> p.getType() == t)
                .collect(Collectors.toList());
    }

    @EventHandler
    public void onMilk(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            giveInitialEffects(false);
        }
    }

    <T extends LivingEntity> void applyPotionEffect(T le, PotionEffect potion) {
        if (!le.hasPotionEffect(potion.getType())) {
            le.addPotionEffect(potion);
        }
    }

    abstract void giveInitialEffects(boolean startup);

    abstract boolean hasEvent();

    abstract void runContinuousEffect();
}
