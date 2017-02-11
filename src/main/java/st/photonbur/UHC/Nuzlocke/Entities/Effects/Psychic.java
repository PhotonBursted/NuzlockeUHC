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
import java.util.List;

/**
 * Controls effects concerning Psychic type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Mind Reader"</td>
 *             <td>Spend XP to see what types of Pokémon are in the surroudings</td>
 *         </tr>
 *         <tr>
 *             <td>"Wits Over Looks"</td>
 *             <td>Permanent weakness I</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class Psychic extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Psychic(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Gives one-shot effects specific to the Pokémon type
     *
     * @param startup Specifies if the effect was applied on startup
     */
    @Override
    void giveInitialEffects(boolean startup) {
        // Refresh the player pool
        pp = getPlayerPool(_TYPE);

        // Set all the online player's maximum health to 22 (11 hearts) and give resistance
        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
            Player player = Bukkit.getPlayer(p.getName());

            applyPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, true, false));
        });
    }

    /**
     * Executed via commands, used to purchase a perk or item through XP levels
     *
     * @param sender The issuer of the command
     */
    public void redeem(CommandSender sender, int levelsIn) {
        // Refresh the player pool
        pp = getPlayerPool(_TYPE);

        // Get the sender's bukkit instance
        Player player = Bukkit.getPlayer(sender.getName());

        // Check if the reward can be afforded at all
        if (levelsIn > player.getLevel()) {
            sender.sendMessage(StringLib.Psychic$NotEnoughXP);
        } else {
            // Take the levels, perform the analysis
            player.giveExpLevels(-levelsIn);
            double radius = 5 + levelsIn;
            sender.sendMessage(String.format(StringLib.Psychic$RedeemedPerk, radius));

            ArrayList<String> nearbyEntities = new ArrayList<>();
            player.getNearbyEntities(radius, radius, radius).stream()
                    .filter(entity -> entity.getType() == EntityType.PLAYER)
                    .filter(p -> nuz.getPlayerManager().getPlayer(p.getName()).getRole() == st.photonbur.UHC.Nuzlocke.Entities.Player.Role.PARTICIPANT)
                    .filter(p -> nuz.getGameManager().getScoreboard().getEntryTeam(p.getName())
                            != nuz.getGameManager().getScoreboard().getEntryTeam(sender.getName()) ||
                            nuz.getGameManager().getScoreboard().getEntryTeam(p.getName()) == null)
                    .forEach(entity -> nearbyEntities.add(entity.getName()));
            nearbyEntities.sort(String.CASE_INSENSITIVE_ORDER);
            if (nearbyEntities.size() > 0) {
                sender.sendMessage(ListPlayers.formatList(nearbyEntities, true, sender, nuz));
            } else {
                sender.sendMessage(StringLib.Psychic$NoPlayersInRange);
            }
        }
    }

    /**
     * Applies effects which have to be running or checked continuously
     */
    @Override
    void runContinuousEffect() {
    }
}
