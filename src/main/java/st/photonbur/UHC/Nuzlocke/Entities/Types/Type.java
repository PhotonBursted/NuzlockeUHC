package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

abstract class Type implements Listener {
    Nuzlocke nuz;

    public Type(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    abstract void giveInitialEffects();
    abstract boolean hasEvent();
    abstract public void redeem(CommandSender sender, int levelsIn);
    abstract void runContinuousEffect();
}
