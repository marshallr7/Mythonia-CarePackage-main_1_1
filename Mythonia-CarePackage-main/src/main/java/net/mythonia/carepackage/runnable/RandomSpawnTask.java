package net.mythonia.carepackage.runnable;

import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import lombok.AllArgsConstructor;
import net.mythonia.carepackage.CarePackagePlugin;
import net.mythonia.carepackage.entity.Carepackages;
import net.mythonia.carepackage.entity.Conf;
import net.mythonia.carepackage.task.TaskSpawnCarepackage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class RandomSpawnTask extends BukkitRunnable {

    private final World world;

    // If block below location is AIR, return true, else, return false
    private boolean isInAir(Location location) {
        return Bukkit.getWorld(location.getWorld().getUID()).getBlockAt(location.getBlockX(), location.getBlockY()-1, location.getBlockZ()).getType() == Material.AIR;
    }


    @Override
    public void run() {
        int x = Conf.get().worldCenterX + ThreadLocalRandom.current().nextInt(Conf.get().worldRange) - (Conf.get().worldRange / 2);
        int z = Conf.get().worldCenterZ + ThreadLocalRandom.current().nextInt(Conf.get().worldRange) - (Conf.get().worldRange / 2);
        int y = world.getHighestBlockYAt(x, z);

        Block block = world.getBlockAt(x, y, z);
        if (!isBlockSafe(block)) return;

        Location location = block.getLocation();

        while (isInAir(location)) {
            location.setY(location.getY()-1);
        }

        Carepackages.get().spawnCarepackage(location, null);
        for (String line : Conf.get().announceCarePackageSpawnedMsg) {
            MixinMessage.get().messageAll(Txt.colorize(line
                    .replace("{x}", CarePackagePlugin.get().getNumberFormat().format(location.getX()))
                    .replace("{y}", CarePackagePlugin.get().getNumberFormat().format(location.getY()))
                    .replace("{z}", CarePackagePlugin.get().getNumberFormat().format(location.getZ()))
            ));
        }

        long nextSpawnInterval = ThreadLocalRandom.current().nextLong(Conf.get().carePackageSpawnIntervalMin, Conf.get().carePackageSpawnIntervalMax + 1);
        Carepackages.get().setNextCarePackageSpawnMillis(System.currentTimeMillis() + nextSpawnInterval);

        this.cancel();
        TaskSpawnCarepackage.get().setRandomSpawnTask(null);
        TaskSpawnCarepackage.get().setAnnouncedUpcomingSpawn(false);
    }

    private boolean isBlockSafe(Block block) {
        return !Conf.get().unsafeSpawnMaterials.contains(block.getRelative(BlockFace.DOWN).getType()) &&
                block.isEmpty() &&
                block.getRelative(BlockFace.UP).isEmpty();
    }

}