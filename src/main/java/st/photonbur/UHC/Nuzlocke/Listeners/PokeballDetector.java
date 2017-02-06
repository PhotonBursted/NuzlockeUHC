package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.HashMap;
import java.util.Map;

public class PokeballDetector implements Listener {
    private final Nuzlocke nuz;

    public PokeballDetector(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (nuz.getGameManager().isGameInProgress()) {
            if (e.getEntity().getType() == EntityType.SNOWBALL) {
                Snowball s = (Snowball) e.getEntity();

                HashMap<Player, Double> ne = new HashMap<>();
                nuz.getServer().getOnlinePlayers().stream().filter(p -> nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.PARTICIPANT)
                        .filter(p -> p.getWorld().equals(s.getWorld()))
                        .forEach(p -> ne.put(p, p.getLocation().distance(s.getLocation())));

                Map.Entry<Player, Double> target = null;
                for (Map.Entry<Player, Double> entry : ne.entrySet()) {
                    if (target == null || target.getValue() > entry.getValue()) {
                        target = entry;
                    }
                }

                Player victim = target != null ? target.getKey() : null;
                if (victim != null) {
                    if (s.getShooter() instanceof Player) {
                        Player thrower = (Player) s.getShooter();

                        // Player should be Pokémon and also alive
                        if (nuz.getPlayerManager().getPlayer(victim.getName()) instanceof Pokemon &&
                                nuz.getPlayerManager().getPlayer(victim.getName()).getRole() == Role.PARTICIPANT) {
                            if (nuz.getGameManager().getScoreboard().getEntryTeam(victim.getName()) == null) {
                                if (nuz.getPlayerManager().getPlayer(thrower.getName()) instanceof Trainer &&
                                        nuz.getPlayerManager().getPlayer(thrower.getName()).getRole() == Role.PARTICIPANT) {
                                    if (nuz.getTeamManager().getTeams().stream()
                                            .filter(t -> t.contains(thrower.getName())).findFirst().orElse(null)
                                            .getMembers().size() < nuz.getGameManager().getTeamCap()) {
                                        nuz.getTeamManager().addPlayer(victim.getName(), thrower.getName());
                                        if (!teamWin(nuz.getTeamManager().getTeams().stream().filter(t -> t.contains(thrower.getName())).findFirst().get())) {
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
                                        }
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

    private boolean teamWin(Team team) {
        if (nuz.getTeamManager().teamsAliveCount() == 1) {
            String members = team.membersToString();
            String teamName = team.getName();

            nuz.getDiscordBot().announce(DiscordBot.Event.WIN,
                    String.format(StringLib.DiscordBot$Win, "**__" + teamName + "__**", members, nuz.getSettings().getEventName())
            );
            nuz.getServer().broadcastMessage(
                    String.format(StringLib.DeathListener$Win,
                            nuz.getGameManager().getScoreboard().getTeams().stream().filter(t -> t.getDisplayName().equals(teamName)).findFirst().get().getPrefix()
                                    + ChatColor.BOLD + teamName + ChatColor.RED,
                            members, ChatColor.BOLD + nuz.getSettings().getEventName())
            );
            nuz.getGameManager().stopGame();
            return true;
        } else return false;
    }
}
