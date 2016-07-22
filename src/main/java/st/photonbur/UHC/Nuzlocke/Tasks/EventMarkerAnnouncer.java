package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class EventMarkerAnnouncer extends BukkitRunnable {
    private final Nuzlocke nuz;
    private long time = 0;
    private int episodeCounter = 0;

    public EventMarkerAnnouncer(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public String getEpisodeNo() {
        return (episodeCounter < 10 ? "0" : "") + episodeCounter;
    }

    public long getRawTime() {
        return time;
    }

    @Override
    public void run() {
        if(time == (nuz.getSettings().getGentlemenDuration() * 60) && nuz.getSettings().getGentlemenDuration() > 0) {
            nuz.getServer().broadcastMessage(StringLib.EMA$GentlemenRuleEnd);
            nuz.getServer().getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 0.8f, 1));
        }

        if(time % (nuz.getSettings().getEpisodeDuration() * 60) == 0 && nuz.getSettings().getEpisodeDuration() > 0) {
            if(episodeCounter == 0) {
                nuz.getServer().broadcastMessage(StringLib.EMA$MarkerStart);
            } else {
                nuz.getServer().broadcastMessage(String.format(StringLib.EMA$EpisodeEnd, getEpisodeNo()));
                nuz.getServer().getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1));
            }

            episodeCounter++;
        }

        double wbSize = nuz.getGameManager().getOverworld().getWorldBorder().getSize();

        if(nuz.getSettings().isWbShrinkEnabled() &&
            nuz.getSettings().getWbInitialSize() != nuz.getSettings().getWbEndSize() &&
            nuz.getSettings().getWbShrinkDuration() > 0) {
            if(time == nuz.getSettings().getWbShrinkDelay() * 60 &&
                    nuz.getSettings().doWbStartMarker()) {
                nuz.getServer().broadcastMessage(StringLib.EMA$WbShrinkStart);
            } else if(time == (nuz.getSettings().getWbShrinkDelay() + nuz.getSettings().getWbShrinkDuration()) * 60 &&
                    nuz.getSettings().doWbEndMarker()) {
                nuz.getServer().broadcastMessage(String.format(StringLib.EMA$WbShrinkEnd, Math.round(wbSize)));
            } else if(time % (nuz.getSettings().getWbProgressMarkerInterval() * 60) == 0 &&
                    time > nuz.getSettings().getWbShrinkDelay() * 60 &&
                    time < (nuz.getSettings().getWbShrinkDelay() + nuz.getSettings().getWbShrinkDuration()) * 60) {
                nuz.getServer().broadcastMessage(String.format(StringLib.EMA$WbProgressReport, Math.round(wbSize)));
            }
        }

        time++;
    }
}
