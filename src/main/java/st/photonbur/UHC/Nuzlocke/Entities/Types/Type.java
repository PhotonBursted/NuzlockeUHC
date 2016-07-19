package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.event.Listener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

abstract class Type implements Listener {
    Nuzlocke nuz;

    public Type(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    abstract void continuousEffect();
    abstract boolean hasEvent();
    abstract void initialEffects();
}
