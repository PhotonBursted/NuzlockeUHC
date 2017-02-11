package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.command.CommandSender;

interface Redeemable {
    void redeem(CommandSender sender, int levelsIn);
}
