package st.photonbur.UHC.Nuzlocke.Tasks;

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

    public String getEpisodeTimeLeft() {
        int min, sec;
        min = (int) (nuz.getSettings().getEpisodeDuration() - (time / 60) % nuz.getSettings().getEpisodeDuration() - 1);
        sec = (int) (59 - ((time - 1) % 60));
        return (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
    }

    @Override
    public void run() {
        if(time == (nuz.getSettings().getGentlemenDuration() * 60) && nuz.getSettings().getGentlemenDuration() > 0) {
            nuz.getServer().broadcastMessage(StringLib.EMA$GentlemenRuleEnd);
        }

        if(time % (nuz.getSettings().getEpisodeDuration() * 60) == 0 && nuz.getSettings().getEpisodeDuration() > 0) {
            if(episodeCounter == 0) {
                nuz.getServer().broadcastMessage(StringLib.EMA$MarkerStart);
            } else {
                nuz.getServer().broadcastMessage(String.format(StringLib.EMA$EpisodeEnd, getEpisodeNo()));
            }

            episodeCounter++;
        }

        time++;
    }
}
