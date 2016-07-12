package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class ListPlayers implements CommandExecutor {
    private final Nuzlocke nuz;

    public ListPlayers(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    // Full list (w/ roles): Spectator, game running
    // Full list (w\ roles): Everything else
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("list")) {
            sender.sendMessage(StringLib.ListPlayers$Participants);

            if(nuz.getGameManager().isGameInProgress() && nuz.getPlayerManager().getPlayer(sender.getName()).getRole() == Role.SPECTATOR) {
                sender.sendMessage(listPlayersByRole(Role.PARTICIPANT, true));
            } else {
                sender.sendMessage(listPlayersByRole(Role.PARTICIPANT, false));
            }

            sender.sendMessage(StringLib.ListPlayers$Spectators);
            sender.sendMessage(listPlayersByRole(Role.SPECTATOR, false));
        }

        return true;
    }

    private String listPlayersByRole(Role role, boolean showRole) {
        final String[] message = {"" + ChatColor.RESET + "- "};
        final Team[] team = {null};
        if (nuz.getPlayerManager().getPlayers().stream().anyMatch(p -> p.getRole() == role)) {
            nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == role).forEach(
                    p -> message[0] +=
                            ((team[0] = nuz.getGameManager().getScoreboard().getEntryTeam(p.getName())) != null ? team[0].getPrefix() : ChatColor.RESET + "")
                          +  ChatColor.ITALIC + p.getName() + ChatColor.BLUE
                          + (role == Role.PARTICIPANT && showRole ? (" (" + (p instanceof Pokemon ? p.getType().getColor() + p.getType().getName() + ChatColor.BLUE + " Pokémon": p.getClass().getSimpleName()) + ")") : "")
                          +  ", "
            );
        } else {
            message[0] += StringLib.ListPlayers$NoPlayersFound;
        }
        return message[0].substring(0, message[0].length() - 2);
    }
}
