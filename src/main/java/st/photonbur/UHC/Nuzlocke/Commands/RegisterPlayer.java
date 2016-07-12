package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class RegisterPlayer implements CommandExecutor {
    private final Nuzlocke nuz;

    public RegisterPlayer(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("register")) {
            if(args.length > 0) {
                nuz.getPlayerManager().registerPlayer(args[0], sender);
            } else {
                nuz.getPlayerManager().registerPlayer(sender.getName(), sender);
            }
        }

        return true;
    }
}
