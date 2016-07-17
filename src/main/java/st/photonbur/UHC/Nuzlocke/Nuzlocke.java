package st.photonbur.UHC.Nuzlocke;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import st.photonbur.UHC.Nuzlocke.Commands.*;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Game.GameManager;
import st.photonbur.UHC.Nuzlocke.Game.PlayerManager;
import st.photonbur.UHC.Nuzlocke.Game.Settings;
import st.photonbur.UHC.Nuzlocke.Game.TeamManager;
import st.photonbur.UHC.Nuzlocke.Listeners.ChatListener;
import st.photonbur.UHC.Nuzlocke.Listeners.DeathListener;
import st.photonbur.UHC.Nuzlocke.Listeners.PlayerConnectListener;
import st.photonbur.UHC.Nuzlocke.Tasks.TaskManager;

import java.util.logging.Level;

/**
 * The main class, wrapping the plugin.
 */
public class Nuzlocke extends JavaPlugin {
    private DiscordBot discordBot;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private TaskManager taskManager;
    private TeamManager teamManager;

    /**
     * Links classes to commands
     */
    private void loadCommands() {
        getCommand("deregister").setExecutor(new DeregisterPlayer(this));
        getCommand("list").setExecutor(new ListPlayers(this));
        getCommand("register").setExecutor(new RegisterPlayer(this));
        getCommand("startMatch").setExecutor(new StartUHC(this));
        getCommand("stopMatch").setExecutor(new StopUHC(this));
    }

    /**
     * Links classes to commands
     * @param ls A list of variable length, containing listeners to be registered to the plugin
     */
    private void loadListeners(Listener... ls) {
        for(Listener l: ls) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    /**
     * Executed as soon as the plugin gets loaded by the Spigot server startup script
     */
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Your nuz seems locked...");

        discordBot = new DiscordBot(this);
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
        discordBot.start();
    }

    /**
     * Executed when the plugin gets disabled. Mainly used for cleanup of the things the server created while running
     */
    @Override
    public void onDisable() {
        gameManager.cleanUp();
        discordBot.stop();
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
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
