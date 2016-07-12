package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class StartUHC implements CommandExecutor {
    private final Nuzlocke nuz;

    public StartUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("startmatch")) {
            if(nuz.getGameManager().isGameInProgress()) {
                sender.sendMessage(StringLib.StartUHC$GameUnderway);
            } else {
                nuz.getGameManager().initGame();
            }
        }

        return true;
    }
}