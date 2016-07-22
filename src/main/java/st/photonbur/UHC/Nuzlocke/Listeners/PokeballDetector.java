package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class PokeballDetector implements Listener {
    private final Nuzlocke nuz;

    public PokeballDetector(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if(nuz.getGameManager().isGameInProgress()) {
            if(e.getDamager().getType() == EntityType.SNOWBALL) {
                if(e.getEntityType() == EntityType.PLAYER) {
                    Player victim = (Player) e.getEntity();
                    Snowball s = (Snowball) e.getDamager();
                    Player thrower = (Player) s.getShooter();

                    // Player should be Pokémon and also alive
                    if (nuz.getPlayerManager().getPlayer(victim.getName()) instanceof Pokemon &&
                            nuz.getPlayerManager().getPlayer(victim.getName()).getRole() == Role.PARTICIPANT) {
                        if (nuz.getGameManager().getScoreboard().getEntryTeam(victim.getName()) == null) {
                            if (nuz.getPlayerManager().getPlayer(thrower.getName()) instanceof Trainer &&
                                    nuz.getPlayerManager().getPlayer(thrower.getName()).getRole() == Role.PARTICIPANT) {
                                if (nuz.getTeamManager().getTeams().stream()
                                        .filter(t -> t.contains(thrower.getName())).findFirst().orElse(null).getMembers().size() < nuz.getSettings().getTeamSize()) {
                                    nuz.getTeamManager().addPlayer(victim.getName(), thrower.getName());
                                    victim.sendMessage(String.format(StringLib.PokeballDetector$CaughtVictim,
                                            nuz.getGameManager().getScoreboard().getEntryTeam(thrower.getName()).getPrefix() +
                                                    "Team " + nuz.getPlayerManager().getPlayer(thrower).getName())
                                    );
                                    thrower.sendMessage(String.format(StringLib.PokeballDetector$CaughtThrower,
                                            nuz.getPlayerManager().getPlayer(victim).getType().getColor() +
                                                    nuz.getPlayerManager().getPlayer(victim).getType().getName() +
                                                    nuz.getGameManager().getScoreboard().getEntryTeam(thrower.getName()).getPrefix() +
                                                    " " + victim.getName())
                                    );
                                } else {
                                    thrower.sendMessage(StringLib.PokeballDetector$TeamAlreadyFull);
                                }
                            } else {
                                thrower.sendMessage(StringLib.PokeballDetector$NotATrainer);
                            }
                        } else {
                            thrower.sendMessage(StringLib.PokeballDetector$TargetOnTeamAlready);
                        }
                    }
                }
            }
        }
    }
}
