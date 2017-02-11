package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.List;

/**
 * Controls effects concerning Poison type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Poison Resistance"</td>
 *             <td>Poison resistant, will turn into nausea instead</td>
 *         </tr>
 *         <tr>
 *             <td>"Portable Poison Potion Shop"</td>
 *             <td>Be able to buy a poison splash potion by trading in XP levels</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class Poison extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;
    /**
     * Tracks if a potion has been redeemed already
     */
    private boolean redeemed = false;

    Poison(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Gives one-shot effects specific to the Pokémon type
     *
     * @param startup Specifies if the effect was applied on startup
     */
    @Override
    void giveInitialEffects(boolean startup) {
    }

    /**
     * Executed via commands, used to purchase a perk or item through XP levels
     *
     * @param sender The issuer of the command
     */
    public void redeem(CommandSender sender, @SuppressWarnings("SameParameterValue") int levelsIn) {
        if (redeemed) {
            sender.sendMessage(StringLib.Poison$AlreadyRedeemed);
        } else {
            if (((Player) sender).getLevel() >= levelsIn) {
                redeemed = true;

                ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionEffects = ((PotionMeta) potion.getItemMeta());
                potionEffects.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true, true), true);
                potion.setItemMeta(potionEffects);

                ((Player) sender).getInventory().addItem(potion);
                sender.sendMessage(StringLib.Poison$RedeemedPotion);
            } else {
                sender.sendMessage(StringLib.Poison$NotEnoughXP);
            }
        }
    }

    /**
     * Applies effects which have to be running or checked continuously
     */
    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Refresh the player pool
                pp = getPlayerPool(_TYPE);

                // If the player pool is 0 or the game isn't in progress, cancel the timers
                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() || !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    // Get all the online players in the type's player pool
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());

                        // Try to find the "Poison" effect
                        PotionEffect poison = player.getActivePotionEffects().stream()
                                .filter(effect -> effect.toString().contains("POISON"))
                                .findAny().orElse(null);

                        // If it's found, apply the confusion effect and remove the poison effect
                        if (poison != null) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.CONFUSION, poison.getDuration(), 2));
                            removePotionEffect(player, PotionEffectType.POISON);
                        }
                    });
                }
        // Runs every tick
            }
        }.runTaskTimer(nuz, 0L, 1L);
    }
}
