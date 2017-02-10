package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import st.photonbur.UHC.Nuzlocke.Commands.ListPlayers;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;

public class Psychic extends Type {
    Psychic(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT)
                .filter(p -> p instanceof Pokemon)
                .filter(p -> p.getType() == Pokemon.Type.PSYCHIC)
                .forEach(p -> applyPotionEffect(Bukkit.getPlayer(p.getName()), new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, true, false)));
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    public void redeem(CommandSender sender, int levelsIn) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (levelsIn > player.getLevel()) {
            sender.sendMessage(StringLib.Psychic$NotEnoughXP);
        } else {
            player.giveExpLevels(-levelsIn);
            double radius = 5 + levelsIn;
            sender.sendMessage(String.format(StringLib.Psychic$RedeemedPerk, radius));

            ArrayList<String> nearbyEntities = new ArrayList<>();
            player.getNearbyEntities(radius, radius, radius).stream()
                    .filter(entity -> entity.getType() == EntityType.PLAYER)
                    .filter(entity -> nuz.getPlayerManager().getPlayer(entity.getName()).getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT)
                    .filter(entity -> nuz.getGameManager().getScoreboard().getEntryTeam(entity.getName())
                            != nuz.getGameManager().getScoreboard().getEntryTeam(sender.getName()) ||
                            nuz.getGameManager().getScoreboard().getEntryTeam(entity.getName()) == null)
                    .forEach(entity -> nearbyEntities.add(entity.getName()));
            nearbyEntities.sort(String.CASE_INSENSITIVE_ORDER);
            if (nearbyEntities.size() > 0) {
                sender.sendMessage(ListPlayers.formatList(nearbyEntities, true, sender, nuz));
            } else {
                sender.sendMessage(StringLib.Psychic$NoPlayersInRange);
            }
        }
    }

    @Override
    void runContinuousEffect() {
    }
}
