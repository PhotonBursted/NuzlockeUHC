package st.photonbur.UHC.Nuzlocke.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class DamageManager implements Listener {
    private final double NE = .5;
    private final double SE = 2;
    private final double[][] atkMods = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,NE, 0, 1, 1,NE, 1},
            {1, 1,NE,NE, 1,SE,SE, 1, 1, 1, 1, 1,SE,NE, 1,NE, 1,SE, 1},
            {1, 1,SE,NE, 1,NE, 1, 1, 1,SE, 1, 1, 1,SE, 1,NE, 1, 1, 1},
            {1, 1, 1,SE,NE,NE, 1, 1, 1, 0,SE, 1, 1, 1, 1,NE, 1, 1, 1},
            {1, 1,NE,SE, 1,NE, 1, 1,NE,SE,NE, 1,NE,SE, 1,NE, 1,NE, 1},
            {1, 1,NE,NE, 1,SE,NE, 1, 1,SE,SE, 1, 1, 1, 1,SE, 1,NE, 1},
            {1,SE, 1, 1, 1, 1,SE, 1,NE, 1,NE,NE,NE,SE, 0, 1,SE,SE,NE},
            {1, 1, 1, 1, 1,SE, 1, 1,NE,NE, 1, 1, 1,NE,NE, 1, 1, 0,SE},
            {1, 1,SE, 1,SE,NE, 1, 1,SE, 1, 0, 1,NE,SE, 1, 1, 1,SE, 1},
            {1, 1, 1, 1,NE,SE, 1,SE, 1, 1, 1, 1,SE,NE, 1, 1, 1,NE, 1},
            {1, 1, 1, 1, 1, 1, 1,SE,SE, 1, 1,NE, 1, 1, 1, 1, 0,NE, 1},
            {1, 1,NE, 1, 1,SE, 1,NE,NE, 1,NE,SE, 1, 1,NE, 1,SE,NE,NE},
            {1, 1,SE, 1, 1, 1,SE,NE, 1,NE,SE, 1,SE, 1, 1, 1, 1,NE, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,SE, 1, 1,SE, 1,NE, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,SE, 1,NE, 0},
            {1, 1, 1, 1, 1, 1, 1,NE, 1, 1, 1,SE, 1, 1,SE, 1,NE, 1,NE},
            {1, 1,NE,NE,NE, 1,SE, 1, 1, 1, 1, 1, 1,SE, 1, 1, 1,NE,SE},
            {1, 1,NE, 1, 1, 1, 1,SE,NE, 1, 1, 1, 1, 1, 1,SE,SE,NE, 1}
    };

    Nuzlocke nuz;

    public DamageManager(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity().getType() == EntityType.PLAYER) {
            Player victim = (Player) e.getEntity();
            if(nuz.getPlayerManager().getPlayer(victim.getName()).getRole() == Role.PARTICIPANT) {
                if (nuz.getGameManager().isTruceActive() || e.getDamager().getType() == EntityType.SNOWBALL)
                    e.setCancelled(true);
                else {
                    Player damager = (Player) (e.getDamager() instanceof Projectile ? ((Projectile) e.getDamager()).getShooter() : e.getDamager());
                    int dTypeID = nuz.getPlayerManager().getPlayer(damager.getName()) instanceof Pokemon
                            ? nuz.getPlayerManager().getPlayer(damager.getName()).getType().getID() : 0;
                    int vTypeID = nuz.getPlayerManager().getPlayer(victim.getName()) instanceof Pokemon
                            ? nuz.getPlayerManager().getPlayer(victim.getName()).getType().getID() : 0;
                    double modifier = atkMods[dTypeID][vTypeID];
                    if(modifier == 0) damager.sendMessage(StringLib.DamageManager$Immune);
                    if(modifier == NE) damager.sendMessage(StringLib.DamageManager$NotEffective);
                    if(modifier == SE) damager.sendMessage(StringLib.DamageManager$SuperEffective);

                    e.setDamage(e.getFinalDamage() * modifier);
                }
            }
        }
    }
}
