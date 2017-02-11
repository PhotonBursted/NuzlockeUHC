package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.List;

/**
 * Controls effects concerning Trainer type players.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Immortality"</td>
 *             <td>Get a maximum health of 30 (15 hearts)</td>
 *         </tr>
 *         <tr>
 *             <td>"Captivate"</td>
 *             <td>Has the ability to throw Pokéballs to grow their team and capture wild Pokémon</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class Trainer extends Type implements Redeemable {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Trainer(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        // Refresh the player pool
        pp = getPlayerPool(_TYPE);

        // Set all the online player's maximum health to 30 (15 hearts)
        pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
            Player player = Bukkit.getPlayer(p.getName());
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30d);
            if (startup) {
                player.setHealth(30d);
            }
        });
    }

    /**
     * Executed via commands, used to purchase a perk or item through XP levels
     *
     * @param sender The issuer of the command
     * @param levelsIn The amount of levels to need before being allowed to redeem the reward
     */
    public void redeem(CommandSender sender, int levelsIn) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (player.getLevel() < levelsIn) {
            sender.sendMessage(StringLib.Trainer$NotEnoughXP);
        } else {
            player.giveExpLevels(-levelsIn);
            player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
        }
    }

    /**
     * Applies effects which have to be running or checked continuously
     */
    @Override
    void runContinuousEffect() {
    }
}
