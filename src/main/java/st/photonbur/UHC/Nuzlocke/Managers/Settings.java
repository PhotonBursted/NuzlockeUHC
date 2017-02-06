package st.photonbur.UHC.Nuzlocke.Managers;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
    private final FileConfiguration config;

    private int countDownLength;
    private int deathHandleDelay;
    private int episodeDuration;
    private int eternalDaylight;
    private String eventName;
    private int gentlemenDuration;
    private String globalChatPrefix;
    private int resistanceLength;
    private boolean seeTeammateDetails;
    private boolean spectatorSeeAll;
    private boolean spectatorGlobalTalk;
    private int teamSize;
    private boolean wbEnabled;
    private boolean wbEndNotification;
    private int wbEndSize;
    private int wbInitialSize;
    private int wbProgressMarkerInterval;
    private int wbShrinkDelay;
    private int wbShrinkDuration;
    private boolean wbShrinkEnabled;
    private boolean wbStartMarker;
    private int wbWarningAmount;
    private String wbWarningType;

    Settings(FileConfiguration config) {
        this.config = config;
        loadSettings();
    }

    void loadSettings() {
        countDownLength = config.getInt("game.start.countDownLength");
        deathHandleDelay = config.getInt("death.delay");
        episodeDuration = config.getInt("game.markers.episodeDuration");
        eternalDaylight = config.getInt("game.environment.eternalDaylight");
        eventName = config.getString("eventName");
        gentlemenDuration = config.getInt("game.markers.gentlemenDuration");
        globalChatPrefix = config.getString("chat.globalPrefix");
        resistanceLength = config.getInt("game.start.resistanceLength");
        seeTeammateDetails = config.getBoolean("chat.listPlayers.seeTeammateDetails");
        spectatorSeeAll = config.getBoolean("chat.spectator.see");
        spectatorGlobalTalk = config.getBoolean("chat.spectator.talk");
        teamSize = config.getInt("game.teamSize");
        wbEnabled = config.getBoolean("game.environment.worldborder.enable");
        wbEndNotification = config.getBoolean("game.markers.worldborder.end");
        wbEndSize = config.getInt("game.environment.worldborder.endSize");
        wbInitialSize = config.getInt("game.environment.worldborder.initialSize");
        wbProgressMarkerInterval = config.getInt("game.markers.worldborder.progress");
        wbShrinkDelay = config.getInt("game.environment.worldborder.shrink.delay");
        wbShrinkDuration = config.getInt("game.environment.worldborder.shrink.time");
        wbShrinkEnabled = config.getBoolean("game.environment.worldborder.shrink.enable");
        wbStartMarker = config.getBoolean("game.markers.worldborder.start");
        wbWarningAmount = config.getInt("game.environment.worldborder.warning.amount");
        wbWarningType = config.getString("game.environment.worldborder.warning.type");
    }

    public int getCountDownLength() {
        return countDownLength;
    }

    public int getDeathHandleDelay() {
        return deathHandleDelay;
    }

    public int getEpisodeDuration() {
        return episodeDuration;
    }

    public int getEternalDaylight() {
        return eternalDaylight;
    }

    public String getEventName() {
        return eventName;
    }

    public int getGentlemenDuration() {
        return gentlemenDuration;
    }

    public String getGlobalChatPrefix() {
        return globalChatPrefix;
    }

    int getResistanceLength() {
        return resistanceLength;
    }

    public boolean doSeeTeammateDetails() {
        return seeTeammateDetails;
    }

    public boolean doSpectatorSeeAll() {
        return spectatorSeeAll;
    }

    public boolean doSpectatorGlobalTalk() {
        return spectatorGlobalTalk;
    }

    int getTeamSize() {
        return teamSize;
    }

    boolean isWbEnabled() {
        return wbEnabled;
    }

    public boolean doWbEndMarker() {
        return wbEndNotification;
    }

    public int getWbEndSize() {
        return wbEndSize;
    }

    public int getWbInitialSize() {
        return wbInitialSize;
    }

    public int getWbProgressMarkerInterval() {
        return wbProgressMarkerInterval;
    }

    public int getWbShrinkDelay() {
        return wbShrinkDelay;
    }

    public int getWbShrinkDuration() {
        return wbShrinkDuration;
    }

    public boolean isWbShrinkEnabled() {
        return wbShrinkEnabled;
    }

    public boolean doWbStartMarker() {
        return wbStartMarker;
    }

    public int getWbWarningAmount() {
        return wbWarningAmount;
    }

    public String getWbWarningType() {
        return wbWarningType;
    }
}
