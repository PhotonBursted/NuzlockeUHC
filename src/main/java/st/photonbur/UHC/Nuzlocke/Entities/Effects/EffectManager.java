package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.event.Listener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.Collections;

public class EffectManager {
    ArrayList<Type> allTypes = new ArrayList<>();
    Nuzlocke nuz;

    Bug bug; Dark drk; Dragon drg; Electric elc;
    Fighting fgt; Fire fir; Flying fly; Ghost gho;
    Grass gra; Ground grd; Poison psn; Psychic psy;
    Steel stl; Trainer tra;

    public EffectManager(Nuzlocke nuz) {
        this.nuz = nuz;
        registerTypes(bug = new Bug(nuz), drk = new Dark(nuz), drg = new Dragon(nuz), elc = new Electric(nuz),
                      fgt = new Fighting(nuz), fir = new Fire(nuz), fly = new Flying(nuz), gho = new Ghost(nuz),
                      gra = new Grass(nuz), grd = new Ground(nuz), psn = new Poison(nuz), psy = new Psychic(nuz),
                      stl = new Steel(nuz), tra = new Trainer(nuz));
    }

    public void giveEffects() {
        allTypes.forEach(t -> t.giveInitialEffects(true));
        allTypes.forEach(Type::runContinuousEffect);
    }

    private void registerTypes(Type... types) {
        Collections.addAll(allTypes, types);

        ArrayList<Listener> listeners = new ArrayList<>();
        for(Type t: types) {
            if(t.hasEvent()) listeners.add(t);
        }
        nuz.loadListeners(listeners.toArray(new Listener[listeners.size()]));
    }

    public Dragon getDRG() { return drg; }
    public Poison getPSN() { return psn; }
    public Psychic getPSY() { return psy; }
    public Trainer getTRA() { return tra; }
}