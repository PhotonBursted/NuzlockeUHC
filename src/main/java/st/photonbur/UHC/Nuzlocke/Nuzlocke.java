package st.photonbur.UHC.Nuzlocke;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import st.photonbur.UHC.Nuzlocke.Commands.*;
import st.photonbur.UHC.Nuzlocke.Game.GameManager;
import st.photonbur.UHC.Nuzlocke.Game.PlayerManager;
import st.photonbur.UHC.Nuzlocke.Game.Settings;
import st.photonbur.UHC.Nuzlocke.Game.TeamManager;
import st.photonbur.UHC.Nuzlocke.Listeners.ChatListener;
import st.photonbur.UHC.Nuzlocke.Listeners.DeathListener;
import st.photonbur.UHC.Nuzlocke.Listeners.PlayerConnectListener;
import st.photonbur.UHC.Nuzlocke.Tasks.TaskManager;

import java.util.logging.Level;

public class Nuzlocke extends JavaPlugin {
    private GameManager gameManager;
    private PlayerManager playerManager;
    private TaskManager taskManager;
    private TeamManager teamManager;

    private void loadCommands() {
        getCommand("register").setExecutor(new RegisterPlayer(this));
        getCommand("startMatch").setExecutor(new StartUHC(this));
        getCommand("stopMatch").setExecutor(new StopUHC(this));
        getCommand("unregister").setExecutor(new DeregisterPlayer(this));
        getCommand("list").setExecutor(new ListPlayers(this));
    }

    private void loadListeners(Listener... ls) {
        for(Listener l: ls) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Your nuz seems locked...");

        gameManager = new GameManager(this);
        playerManager = new PlayerManager(this);
        taskManager = new TaskManager(this);
        teamManager = new TeamManager(this);

        loadCommands();
        loadListeners(
            new ChatListener(this),
            new DeathListener(this),
            new PlayerConnectListener(this)
        );

        Bukkit.getOnlinePlayers().stream().forEach(p -> playerManager.registerPlayer(p.getName()));
    }

    @Override
    public void onDisable() {
        gameManager.cleanUp();
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
    public TaskManager getTaskManager() {
        return taskManager;
    }
    public TeamManager getTeamManager() {
        return teamManager;
    }
}
