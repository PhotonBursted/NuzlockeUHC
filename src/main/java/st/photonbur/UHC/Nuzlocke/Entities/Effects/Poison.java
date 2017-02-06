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
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.List;

public class Poison extends Type {
    private final Pokemon.Type _TYPE = Pokemon.Type.valueOf(getClass().getSimpleName().toUpperCase());
    private List<st.photonbur.UHC.Nuzlocke.Entities.Player> pp;
    private boolean redeemed = false;

    //Buff: Poison resistance, short poison potion redemption
    //Debuff: Poison turns to nausea
    Poison(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) {
    }

    @Override
    boolean hasEvent() {
        return false;
    }

    public void redeem(CommandSender sender, @SuppressWarnings("SameParameterValue") int levelsIn) {
        if (redeemed) {
            sender.sendMessage(StringLib.Poison$AlreadyRedeemed);
        } else {
            if (((Player) sender).getLevel() >= levelsIn) {
                redeemed = true;

                ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionEffects = ((PotionMeta) potion.getItemMeta());
                potionEffects.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true, true), true);
                potion.setItemMeta(potionEffects);

                ((Player) sender).getInventory().addItem(potion);
                sender.sendMessage(StringLib.Poison$RedeemedPotion);
            } else {
                sender.sendMessage(StringLib.Poison$NotEnoughXP);
            }
        }
    }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                pp = getPlayerPool(_TYPE);

                if (pp.size() == 0 && nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) {
                    this.cancel();
                } else {
                    pp.stream().filter(p -> nuz.getServer().getOnlinePlayers().contains(nuz.getServer().getPlayer(p.getName()))).forEach(p -> {
                        Player player = Bukkit.getPlayer(p.getName());
                        PotionEffect poison = player.getActivePotionEffects().stream()
                                .filter(effect -> effect.toString().contains("POISON"))
                                .findAny().orElse(null);
                        if (poison != null) {
                            applyPotionEffect(player, new PotionEffect(PotionEffectType.CONFUSION, poison.getDuration(), 2));
                            player.removePotionEffect(PotionEffectType.POISON);
                        }
                    });
                }
            }
        }.runTaskTimer(nuz, 0L, 1L);
    }
}
