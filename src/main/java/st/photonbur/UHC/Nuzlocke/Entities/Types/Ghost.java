package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class Ghost extends Type {
    //Buff: Invisibility when above 9 hunger
    public Ghost(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects() { }

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
                        .noneMatch(p -> p.getType().equals(Pokemon.Type.GHOST)) ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType().equals(Pokemon.Type.GHOST))
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            if(player.getFoodLevel() > 18)
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
                            else player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        });
            }
        }.runTaskTimer(nuz, 0L, 20L);
    }
}
