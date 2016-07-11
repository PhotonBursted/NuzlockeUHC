package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class ListPlayers implements CommandExecutor {
    Nuzlocke nuz;

    public ListPlayers(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("list")) {
            commandSender.sendMessage("" + ChatColor.BLUE + ChatColor.BOLD + "Participants:");
            commandSender.sendMessage(listPlayers(Role.PARTICIPANT));

            commandSender.sendMessage("" + ChatColor.BLUE + ChatColor.BOLD + "Spectators:");
            commandSender.sendMessage(listPlayers(Role.SPECTATOR));
        }

        return true;
    }

    private String listPlayers(Role role) {
        final String[] message = {"- "};
        if (nuz.getPlayerManager().getPlayers().stream().anyMatch(p -> p.getRole() == role)) {
            nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == role).forEach(
                    p -> message[0] += (p.getTeamColor() != null ? p.getTeamColor() : ChatColor.RESET + "" + ChatColor.ITALIC) + p.getName() + ChatColor.BLUE
                    + (role == Role.PARTICIPANT ? (" (" + p.getClass().getSimpleName() + ")") : "") + ", "
            );
        } else {
            message[0] += "" + ChatColor.BLUE + ChatColor.ITALIC + "No players found  ";
        }
        return message[0].substring(0, message[0].length() - 2);
    }
}
