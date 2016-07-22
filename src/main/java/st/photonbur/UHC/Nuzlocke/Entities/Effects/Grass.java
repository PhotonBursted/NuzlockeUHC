package st.photonbur.UHC.Nuzlocke.Entities.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class Grass extends Type {
    //Buff: Invis in grass, slight absorption in water
    //Debuff: Roots in water (slowness), weaker sword attacks
    public Grass(Nuzlocke nuz) {
        super(nuz);
    }

    @Override
    void giveInitialEffects(boolean startup) { }

    @Override
    boolean hasEvent() { return false; }

    @Override
    void runContinuousEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .noneMatch(p -> p.getType() == Pokemon.Type.GRASS) &&
                        nuz.getGameManager().isGameInProgress() ||
                        !nuz.getGameManager().isGameInProgress()) this.cancel();
                else nuz.getPlayerManager().getPlayers().stream()
                        .filter(p -> p.getRole() == Role.PARTICIPANT)
                        .filter(p -> p instanceof Pokemon)
                        .filter(p -> p.getType() == Pokemon.Type.GRASS)
                        .forEach(p -> {
                            Player player = Bukkit.getPlayer(p.getName());
                            Location l = player.getLocation();

                            if(l.getBlock().getType() == Material.DOUBLE_PLANT)
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                            else player.removePotionEffect(PotionEffectType.INVISIBILITY);

                            if(l.getBlock().getType() == Material.WATER || l.getBlock().getType() == Material.STATIONARY_WATER) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 0));
                            } else {
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.removePotionEffect(PotionEffectType.ABSORPTION);
                            }

                            if(player.getInventory().getItemInMainHand().getType() == Material.WOOD_SWORD ||
                                    player.getInventory().getItemInOffHand().getType() == Material.WOOD_SWORD ||
                                    player.getInventory().getItemInMainHand().getType() == Material.STONE_SWORD ||
                                    player.getInventory().getItemInOffHand().getType() == Material.STONE_SWORD ||
                                    player.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD ||
                                    player.getInventory().getItemInOffHand().getType() == Material.IRON_SWORD ||
                                    player.getInventory().getItemInMainHand().getType() == Material.GOLD_SWORD ||
                                    player.getInventory().getItemInOffHand().getType() == Material.GOLD_SWORD ||
                                    player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD ||
                                    player.getInventory().getItemInOffHand().getType() == Material.DIAMOND_SWORD)
                                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
                            else player.removePotionEffect(PotionEffectType.WEAKNESS);
                        });
            }
        }.runTaskTimer(nuz, 0L, 10L);
    }
}