package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

/**
 * Gives manual control over making a player a spectator
 */
public class DeregisterPlayer implements CommandExecutor {
    /**
     * The plugin instance
     */
    private final Nuzlocke nuz;

    /**
     * Constructor for the command
     * @param nuz The plugin instance
     */
    public DeregisterPlayer(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    /**
     * Called when a command is issued
     * @param sender A reference to who executed the command
     * @param cmd The command being issued
     * @param s A pure copy of the message entered in chat
     * @param args The split up message without the command name
     * @return The success of the command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        // If the command matches the actual name, execute the real command
        if(cmd.getName().equalsIgnoreCase("deregister")) {
            // If more than only the command was entered, target another player. Otherwise, target the issuer.
            if(args.length > 0) {
                nuz.getPlayerManager().deregisterPlayer(args[0], sender);
            } else {
                nuz.getPlayerManager().deregisterPlayer(sender.getName(), sender);
            }
        }

        return true;
    }
}
