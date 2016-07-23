package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
                nuz.getPlayerManager().getPlayer(p.getName()).setRole(Role.SPECTATOR);
                if(nuz.getSettings().getDeathHandleDelay() != -1) {
                    p.sendMessage(String.format(StringLib.DeathListener$DeathMove, nuz.getSettings().getDeathHandleDelay()));
                    p.setGameMode(GameMode.SPECTATOR);
                    new HandleDeadPlayer(p).runTaskLaterAsynchronously(nuz, nuz.getSettings().getDeathHandleDelay() * 20L);
                }

                Team team = nuz.getTeamManager().getTeams().stream()
                        .filter(t -> t.contains(p.getName())).findFirst().orElse(null);
                // Checks for a count of one as the team hasn't been updated on the death yet
                if(team == null || team.countStillAlive() == 0) {
                    if(nuz.getTeamManager().teamsAliveCount() == 1) {
                        String teamName, members;

                        Team otherTeam = nuz.getTeamManager().getTeams().stream()
                                .filter(t -> t.countStillAlive() > 0 && !t.contains(p.getName()))
                                .findFirst().orElse(null);
                        if (otherTeam == null) {
                            members = nuz.getPlayerManager().getPlayers().stream()
                                    .filter(player -> player.getRole() == Role.PARTICIPANT && !player.getName().equals(p.getName()))
                                    .findFirst().get().getName();
                            teamName = "Team "+ members;
                        } else {
                            members = otherTeam.membersToString();
                            teamName = otherTeam.getName();
                        }

                        nuz.getDiscordBot().announce(DiscordBot.Event.WIN,
                                String.format(StringLib.DiscordBot$Win, "**__"+ teamName +"__**", members, nuz.getSettings().getEventName())
                        );
                        nuz.getServer().broadcastMessage(
                                otherTeam == null ? String.format(StringLib.DeathListener$Win, teamName, members, ChatColor.BOLD + nuz.getSettings().getEventName())
                                        : String.format(StringLib.DeathListener$Win,
                                        nuz.getGameManager().getScoreboard().getTeam(teamName).getPrefix() + ChatColor.BOLD + teamName + ChatColor.RED,
                                        members, ChatColor.BOLD + nuz.getSettings().getEventName())
                        );
                        nuz.getGameManager().stopGame();
                    } else {
                        nuz.getDiscordBot().announce(DiscordBot.Event.TEAM_WIPE,
                                String.format("Team " + p.getName(), nuz.getTeamManager().teamsAliveCount()));
                        nuz.getServer().broadcastMessage(String.format(StringLib.DeathListener$TeamWipe,
                                "Team " + p.getName(), nuz.getTeamManager().teamsAliveCount()));
                    }
                } else if(nuz.getPlayerManager().getPlayer(p.getName()) instanceof Trainer) {
                    nuz.getGameManager().teamCapBonus++;

                    nuz.getDiscordBot().announce(DiscordBot.Event.TRAINER_WIPE);
                    nuz.getServer().broadcastMessage(String.format(StringLib.DeathListener$TrainerWipe,
                            p.getName(), nuz.getGameManager().teamCapBonus + nuz.getSettings().getTeamSize()));
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
        }
    }
}