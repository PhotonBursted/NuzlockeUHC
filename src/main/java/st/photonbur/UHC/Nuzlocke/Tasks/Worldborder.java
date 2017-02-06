package st.photonbur.UHC.Nuzlocke.Tasks;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class Worldborder extends BukkitRunnable {
    private boolean setup = false;
    private final Nuzlocke nuz;

    Worldborder(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    @Override
    public void run() {
        long time = nuz.getTaskManager().getEMA().getRawTime();

        if(time == 0 && !setup) {
            nuz.getServer().getWorlds().forEach(world -> {
                WorldBorder wb = world.getWorldBorder();
                wb.setCenter(0, 0);
                wb.setSize(nuz.getSettings().getWbInitialSize() / ((world.getEnvironment() == World.Environment.NETHER) ? 8d : 1d));
                wb.setDamageAmount(0.5);
                wb.setDamageBuffer(0);

                switch (nuz.getSettings().getWbWarningType().toLowerCase()) {
                    case "distance":
                        wb.setWarningDistance(nuz.getSettings().getWbWarningAmount());
                        break;
                    case "time":
                        wb.setWarningTime(nuz.getSettings().getWbWarningAmount());
                        break;
                    default:
                        wb.setWarningDistance(0);
                        wb.setWarningTime(0);
                }
            });
            setup = true;
        }
        if(time == Math.max(1, nuz.getSettings().getWbShrinkDelay() * 60) &&
                nuz.getSettings().isWbShrinkEnabled() &&
                nuz.getSettings().getWbInitialSize() != nuz.getSettings().getWbEndSize() &&
                nuz.getSettings().getWbShrinkDuration() > 0) {
            nuz.getServer().getWorlds().forEach(world -> {
                WorldBorder wb = world.getWorldBorder();
                wb.setSize(nuz.getSettings().getWbInitialSize() / ((world.getEnvironment() == World.Environment.NETHER) ? 8d : 1d));
                wb.setSize(nuz.getSettings().getWbEndSize() / ((world.getEnvironment() == World.Environment.NETHER) ? 8d : 1d), nuz.getSettings().getWbShrinkDuration() * 60);
            });
        }
    }

    public void reset() {
        nuz.getServer().getWorlds().forEach(world -> {
            WorldBorder wb = world.getWorldBorder();
            wb.setSize(60000000);
        });
        setup = false;
    }
}