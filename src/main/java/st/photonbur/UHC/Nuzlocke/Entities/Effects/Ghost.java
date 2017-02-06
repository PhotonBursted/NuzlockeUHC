package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;

class Ghost extends Type {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    //Buff: Invisibility when above 9 hunger
    Ghost(Nuzlocke nuz) {
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
                        if (player.getFoodLevel() > 18) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, true));
                        } else {
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
