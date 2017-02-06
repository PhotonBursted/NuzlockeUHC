package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.event.Listener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class EffectManager {
    private final ArrayList<Type> allTypes = new ArrayList<>();
    private final Nuzlocke nuz;

    private final Bug bug;
    private final Dark drk;
    private final Dragon drg;
    private final Electric elc;
    private final Fighting fgt;
    private final Fire fir;
    private final Flying fly;
    private final Ghost gho;
    private final Grass gra;
    private final Ground grd;
    private final Ice ice;
    private final Poison psn;
    private final Psychic psy;
    private final Rock rck;
    private final Steel stl;
    private final Trainer tra;
    private final Water wat;

    public EffectManager(Nuzlocke nuz) {
        this.nuz = nuz;
        registerTypes(bug = new Bug(nuz), drk = new Dark(nuz), drg = new Dragon(nuz), elc = new Electric(nuz),
                fgt = new Fighting(nuz), fir = new Fire(nuz), fly = new Flying(nuz), gho = new Ghost(nuz),
                gra = new Grass(nuz), grd = new Ground(nuz), ice = new Ice(nuz), psn = new Poison(nuz),
                psy = new Psychic(nuz), rck = new Rock(nuz), stl = new Steel(nuz), tra = new Trainer(nuz),
                wat = new Water(nuz));
    }

    public void giveTypeEffects() {
        allTypes.forEach(t -> t.giveInitialEffects(true));
        allTypes.forEach(Type::runContinuousEffect);
    }

    private void registerTypes(Type... types) {
        Collections.addAll(allTypes, types);

        ArrayList<Listener> listeners = new ArrayList<>();
        for (Type t : types) {
            if (t.hasEvent()) {
                listeners.add(t);
            }
        }
        nuz.loadListeners(listeners.toArray(new Listener[listeners.size()]));
    }

    public Dragon getDRG() {
        return drg;
    }

    public Poison getPSN() {
        return psn;
    }

    public Psychic getPSY() {
        return psy;
    }

    public Trainer getTRA() {
        return tra;
    }
}