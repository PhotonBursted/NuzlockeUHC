package st.photonbur.UHC.Nuzlocke;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import st.photonbur.UHC.Nuzlocke.Commands.*;
import st.photonbur.UHC.Nuzlocke.Discord.DiscordBot;
import st.photonbur.UHC.Nuzlocke.Entities.Effects.EffectManager;
import st.photonbur.UHC.Nuzlocke.Listeners.*;
import st.photonbur.UHC.Nuzlocke.Managers.*;
import st.photonbur.UHC.Nuzlocke.Tasks.TaskManager;

import java.util.logging.Level;

/**
 * The main class, wrapping the plugin.
 */
public class Nuzlocke extends JavaPlugin {
    private DiscordBot discordBot;
    private EffectManager effectManager;
    private JSONManager jsonManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private ServerLinkManager serverLinkManager;
    private TaskManager taskManager;
    private TeamManager teamManager;

    private String configFilename;
    private String linkFilename;

    /**
     * Links classes to commands
     */
    private void loadCommands() {
        GiveInfo giveInfo = new GiveInfo(this);
        getCommand("deregister").setExecutor(new DeregisterPlayer(this));
        getCommand("list").setExecutor(new ListPlayers(this));
        getCommand("info").setExecutor(giveInfo);
        getCommand("info").setTabCompleter(giveInfo);
        getCommand("redeem").setExecutor(new Redeem(this));
        getCommand("register").setExecutor(new RegisterPlayer(this));
        getCommand("startMatch").setExecutor(new StartUHC(this));
        getCommand("stopMatch").setExecutor(new StopUHC(this));
    }

    /**
     * Links classes to commands
     *
     * @param ls A list of variable length, containing listeners to be registered to the plugin
     */
    public void loadListeners(Listener... ls) {
        for (Listener l : ls) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    /**
     * Executed as soon as the plugin gets loaded by the Spigot server startup script
     */
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Your nuz seems locked...");

        configFilename = getDataFolder().getPath() + "/discord.json";
        linkFilename = getDataFolder().getPath() + "/serverLinks.json";

        saveDefaultConfig();

        discordBot = new DiscordBot(this);
        effectManager = new EffectManager(this);
        jsonManager = new JSONManager(this);
        gameManager = new GameManager(this);
        playerManager = new PlayerManager(this);
        serverLinkManager = new ServerLinkManager(this);
        taskManager = new TaskManager(this);
        teamManager = new TeamManager(this);

        loadCommands();
        loadListeners(
                new ChatListener(this),
                new DamageManager(this),
                new DeathListener(this),
                new PlayerConnectListener(this),
                new PokeballDetector(this)
        );

        jsonManager.readAllConfigs();
        Bukkit.getOnlinePlayers().forEach(p -> playerManager.registerPlayer(p.getName()));
    }

    /**
     * Executed when the plugin gets disabled. Mainly used for cleanup of the things the server created while running
     */
    @Override
    public void onDisable() {
        gameManager.cleanUp();
        jsonManager.writeAllConfigs();
        discordBot.stop();
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public JSONManager getJSONManager() {
        return jsonManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ServerLinkManager getServerLinkManager() {
        return serverLinkManager;
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

    public String getConfigFilename() {
        return configFilename;
    }

    public String getLinkFilename() {
        return linkFilename;
    }
}
