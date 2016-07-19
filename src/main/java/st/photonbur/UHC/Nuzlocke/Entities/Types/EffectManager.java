package st.photonbur.UHC.Nuzlocke.Entities.Types;

import org.bukkit.event.Listener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.Collections;

public class EffectManager {
    ArrayList<Type> allTypes = new ArrayList<>();
    Nuzlocke nuz;

    Bug bug;
    Dark drk;
    Dragon drg;
    Electric elc;
    Fighting fgt;
    Fire fir;
    Flying fly;

    public EffectManager(Nuzlocke nuz) {
        this.nuz = nuz;
        registerTypes(bug = new Bug(nuz), drk = new Dark(nuz), drg = new Dragon(nuz), elc = new Electric(nuz),
                      fgt = new Fighting(nuz), fir = new Fire(nuz), fly = new Flying(nuz));
    }

    public void giveEffects() {
        allTypes.forEach(Type::initialEffects);
        allTypes.forEach(Type::continuousEffect);
    }

    private void registerTypes(Type... types) {
        Collections.addAll(allTypes, types);
        ArrayList<Listener> listeners = new ArrayList<>();
        for(Type t: types) {
            if(t.hasEvent()) listeners.add(t);
        }
        nuz.loadListeners(listeners.toArray(new Listener[listeners.size()]));
    }
}