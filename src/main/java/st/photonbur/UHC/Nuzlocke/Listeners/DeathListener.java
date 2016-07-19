package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class DeathListener implements Listener {
    private final Nuzlocke nuz;

    public DeathListener(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(nuz.getGameManager().isGameInProgress()) {
            Player p = e.getEntity().getPlayer();

            if(nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.PARTICIPANT) {
                if(nuz.getSettings().getDeathHandleDelay() != -1) {
                    p.sendMessage(String.format(StringLib.DeathListener$DeathMove, nuz.getSettings().getDeathHandleDelay()));
                    new HandleDeadPlayer(p).runTaskLaterAsynchronously(nuz, nuz.getSettings().getDeathHandleDelay() * 20L);
                }

                if(nuz.getGameManager().getScoreboard().getEntryTeam(p.getName()) != null) {
                    Team team = nuz.getTeamManager().getTeams().stream()
                            .filter(t -> t.contains(p.getName())).findFirst().orElse(null);
                    // Checks for a count of one as the team hasn't been updated on the death yet
                    if(team.countStillAlive() == 1) {
                        if(nuz.getTeamManager().teamsAliveCount() == 2) {
                            Team otherTeam = nuz.getTeamManager().getTeams().stream()
                                    .filter(t -> t.countStillAlive() > 0 && !t.equals(team))
                                    .findFirst().orElse(null);
                            nuz.getDiscordBot().announce(DiscordBot.Event.WIN,
                                    String.format(StringLib.DiscordBot$Win, otherTeam.getName(), otherTeam.membersToString(), nuz.getSettings().getEventName())
                            );
                            nuz.getServer().broadcastMessage(
                                    String.format(StringLib.DeathListener$Win, otherTeam.getName(), otherTeam.membersToString(), nuz.getSettings().getEventName())
                            );
                            nuz.getGameManager().stopGame();
                        } else {
                            nuz.getDiscordBot().announce(DiscordBot.Event.TEAM_WIPE);
                            nuz.getServer().broadcastMessage(String.format(StringLib.DeathListener$TeamWipe,
                                    "Team " + p.getName(), nuz.getTeamManager().teamsAliveCount()));
                        }
                    } else if(nuz.getPlayerManager().getPlayer(p.getName()) instanceof Trainer) {
                        nuz.getDiscordBot().announce(DiscordBot.Event.TRAINER_WIPE);
                        nuz.getServer().broadcastMessage(String.format(StringLib.DeathListener$TrainerWipe,
                                p.getName()));
                    }
                }

                nuz.getDiscordBot().announce(DiscordBot.Event.DEATH, e.getDeathMessage().replaceAll("§.", ""));
                if(nuz.getGameManager().isGameInProgress()) nuz.getTaskManager().getSBU().updateScores();
            }
        }
    }

    private class HandleDeadPlayer extends BukkitRunnable {
        final Player p;

        public HandleDeadPlayer(Player p) {
            this.p = p;
        }

        @Override
        public void run() {
            nuz.getDiscordBot().deregisterPlayer(p.getName());
            nuz.getPlayerManager().getPlayer(p.getName()).setRole(Role.SPECTATOR);
        }
    }
}