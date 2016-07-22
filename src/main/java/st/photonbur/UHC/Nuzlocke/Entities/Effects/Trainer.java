package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class Trainer extends Type {
    public Trainer(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
        nuz.getPlayerManager().getPlayers().stream()
                .filter(p -> p.getRole() == Role.PARTICIPANT)
                .filter(p -> p.getType() == Pokemon.Type.TRAINER)
                .forEach(p -> {
                    Player player = Bukkit.getPlayer(p.getName());
                    player.setMaxHealth(30d);
                    if(startup) player.setHealth(30d);
                });
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    public void redeem(CommandSender sender, int levelsIn) {
        Player player = Bukkit.getPlayer(sender.getName());
        if(player.getLevel() < levelsIn) sender.sendMessage(StringLib.Trainer$NotEnoughXP);
        else {
            player.giveExpLevels(-levelsIn);
            player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
        }
    }

    @Override
    void runContinuousEffect() { }
}
