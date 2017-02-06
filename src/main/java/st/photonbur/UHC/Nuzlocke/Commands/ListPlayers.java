package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command regarding the listing of players. It separates players and spectators, and shows who's in what group on demand.
 *
 * @see st.photonbur.UHC.Nuzlocke.Entities.Player
 * @see Role
 */
public class ListPlayers implements CommandExecutor {
    /**
     * The main plugin instance
     */
    private final Nuzlocke nuz;

    /**
     * Constructs the command's instance
     *
     * @param nuz The main plugin instance
     */
    public ListPlayers(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    private static String details(Player p, Team team) {
        String message = "";
        if (p instanceof Pokemon) {
            message += p.getType().getColor() + p.getType().getName();
        } else {
            message += (team == null ? ChatColor.RESET : team.getPrefix()) + "[" + p.getClass().getSimpleName() + "]";
        }
        return message + " ";
    }

    public static String formatList(ArrayList<String> playerList, boolean showDetails, CommandSender sender, Nuzlocke nuz) {
        String message = "";

        for (int i = 0; i < playerList.size(); i += 3) {
            List<String> segment = playerList.subList(i, Math.min(i + 3, playerList.size()));
            message += i == 0 ? "- " : "\n   ";
            for (int j = 0; j < segment.size(); j++) {
                Team team = nuz.getGameManager().getScoreboard().getEntryTeam(playerList.get(i + j));
                Player p = nuz.getPlayerManager().getPlayer(playerList.get(i + j));

                if (showDetails) {
                    message += details(p, team);
                }

                if (!showDetails && team != null) {
                    if (nuz.getGameManager().getScoreboard().getEntryTeam(sender.getName()) != null) {
                        if (nuz.getGameManager().getScoreboard().getEntryTeam(sender.getName()).equals(team) && nuz.getSettings().doSeeTeammateDetails()) {
                            message += details(p, team);
                        }
                    }
                }

                message += (team == null ? ChatColor.RESET : team.getPrefix()) + playerList.get(i + j);

                if (i + j < playerList.size() - 1) {
                    message += ChatColor.RESET + ", ";
                }
            }
        }

        return message;
    }

    /**
     * Called when a command is issued
     *
     * @param sender A reference to who executed the command
     * @param cmd    The command being issued
     * @param s      A pure copy of the message entered in chat
     * @param args   The split up message without the command name
     * @return The success of the command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        // If the command matches the desired name, continue executing the command
        if (cmd.getName().equalsIgnoreCase("list")) {
            // Send a "participant" header message to the issuer
            sender.sendMessage(StringLib.ListPlayers$Participants);

            // Decide whether or not to show specifics per player (Pokemon.Type for example)
            if (nuz.getGameManager().isGameInProgress() && nuz.getPlayerManager().getPlayer(sender.getName()).getRole() == Role.SPECTATOR) {
                sender.sendMessage(listPlayersByRole(sender, Role.PARTICIPANT, true));
            } else {
                sender.sendMessage(listPlayersByRole(sender, Role.PARTICIPANT, false));
            }

            // List all spectators
            sender.sendMessage(StringLib.ListPlayers$Spectators);
            sender.sendMessage(listPlayersByRole(sender, Role.SPECTATOR, false));
        }

        return true;
    }

    /**
     * Lists the players in a certain role
     *
     * @param role        The role to check with
     * @param showDetails Whether or not to show specifics of the player
     * @return The message to show
     */
    private String listPlayersByRole(CommandSender sender, Role role, boolean showDetails) {
        String message = "";
        final ArrayList<String> playerList = new ArrayList<>();
        if (nuz.getPlayerManager().getPlayers().stream().anyMatch(p -> p.getRole() == role)) {
            playerList.addAll(teamedPlayers(role));
            playerList.addAll(teamlessPlayers(role));

            message += formatList(playerList, showDetails, sender, nuz);
        } else {
            message += StringLib.ListPlayers$NoPlayersFound;
        }

        return message;
    }

    private List<String> teamedPlayers(Role role) {
        List<String> playerList = new ArrayList<>();
        nuz.getGameManager().getScoreboard().getTeams().forEach(t -> {
                    List<String> entries = t.getEntries().stream().filter(p -> nuz.getPlayerManager().getPlayer(p).getRole() == role)
                            .collect(Collectors.toList());
                    entries.sort(String.CASE_INSENSITIVE_ORDER);
                    playerList.addAll(entries);
                }
        );
        return playerList;
    }

    private List<String> teamlessPlayers(Role role) {
        List<Player> teamlessPlayers = nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> nuz.getGameManager().getScoreboard().getTeams().stream().noneMatch(t -> t.getEntries().contains(p.getName())))
                .filter(p -> p.getRole() == role)
                .collect(Collectors.toList());
        List<String> teamlessNames = new ArrayList<>();

        teamlessPlayers.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        teamlessPlayers.forEach(p -> teamlessNames.add(p.getName()));
        return teamlessNames;
    }
}
