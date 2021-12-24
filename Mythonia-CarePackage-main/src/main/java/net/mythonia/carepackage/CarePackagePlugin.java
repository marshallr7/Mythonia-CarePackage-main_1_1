package net.mythonia.carepackage;

import co.vanitymc.randall.CraftHologram;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.util.Txt;
import lombok.Getter;
import net.mythonia.carepackage.cmd.CmdCarepackage;
import net.mythonia.carepackage.engine.EngineCarepackage;
import net.mythonia.carepackage.entity.Carepackages;
import net.mythonia.carepackage.entity.CarepackagesColl;
import net.mythonia.carepackage.entity.Conf;
import net.mythonia.carepackage.entity.ConfColl;
import net.mythonia.carepackage.entity.impl.Prize;
import net.mythonia.carepackage.impl.Carepackage;
import net.mythonia.carepackage.task.TaskCarepackageExpiry;
import net.mythonia.carepackage.task.TaskSpawnCarepackage;
import net.mythonia.carepackage.util.RandomCollection;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.stream.Collectors;

public final class CarePackagePlugin extends MassivePlugin {

    private static CarePackagePlugin i;

    private final NamespacedKey carepackageKey = new NamespacedKey(this, "carepackageItem");

    @Getter
    private NumberFormat numberFormat;

    @Getter
    private RandomCollection<Prize> prizePool;

    public CarePackagePlugin() {
        CarePackagePlugin.i = this;
    }

    public static CarePackagePlugin get() {
        return i;
    }

    @Override
    public void onEnableInner() {
        ConfColl.get().setActive(true);
        CarepackagesColl.get().setActive(true);

        this.activate(
                CmdCarepackage.class,

                EngineCarepackage.class,

                TaskSpawnCarepackage.class,
                TaskCarepackageExpiry.class
        );

        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setGroupingUsed(true);

        this.refreshPrizePool();

        World world = Bukkit.getWorld(Conf.get().carePackageWorldName);
        if (world != null) {
            for (Carepackage carepackage : Carepackages.get().activeCarepackages) {
                Location location = new Location(
                        world,
                        carepackage.getBlockCoordinates().getBlockX(),
                        carepackage.getBlockCoordinates().getBlockY(),
                        carepackage.getBlockCoordinates().getBlockZ()
                );

                if (carepackage.getHologram() == null) {
                    carepackage.setHologram(new CraftHologram(
                            location.clone().add(0.5, Conf.get().carePackageHologramHeight, 0.5),
                            Conf.get().carePackageHologramText.stream().map(Txt::colorize).collect(Collectors.toList())
                    ));
                }

                location.getBlock().setType(Material.SPONGE);
            }
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> EngineCarepackage.get().getDamagedThisTick().clear(), 0L, 1L);
    }

    public void refreshPrizePool() {
        this.prizePool = new RandomCollection<>();

        for (Prize prize : Conf.get().prizes) {
            prizePool.add(prize.getChance(), prize);
        }
    }

    @Override
    public boolean isVersionSynchronized() {
        return false;
    }

    @Override
    public void onDisable() {
        for (Carepackage carepackage : Carepackages.get().activeCarepackages) {
            if (carepackage.getHologram() != null) {
                carepackage.getHologram().delete();
            }
        }

        super.onDisable();
    }

    public ItemStack getCarepackageItem(int amount) {
        ItemStack ret = Conf.get().carePackageItem.createItem();
        ret.setAmount(amount);

        ItemMeta itemMeta = ret.getItemMeta();
        itemMeta.getPersistentDataContainer().set(carepackageKey, PersistentDataType.STRING, "josephwashere");
        ret.setItemMeta(itemMeta);

        return ret;
    }

    public boolean isCarePackage(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Conf.get().carePackageItem.getItemStack().getType() || !itemStack.hasItemMeta()) {
            return false;
        }
        return itemStack.getItemMeta().getPersistentDataContainer().has(carepackageKey, PersistentDataType.STRING);
    }

}