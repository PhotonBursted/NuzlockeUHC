package st.photonbur.UHC.Nuzlocke;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import st.photonbur.UHC.Nuzlocke.Commands.RegisterPlayer;
import st.photonbur.UHC.Nuzlocke.Commands.StartUHC;
import st.photonbur.UHC.Nuzlocke.Commands.UnregisterPlayer;
import st.photonbur.UHC.Nuzlocke.Game.GameManager;
import st.photonbur.UHC.Nuzlocke.Game.PlayerManager;
import st.photonbur.UHC.Nuzlocke.Game.Settings;
import st.photonbur.UHC.Nuzlocke.Listeners.ChatListener;
import st.photonbur.UHC.Nuzlocke.Listeners.PlayerConnectListener;

import java.util.logging.Level;

public class Nuzlocke extends JavaPlugin {
    private GameManager gameManager;
    private PlayerManager playerManager;

    void loadCommands() {
        getCommand("register").setExecutor(new RegisterPlayer(this));
        getCommand("startUHC").setExecutor(new StartUHC(this));
        getCommand("unregister").setExecutor(new UnregisterPlayer(this));
    }

    void loadListeners(Listener... ls) {
        for(Listener l: ls) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Your nuz seems locked...");

        gameManager = new GameManager(this);
        playerManager = new PlayerManager(this);

        loadCommands();
        loadListeners(
            new ChatListener(this),
            new PlayerConnectListener(this)
        );

        for(Player p: Bukkit.getOnlinePlayers()) {
            playerManager.registerPlayer(p.getName());
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Settings getSettings() {
        return gameManager.getSettings();
    }
}
