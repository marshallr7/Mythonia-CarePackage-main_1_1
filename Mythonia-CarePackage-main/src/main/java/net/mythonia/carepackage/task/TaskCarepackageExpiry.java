package net.mythonia.carepackage.task;

import com.massivecraft.massivecore.ModuloRepeatTask;
import net.mythonia.carepackage.entity.Carepackages;
import net.mythonia.carepackage.entity.Conf;
import net.mythonia.carepackage.impl.Carepackage;

import java.util.Set;
import java.util.stream.Collectors;

public class TaskCarepackageExpiry extends ModuloRepeatTask {

    private static final TaskCarepackageExpiry i = new TaskCarepackageExpiry();

    public static TaskCarepackageExpiry get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 5000L;
    }

    @Override
    public void invoke(long l) {
        Set<Carepackage> toRemove = Carepackages.get().getActiveCarepackages().stream()
                .filter(carepackage -> (carepackage.getSpawnedMillis() + Conf.get().activeCarePackageExpireAfterXMillis) < System.currentTimeMillis())
                .collect(Collectors.toSet());
        toRemove.forEach(carepackage -> Carepackages.get().despawnCarepackage(carepackage));
    }

}