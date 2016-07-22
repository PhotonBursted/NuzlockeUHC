package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.Bukkit;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class Trainer extends Player {
    public Trainer(String name, Role role) {
        super(name, role);
        setType(Pokemon.Type.TRAINER);
        Bukkit.getPlayer(name).sendMessage(String.format(StringLib.Pokemon$Type, "Trainer"));
    }
}
