package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class Poison extends Type {
    private boolean redeemed = false;

    //Buff: Poison resistance, short poison potion redemption
    //Debuff: Poison turns to nausea
    public Poison(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) { }

    @Override
    boolean hasEvent() { return false; }

    public void redeem(CommandSender sender, int levelsIn) {
        if(redeemed) sender.sendMessage(StringLib.Poison$AlreadyRedeemed);
        else {
            if (((Player) sender).getLevel() >= 20) {
                redeemed = true;

                ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionEffects = ((PotionMeta) potion.getItemMeta());
                potionEffects.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true, true), true);
                potion.setItemMeta(potionEffects);

                ((Player) sender).getInventory().addItem(potion);
                sender.sendMessage(StringLib.Poison$RedeemedPotion);
            } else sender.sendMessage(StringLib.Poison$NotEnoughXP);
        }
    }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType() == Pokemon.Type.POISON) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                else nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType() == Pokemon.Type.POISON)
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            PotionEffect poison = player.getActivePotionEffects().stream()
                                    .filter(effect -> effect.toString().contains("POISON"))
                                    .findAny().orElse(null);
                            if(poison != null) {
                                player.addPotionEffect(
                                        new PotionEffect(PotionEffectType.CONFUSION, poison.getDuration(), 2)
                                );
                                player.removePotionEffect(PotionEffectType.POISON);
                            }
                        });
            }
        }.runTaskTimer(nuz, 0L, 1L);
    }
}
