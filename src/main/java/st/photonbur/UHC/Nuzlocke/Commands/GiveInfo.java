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

public class GiveInfo implements TabExecutor {
    private final Nuzlocke nuz;

    public GiveInfo(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(cmd.getName().equalsIgnoreCase("info"))
            if(args.length > 0)
                if(args[0].equals("list")) sender.sendMessage(getOptionsList());
                else sender.sendMessage(Pokemon.Type.valueOf(args[0]) == null ? getOptionsList() : Pokemon.Type.valueOf(args[0]).getInfo());
            else if (nuz.getPlayerManager().getPlayer(sender.getName()).getType() != null)
                    sender.sendMessage(nuz.getPlayerManager().getPlayer(sender.getName()).getType().getInfo());
            else sender.sendMessage(StringLib.GiveInfo$NeedsArguments);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if(cmd.getName().equalsIgnoreCase("info")) {
            List<String> types = new ArrayList<>();
            Arrays.asList(Pokemon.Type.values()).stream()
                    .filter(type -> type.name().toLowerCase().startsWith(args[0].toLowerCase()))
                    .forEach(type -> types.add(type.name()));
            types.sort(String.CASE_INSENSITIVE_ORDER);
            return types;
        } else return null;
    }

    private String getOptionsList() {
        List<String> types = new ArrayList<>();
        String output = "";

        Arrays.asList(Pokemon.Type.values()).forEach(type -> types.add(type.name()));
        types.sort(String.CASE_INSENSITIVE_ORDER);
        for(int i=0; i<types.size(); i++) {
            output += types.get(i);
            if(i == types.size() - 2) output += " & ";
            if(i < types.size() - 2) output += ", ";
        }

        return ChatColor.ITALIC + "Available options: "+ output;
    }
}
