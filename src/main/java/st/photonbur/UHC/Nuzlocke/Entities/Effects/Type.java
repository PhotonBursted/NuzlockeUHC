package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

abstract class Type implements Listener {
    final Nuzlocke nuz;

    Type(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onMilk(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() == Material.MILK_BUCKET) giveInitialEffects(false);
    }

    abstract void giveInitialEffects(boolean startup);
    abstract boolean hasEvent();

    abstract void runContinuousEffect();
}
