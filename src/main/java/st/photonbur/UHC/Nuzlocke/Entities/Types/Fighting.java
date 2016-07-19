package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class Fighting extends Type {
    //Buff: Stronger when unarmed
    public Fighting(Nuzlocke nuz) {
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
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.FIGHTING)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.FIGHTING))
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            if(player.getInventory().getItemInMainHand().getAmount() == 0 &&
                               player.getInventory().getItemInOffHand().getAmount() == 0 &&
                               player.getFoodLevel() > 13) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                            } else {
                                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                        });
            }
        }.runTaskTimer(nuz, 0L, 10L);
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    @Override
    void initialEffects() { }
}
