package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

class GameCountdown extends BukkitRunnable {
    private final Nuzlocke nuz;

    private int counter;

    public GameCountdown(Nuzlocke nuz, int counter) {
        this.nuz = nuz;
        this.counter = counter;
    }

    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> player.playNote(player.getLocation(), Instrument.PIANO, new Note(1 + (counter == 0 ? 1 : 0), Note.Tone.F, true)));

        if(counter == nuz.getSettings().getCountDownLength()) {
            nuz.getServer().broadcastMessage(String.format(StringLib.GCD$CountdownStart, counter));
        }
        else if(counter > 0 && counter < nuz.getSettings().getCountDownLength()) {
            nuz.getServer().broadcastMessage(String.format(StringLib.GCD$CountdownProgress, counter));
        }
        else {
            this.cancel();
            nuz.getServer().broadcastMessage(StringLib.GCD$CountdownEnd);
            nuz.getGameManager().startGame();
        }

        counter--;
    }
}
