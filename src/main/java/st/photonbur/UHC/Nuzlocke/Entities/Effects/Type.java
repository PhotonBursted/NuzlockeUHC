package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

abstract class Type implements Listener {
    Nuzlocke nuz;

    public Type(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onMilk(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() == Material.MILK_BUCKET) giveInitialEffects(false);
    }

    abstract void giveInitialEffects(boolean startup);
    abstract boolean hasEvent();
    abstract public void redeem(CommandSender sender, int levelsIn);
    abstract void runContinuousEffect();
}
