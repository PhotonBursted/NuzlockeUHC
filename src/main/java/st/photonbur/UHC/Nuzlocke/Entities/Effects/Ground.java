package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.List;
import java.util.Set;

class Ground extends Type {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    //Buff: Fuzt diggah.
    //Debuff: Acrophobia
    Ground(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        pp = getPlayerPool(_TYPE);

        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p ->
                Bukkit.getPlayer(p.getName()).addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1))
        );
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
                        if (player.getLocation().getY() - player.getTargetBlock((Set<Material>) null, 100).getY() > 10 + player.getLevel())
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.CONFUSION, 140, 0));
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
