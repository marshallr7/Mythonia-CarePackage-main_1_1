package net.mythonia.carepackage.entity;

import com.massivecraft.massivecore.chestgui.object.DisplayItem;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import net.mythonia.carepackage.entity.impl.Prize;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Conf extends Entity<Conf> {

    protected static transient Conf i;

    public List<String> cmdAliases = MUtil.list("carepackage");

    public String carePackageWorldName = "world-warzone";
    public long activeCarePackageExpireAfterXMillis = TimeUnit.MINUTES.toMillis(30);

    public long carePackageSpawnIntervalMin = TimeUnit.MINUTES.toMillis(10);
    public long carePackageSpawnIntervalMax = TimeUnit.MINUTES.toMillis(20);

    public DisplayItem carePackageItem = new DisplayItem(
            new ItemStack(Material.SPONGE),
            "&c&lCare Package",
            MUtil.list(
                    " ",
                    "&7Place this item anywhere within",
                    "&7the open world to spawn a carepackage!"
            )
    );

    public String msgGivenCarePackage = "&aYou have given {player} x{amount} carepackages!";
    public String msgReceivedCarePackage = "&aYou have received x{amount} carepackages!";
    public String msgYouCantPlaceCarepackagesHere = "&cYou are not permitted to place carepackages here.";

    public List<Material> unsafeSpawnMaterials = MUtil.list(
            Material.STATIONARY_LAVA,
            Material.STATIONARY_WATER,
            Material.WATER,
            Material.LAVA,
            Material.CACTUS,
            Material.LEAVES,
            Material.LEAVES_2
    );

    public int worldCenterX = 0;
    public int worldCenterZ = 0;
    public int worldRange = 1000;

    public boolean announceCarePackageSpawnedForPlayerPlacedCarePackage = false;
    public List<String> announceCarePackageSpawnedMsg = MUtil.list(
            " ",
            "&aCare package has been spawned at {x}, {y}, {z}.",
            " "
    );

    public double carePackageHologramHeight = 3;
    public List<String> carePackageHologramText = MUtil.list(
            "&c&lCare Package",
            "&7&o(Right click to open)"
    );

    public int minPrizesToReward = 3;
    public int maxPrizesToReward = 5;
    public List<Prize> prizes = MUtil.list(
            new Prize("&aCookie", "give {player} cookie 1", 5),
            new Prize("&aCarrot", "give {player} carrot 1", 5)
    );

    public boolean announceCarePackageClaimed = true;
    public boolean announceCarePackageClaimedForPlayerPlacedCarePackage = false;
    public List<String> announceCarePackageClaimedMsg = MUtil.list(
            " ",
            "&aCare package at {x}, {y}, {z} has been claimed by {player}.",
            " "
    );
    public String msgRewardsRemaining = "&aThis crystal has {rewards} rewards remaining.";

    public long carePackageBreakTicks = 20L;

    public int carepackageArrivingInXSecondsTimeInterval = 30;
    public List<String> msgCarepackageArrivingInXSeconds = MUtil.list(
            " ",
            "&aA crystal will spawn in the open world in 30 seconds!",
            " "
    );

    public static Conf get() {
        return i;
    }

    @Override
    public Conf load(Conf that) {
        super.load(that);
        return this;
    }

}