package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class StopUHC implements CommandExecutor {
    Nuzlocke nuz;

    public StopUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("stopmatch")) {
            if(!nuz.getGameManager().isGameInProgress()) {
                sender.sendMessage(ChatColor.DARK_RED + "[!] There's no game running at the moment!");
            } else {
                nuz.getServer().broadcastMessage(ChatColor.DARK_RED + "[!] The match was stopped!");
                nuz.getGameManager().stopGame();
            }
        }

        return true;
    }
}
