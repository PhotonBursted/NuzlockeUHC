package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class StartUHC implements CommandExecutor {
    private final Nuzlocke nuz;

    public StartUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("startmatch")) {
            if (nuz.getGameManager().isGameInProgress()) {
                sender.sendMessage(StringLib.StartUHC$GameUnderway);
            } else if (nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == Player.Role.PARTICIPANT).count() == 0) {
                sender.sendMessage(StringLib.StartUHC$NoParticipants);
            } else {
                nuz.getGameManager().initGame();
            }
        }

        return true;
    }
}