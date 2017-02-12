package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides information for all the separate Pokémon types
 */
public class GiveInfo implements TabExecutor {
    /**
     * The main plugin instance
     */
    private final Nuzlocke nuz;

    public GiveInfo(Nuzlocke nuz) {
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
        if (cmd.getName().equalsIgnoreCase("info")) {
            // If there are arguments specified, either give a list or a Pokémon type information.
            if (args.length > 0) {
                if (args[0].equals("list")) {
                    sender.sendMessage(getOptionsList());
                } else if (Arrays.stream(Pokemon.Type.values()).anyMatch(t -> t.name().equalsIgnoreCase(args[0]))) {
                    sender.sendMessage(Pokemon.Type.valueOf(args[0].toUpperCase()).getInfo());
                } else {
                    sender.sendMessage(StringLib.GiveInfo$TypeNotFound);
                }
            // If no arguments were specified, see if the player has a type
            } else if (nuz.getPlayerManager().getPlayer(sender.getName()).getType() != null) {
                sender.sendMessage(nuz.getPlayerManager().getPlayer(sender.getName()).getType().getInfo());
            } else sender.sendMessage(StringLib.GiveInfo$NeedsArguments);
        }

        return true;
    }

    /**
     * Called when a tab completion is required
     *
     * @param sender A reference to who executed the command
     * @param cmd    The command being issued
     * @param s      A pure copy of the message entered in chat
     * @param args   The split up message without the command name
     * @return The list of things to complete with
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> types = new ArrayList<>();

        // Check the command being issued
        if (cmd.getName().equalsIgnoreCase("info")) {
            // Collect all Pokémon type names and return them as a list
            Arrays.stream(Pokemon.Type.values())
                    .filter(type -> type.name().toLowerCase().startsWith(args[0].toLowerCase()))
                    .forEach(type -> types.add(type.getName()));
            types.sort(String.CASE_INSENSITIVE_ORDER);
            return types;
        } else {
            return null;
        }
    }

    /**
     * Retrieves a list of options to choose from
     *
     * @return A list of options to be used by the user of /info
     */
    private String getOptionsList() {
        List<String> types = new ArrayList<>();
        String output = "";

        Arrays.asList(Pokemon.Type.values()).forEach(type -> types.add(type.name()));
        types.sort(String.CASE_INSENSITIVE_ORDER);

        // Loop over all types and build a formatted string out of it
        for (int i = 0; i < types.size(); i++) {
            output += types.get(i);
            if (i == types.size() - 2) {
                output += " & ";
            }
            if (i < types.size() - 2) {
                output += ", ";
            }
        }

        return ChatColor.ITALIC + "Available options: " + output;
    }
}
