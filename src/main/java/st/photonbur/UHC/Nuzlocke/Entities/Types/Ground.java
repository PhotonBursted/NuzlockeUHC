package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.Set;

public class Ground extends Type {
    //Buff: Fuzt diggah.
    //Debuff: Acrophobia
    public Ground(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects() {
        nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == Role.PARTICIPANT)
                .filter(p -> p instanceof Pokemon)
                .filter(p -> p.getType().equals(Pokemon.Type.GROUND))
                .forEach(p ->
                        Bukkit.getPlayer(p.getName()).addPotionEffect(
                                new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1, true, false)
                        )
                );
    }

    @Override
    boolean hasEvent() { return false; }

    @Override
    public void redeem(CommandSender sender, int levelsIn) { }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.GROUND)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();

                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.GROUND))
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            if(player.getLocation().getY() - player.getTargetBlock((Set<Material>) null, 100).getY() > 10 + player.getLevel())
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 1, true, false));
                        });
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
