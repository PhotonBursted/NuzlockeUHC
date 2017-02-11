package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command regarding the listing of players. It separates players and spectators, and shows who's in what group on demand.
 *
 * @see st.photonbur.UHC.Nuzlocke.Entities.Player
 * @see Player.Role
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

    /**
     * Requests details of the player (team, Pokémon type, etcetera)
     *
     * @param p    The player to check
     * @param team The team the player is in
     * @return A formatted message containing information about the player
     */
    private static String details(Player p, Team team) {
        // Initialize the empty string to add to later
        String prefix = "";

        // Check if the player is a Pokémon
        if (p instanceof Pokemon) {
            // Yes? Display the type of the Pokémon
            prefix += p.getType().getColor() + p.getType().getName();
        } else {
            // No? Display the team's prefix (which is a color as it takes a Bukkit team) and the class of the player
            prefix += (team == null ? ChatColor.RESET : team.getPrefix()) + "[" + p.getClass().getSimpleName() + "]";
        }

        // Return the prefix
        return prefix + " ";
    }

    /**
     * Generates a formatted list of all the players on the server
     *
     * @param playerList  A list containing all online players, or at least, all the players to be listed
     * @param showDetails Determines if specific details of the players should be shown
     * @param sender      The issuer of the command
     * @param nuz         The plugin instance. Needed because of the static nature of the method
     * @return A formatted message containing information about all players in playerList
     */
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
            if (nuz.getGameManager().isGameInProgress() && nuz.getPlayerManager().getPlayer(sender.getName()).getRole() == Player.Role.SPECTATOR) {
                sender.sendMessage(listPlayersByRole(sender, Player.Role.PARTICIPANT, true));
            } else {
                sender.sendMessage(listPlayersByRole(sender, Player.Role.PARTICIPANT, false));
            }

            // List all spectators
            sender.sendMessage(StringLib.ListPlayers$Spectators);
            sender.sendMessage(listPlayersByRole(sender, Player.Role.SPECTATOR, false));
        }

        return true;
    }

    /**
     * Lists the players in a certain role
     *
     * @param sender      A reference to who executed the command
     * @param role        The role to check for
     * @param showDetails Whether or not to show specifics of the player (e.g. Pokémon type)
     * @return The formatted message to show
     */
    private String listPlayersByRole(CommandSender sender, Player.Role role, boolean showDetails) {
        String message = "";

        // Start an empty list
        final ArrayList<String> playerList = new ArrayList<>();
        // If there's any players with the role specified, add all players with that role
        if (nuz.getPlayerManager().getPlayers().stream().anyMatch(p -> p.getRole() == role)) {
            playerList.addAll(teamedPlayers(role));
            playerList.addAll(teamlessPlayers(role));

            // Add the formatted string of all players to the list
            message += formatList(playerList, showDetails, sender, nuz);
        } else {
            // Return an error message
            message += StringLib.ListPlayers$NoPlayersFound;
        }

        return message;
    }

    /**
     * Retrieves all players who are already on a team and have a certain role
     *
     * @param role The role to filter with
     * @return The list of teamed players with the specified role
     */
    private List<String> teamedPlayers(Player.Role role) {
        // Start an empty list
        List<String> playerList = new ArrayList<>();

        // For every scoreboard team, iterate over all the entries.
        // This ensures that all the players are actually on a scoreboard team
        nuz.getGameManager().getScoreboard().getTeams().forEach(t -> {
                    // Filter on role, then add all the entries to the playerList
                    List<String> entries = t.getEntries().stream().filter(p -> nuz.getPlayerManager().getPlayer(p).getRole() == role)
                            .collect(Collectors.toList());
                    entries.sort(String.CASE_INSENSITIVE_ORDER);
                    playerList.addAll(entries);
                }
        );
        return playerList;
    }

    /**
     * Retrieves all players who are not on a team and with a certain role
     *
     * @param role The role to filter with
     * @return The list of teamless players with the specified role
     */
    private List<String> teamlessPlayers(Player.Role role) {
        // Generates a list of players who are not on a scoreboard team by streaming them and looking for no matches on the player's name.
        List<Player> teamlessPlayers = nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> nuz.getGameManager().getScoreboard().getTeams().stream().noneMatch(t -> t.getEntries().contains(p.getName())))
                .filter(p -> p.getRole() == role)
                .collect(Collectors.toList());
        // List to hold all names of players which are teamless
        List<String> teamlessNames = new ArrayList<>();

        // Sort the list of players alphabetically (ignoring case)
        teamlessPlayers.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        // Add all playernames to the result list
        teamlessPlayers.forEach(p -> teamlessNames.add(p.getName()));
        return teamlessNames;
    }
}
