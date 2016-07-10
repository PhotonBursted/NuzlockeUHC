package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class UnregisterPlayer implements CommandExecutor {
    Nuzlocke nuz;

    public UnregisterPlayer(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("unregister")) {
            if(args.length > 0) {
                nuz.getPlayerManager().unregisterPlayer(args[0], commandSender);
            } else {
                nuz.getPlayerManager().unregisterPlayer(commandSender.getName(), commandSender);
            }
        }

        return true;
    }
}
