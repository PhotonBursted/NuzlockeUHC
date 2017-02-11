package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Dragon type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Cold Blooded"</td>
 *             <td>Cold biomes will sometimes freeze the player</td>
 *         </tr>
 *         <tr>
 *             <td>"High Flyer"</td>
 *             <td>Option to redeem an elytra at XP level 25</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class Dragon extends Type {
    /**
     * Holds the type of the Pokémon by means of the classname
     */
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    /**
     * The randomizer instance
     */
    private final Random r = new Random();
    /**
     * Holds the player pool of people having this type
     */
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;

    Dragon(Nuzlocke nuz) {
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
    public void redeem(CommandSender sender) {
        if (((Player) sender).getLevel() >= 25) {
            sender.sendMessage(StringLib.Dragon$RedeemedElytra);
            ((Player) sender).getInventory().addItem(new ItemStack(Material.ELYTRA));
            ((Player) sender).giveExpLevels(-10);
        } else {
            sender.sendMessage(StringLib.Dragon$NotEnoughXP);
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
                        Location l = player.getLocation();

                        // Retrieve the biome the block is in
                        switch (l.getBlock().getBiome()) {
                            case COLD_BEACH:
                            case FROZEN_OCEAN:
                            case FROZEN_RIVER:
                            case ICE_FLATS:
                            case ICE_MOUNTAINS:
                            case MUTATED_ICE_FLATS:
                            case MUTATED_REDWOOD_TAIGA:
                            case MUTATED_REDWOOD_TAIGA_HILLS:
                            case MUTATED_TAIGA:
                            case MUTATED_TAIGA_COLD:
                            case REDWOOD_TAIGA:
                            case REDWOOD_TAIGA_HILLS:
                            case TAIGA:
                            case TAIGA_COLD:
                            case TAIGA_COLD_HILLS:
                            case TAIGA_HILLS:
                                // Randomly freeze the player in place
                                if (r.nextDouble() <= 0.1) {
                                    float flyspeed = player.getFlySpeed();
                                    float walkspeed = player.getWalkSpeed();
                                    player.setFlySpeed(0F);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.setFlySpeed(flyspeed);
                                            player.setWalkSpeed(walkspeed);
                                            // Run 3 seconds later
                                        }
                                    }.runTaskLater(nuz, 60L);
                                }
                        }
                    });
                }
        // Run every 10 seconds
            }
        }.runTaskTimer(nuz, 0L, 200L);
    }
}
