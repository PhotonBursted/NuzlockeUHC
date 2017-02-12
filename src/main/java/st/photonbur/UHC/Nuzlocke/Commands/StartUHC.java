package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

/**
 * Handles starting the UHC game
 */
public class StartUHC implements CommandExecutor {
    /**
     * The main plugin instance
     */
    private final Nuzlocke nuz;

    public StartUHC(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    /**
     * Called when a command is issued
     *
     * @param sender A reference to who executed the command
     * @param cmd    The command being issued
     * @param s      A pure copy of the message entered in chat
     * @param args   The split up message without the command name
     * @return The success of the command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        // If the command matches the actual name, execute the real command
        if (cmd.getName().equalsIgnoreCase("startmatch")) {
            if (nuz.getGameManager().isGameInProgress()) {
                sender.sendMessage(StringLib.StartUHC$GameUnderway);
            } else if (nuz.getPlayerManager().getPlayers().stream().filter(p -> p.getRole() == Player.Role.PARTICIPANT).count() < 0) {
                sender.sendMessage(StringLib.StartUHC$NoParticipants);
            } else {
                nuz.getGameManager().initGame();
            }
        }

        return true;
    }
}