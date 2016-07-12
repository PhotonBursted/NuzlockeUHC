package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class StartUHC implements CommandExecutor {
    Nuzlocke nuz;

    public StartUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("startmatch")) {
            if(nuz.getGameManager().isGameInProgress()) {
                sender.sendMessage(ChatColor.DARK_RED + "[!] Game is already underway!");
            } else {
                nuz.getGameManager().initGame();
            }
        }

        return true;
    }
}