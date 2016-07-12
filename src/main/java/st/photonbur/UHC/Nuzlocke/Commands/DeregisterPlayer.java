package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class DeregisterPlayer implements CommandExecutor {
    private final Nuzlocke nuz;

    public DeregisterPlayer(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("unregister")) {
            if(args.length > 0) {
                nuz.getPlayerManager().deregisterPlayer(args[0], commandSender);
            } else {
                nuz.getPlayerManager().deregisterPlayer(commandSender.getName(), commandSender);
            }
        }

        return true;
    }
}
