package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class StartUHC implements CommandExecutor {
    Nuzlocke nuz;

    public StartUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("startuhc")) {
            nuz.getGameManager().initGame();
        }

        return true;
    }
}