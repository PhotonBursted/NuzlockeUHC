package st.photonbur.UHC.Nuzlocke.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class Redeem implements CommandExecutor {
    private final Nuzlocke nuz;

    public Redeem(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("redeem")) {
            st.photonbur.UHC.Nuzlocke.Entities.Player p = nuz.getPlayerManager().getPlayer(sender.getName());
            if (p.getRole() == Role.PARTICIPANT) {
                if (p instanceof Pokemon) {
                    if (p.getType() == Pokemon.Type.DRAGON) nuz.getEffectManager().getDRG().redeem(sender, 25);
                    if (p.getType() == Pokemon.Type.POISON) nuz.getEffectManager().getPSN().redeem(sender, 20);
                    if (p.getType() == Pokemon.Type.PSYCHIC)
                        if (args.length == 0) sender.sendMessage(StringLib.Redeem$InvalidArgLength);
                        else if (Integer.parseInt(args[0]) < 5) sender.sendMessage(StringLib.Redeem$InvalidInput);
                        else nuz.getEffectManager().getPSY().redeem(sender, Integer.parseInt(args[0]));
                } else if (p instanceof Trainer) nuz.getEffectManager().getTRA().redeem(sender, 2);
            }
        }

        return true;
    }
}
