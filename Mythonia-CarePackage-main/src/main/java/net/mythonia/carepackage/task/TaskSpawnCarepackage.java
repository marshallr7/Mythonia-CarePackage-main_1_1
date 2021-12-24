package net.mythonia.carepackage.task;

import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import lombok.Setter;
import net.mythonia.carepackage.CarePackagePlugin;
import net.mythonia.carepackage.entity.Carepackages;
import net.mythonia.carepackage.entity.Conf;
import net.mythonia.carepackage.runnable.RandomSpawnTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class TaskSpawnCarepackage extends ModuloRepeatTask {

    private static final TaskSpawnCarepackage i = new TaskSpawnCarepackage();

    @Setter
    private BukkitTask randomSpawnTask = null;

    @Setter
    private boolean announcedUpcomingSpawn = false;

    public static TaskSpawnCarepackage get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 5000L;
    }

    @Override
    public void invoke(long l) {
        if (!announcedUpcomingSpawn && System.currentTimeMillis() >= Carepackages.get().getNextCarePackageSpawnMillis() - (TimeUnit.SECONDS.toMillis(Conf.get().carepackageArrivingInXSecondsTimeInterval))) {
            for (String line : Conf.get().msgCarepackageArrivingInXSeconds) {
                MixinMessage.get().messageAll(Txt.colorize(line));
            }
            announcedUpcomingSpawn = true;
        }

        if (System.currentTimeMillis() >= Carepackages.get().getNextCarePackageSpawnMillis()) {
            if (randomSpawnTask != null && Bukkit.getScheduler().isCurrentlyRunning(randomSpawnTask.getTaskId())) {
                return;
            }

            World world = Bukkit.getWorld(Conf.get().carePackageWorldName);
            if (world == null) {
                return;
            }

            randomSpawnTask = new RandomSpawnTask(world).runTaskTimer(CarePackagePlugin.get(), 2L, 2L);
        }
    }

}