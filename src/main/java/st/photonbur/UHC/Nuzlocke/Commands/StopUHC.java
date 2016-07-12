package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class StopUHC implements CommandExecutor {
    private final Nuzlocke nuz;

    public StopUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("stopmatch")) {
            if(!nuz.getGameManager().isGameInProgress()) {
                sender.sendMessage(StringLib.StopUHC$GameNotUnderway);
            } else {
                nuz.getServer().broadcastMessage(StringLib.StopUHC$Stopped);
                nuz.getGameManager().stopGame();
            }
        }

        return true;
    }
}
