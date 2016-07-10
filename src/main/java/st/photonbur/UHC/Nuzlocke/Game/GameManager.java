package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.Tasks.GameCountDown;

public class GameManager {
    private boolean gameInProgress = false;
    Nuzlocke nuz;
    Settings settings;

    public GameManager(Nuzlocke nuz) {
        this.nuz = nuz;

        settings = new Settings(nuz.getConfig());
        settings.loadSettings();
    }

    public Settings getSettings() {
        return settings;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    void preparePlayers() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10, 10, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 20, true));
        }

        nuz.getPlayerManager().divideRoles();
    }

    public void setPlayerEffects() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            if(nuz.getPlayerManager().getPlayer(player) != null) {
                for(PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getSettings().getResistanceLength() * 20, 10, true));
            }
        }
    }

    public void startGame() {
        gameInProgress = true;

        preparePlayers();
        startCountDown();
    }

    public void stopGame() {
        gameInProgress = false;
    }

    public void startCountDown() {
        new GameCountDown(nuz, getSettings().getCountDownLength()).runTaskTimer(nuz, 0L, 20L);
    }
}
