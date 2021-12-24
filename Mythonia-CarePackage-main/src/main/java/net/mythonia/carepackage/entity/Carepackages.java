package net.mythonia.carepackage.entity;

import co.vanitymc.randall.CraftHologram;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.Txt;
import lombok.Getter;
import net.mythonia.carepackage.impl.Carepackage;
import net.mythonia.carepackage.util.BlockCoordinates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Carepackages extends Entity<Carepackages> {

    protected static transient Carepackages i;

    @Getter
    public List<Carepackage> activeCarepackages = new ArrayList<>();

    @Getter
    private Long nextCarePackageSpawnMillis = System.currentTimeMillis();

    public static Carepackages get() {
        return i;
    }

    public void setNextCarePackageSpawnMillis(Long nextCarePackageSpawnMillis) {
        this.nextCarePackageSpawnMillis = nextCarePackageSpawnMillis;
        this.changed();
    }

    public void setActiveCarepackages(List<Carepackage> activeCarepackages) {
        this.activeCarepackages = activeCarepackages;
        this.changed();
    }

    public void spawnCarepackage(Location location, UUID uuid) {
        Carepackage carepackage = new Carepackage(
                new BlockCoordinates(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                System.currentTimeMillis(),
                uuid,
                ThreadLocalRandom.current().nextInt(Conf.get().minPrizesToReward, Conf.get().maxPrizesToReward + 1)
        );

        location.getBlock().setType(Material.SPONGE);

        carepackage.setHologram(new CraftHologram(
                location.clone().add(0.5, Conf.get().carePackageHologramHeight, 0.5),
                Conf.get().carePackageHologramText.stream().map(Txt::colorize).collect(Collectors.toList())
        ));

        this.activeCarepackages.add(carepackage);
        this.changed();
    }

    public void despawnCarepackage(Carepackage carepackage) {
        World world = Bukkit.getWorld(Conf.get().carePackageWorldName);
        if (world != null) {
            Block blockAt = world.getBlockAt(
                    carepackage.getBlockCoordinates().getBlockX(),
                    carepackage.getBlockCoordinates().getBlockY(),
                    carepackage.getBlockCoordinates().getBlockZ()
            );
            blockAt.setType(Material.AIR);
        }

        if (carepackage.getHologram() != null) {
            carepackage.getHologram().delete();
        }

        this.activeCarepackages.remove(carepackage);
        this.changed();
    }

    public Carepackage getCarePackage(Block block) {
        for (Carepackage carepackage : activeCarepackages) {
            if (carepackage.getBlockCoordinates().getBlockX() == block.getX() &&
                    carepackage.getBlockCoordinates().getBlockY() == block.getY() &&
                    carepackage.getBlockCoordinates().getBlockZ() == block.getZ()) {
                return carepackage;
            }
        }
        return null;
    }

    @Override
    public Carepackages load(Carepackages that) {
        super.load(that);
        this.setActiveCarepackages(that.activeCarepackages);
        return this;
    }

}