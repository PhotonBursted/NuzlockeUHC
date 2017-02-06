package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;

class Fighting extends Type {
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());

    //Buff: Stronger when unarmed
    Fighting(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
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

                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        if (player.getInventory().getItemInMainHand().getAmount() == 0 &&
                                player.getInventory().getItemInOffHand().getAmount() == 0 &&
                                player.getFoodLevel() > 13) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                        } else {
                            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 10L);
    }
}
