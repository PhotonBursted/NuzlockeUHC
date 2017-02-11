package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Controls effects concerning Dark type Pokémon.<br>
 *<br>
 * <table summary="perks">
 *     <tbody>
 *         <tr>
 *             <td colspan="2">Perks:</td>
 *         </tr>
 *         <tr>
 *             <td>"Eyepoker"</td>
 *             <td>40% chance to blind an attacked opponent for a short amount of time</td>
 *         </tr>
 *         <tr>
 *             <td>"Shadowcraver"</td>
 *             <td>Being in the light for too long will cause nausea</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
class Dark extends Type implements Listener {
    /**
     * Holds the amount of light all players have encountered.
     */
    private final HashMap<Player, Double> light = new HashMap<>();
    /**
     * Holds all players which have been weakened as a cause of Shadowcraver.
     */
    private final HashMap<Player, Boolean> weak = new HashMap<>();
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

    Dark(Nuzlocke nuz) {
        super(nuz);
    }

    /**
     * Fires whenever an entity damages another
     *
     * @param e The caught event
     */
    @EventHandler
    public void onPoke(EntityDamageByEntityEvent e) {
        // If both entities are players, the damager is of type Pokémon and the Pokémon is actually a Dark type...
        if (e.getEntityType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
            if (nuz.getPlayerManager().getPlayer((Player) e.getDamager()) != null) {
                if (nuz.getPlayerManager().getPlayer((Player) e.getDamager()) instanceof Pokemon) {
                    if (pp.contains(nuz.getPlayerManager().getPlayer(e.getDamager().getName()))) {
                        if (r.nextDouble() <= .4) {
                            // Apply a blindness effect for 2.5 seconds
                            applyPotionEffect((Player) e.getEntity(), new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                            e.getDamager().sendMessage(StringLib.Dark$PokeEyesLanded);
                            e.getEntity().sendMessage(StringLib.Dark$PokeEyesVictim);
                        }
                    }
                }
            }
        }
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
                    weak.clear();
                    light.clear();
                } else {
                    // Get all the online players in the type's player pool
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        Location l = player.getLocation();

                        // Calculate the amount of light the Pokémon has suffered from based on the current light levels
                        if (light.containsKey(player)) {
                            light.replace(player, Math.min(Math.max(-600, light.get(player) + l.getBlock().getLightLevel() - 12), 2000));
                        } else {
                            light.put(player, 0d);
                        }

                        // If a threshold has been met, send a message and apply the weakness effect
                        if (light.get(player) >= 1750) {
                            if (!weak.getOrDefault(player, false)) {
                                player.sendMessage(StringLib.Dark$Weakened);
                            }
                            weak.replace(player, true);
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
                        } else if (weak.containsKey(player)) {
                            // If the player was already weak, but the threshold isn't met anymore, remove the effect
                            weak.replace(player, false);
                            removePotionEffect(player, PotionEffectType.WEAKNESS);
                        } else {
                            weak.put(player, false);
                        }
                    });
                }
        // Run every second
            }
        }.runTaskTimer(nuz, 0, 20L);
    }
}
