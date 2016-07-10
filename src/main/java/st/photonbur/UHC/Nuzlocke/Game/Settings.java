package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
    private FileConfiguration config;

    private int countDownLength;
    private String globalChatPrefix;
    private int resistanceLength;
    private boolean spectatorSeeAll;
    private boolean spectatorGlobalTalk;
    private int teamSize;

    public Settings(FileConfiguration config) {
        this.config = config;
        loadSettings();
    }

    void loadSettings() {
        countDownLength = config.getInt("game.start.countDownLength");
        globalChatPrefix = config.getString("chat.globalPrefix");
        resistanceLength = config.getInt("game.start.resistanceLength");
        spectatorSeeAll = config.getBoolean("chat.spectator.see");
        spectatorGlobalTalk = config.getBoolean("chat.spectator.talk");
        teamSize = config.getInt("game.teamSize");
    }

    //GETTERS
    public int getCountDownLength() { return countDownLength; }
    public String getGlobalChatPrefix() { return globalChatPrefix; }
    public int getResistanceLength() { return resistanceLength;}
    public boolean doSpectatorSeeAll() { return spectatorSeeAll; }
    public boolean doSpectatorGlobalTalk() { return spectatorGlobalTalk; }
    public int getTeamSize() { return teamSize; }
}
