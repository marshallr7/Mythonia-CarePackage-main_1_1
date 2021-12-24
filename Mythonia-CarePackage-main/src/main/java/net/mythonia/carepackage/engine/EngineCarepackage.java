package net.mythonia.carepackage.engine;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.massivecore.xlib.guava.collect.ImmutableSet;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.mythonia.carepackage.CarePackagePlugin;
import net.mythonia.carepackage.entity.Carepackages;
import net.mythonia.carepackage.entity.Conf;
import net.mythonia.carepackage.entity.impl.Prize;
import net.mythonia.carepackage.impl.Carepackage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EngineCarepackage extends Engine {

    private static final EngineCarepackage i = new EngineCarepackage();

    private static final Set<Material> TRANSPARENT_MATERIALS = ImmutableSet.of(Material.STATIONARY_WATER, Material.AIR);

    @Getter
    private final Set<Block> damagedThisTick = new HashSet<>();
    Map<Location, Long> blockDurabilityMap = new HashMap<>();
    Map<Location, Integer> entityIDMap = new HashMap<>();

    public static EngineCarepackage get() {
        return i;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (!CarePackagePlugin.get().isCarePackage(itemStack)) {
            return;
        }

        Block block = event.getBlock();
        if (!block.getWorld().getName().equals(Conf.get().carePackageWorldName)) {
            event.setCancelled(true);
            player.sendMessage(Txt.colorize(Conf.get().msgYouCantPlaceCarepackagesHere));
            return;
        }

        if (player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        } else {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
        player.updateInventory();

        if (Conf.get().announceCarePackageSpawnedForPlayerPlacedCarePackage) {
            for (String line : Conf.get().announceCarePackageSpawnedMsg) {
                MixinMessage.get().messageAll(Txt.colorize(line
                        .replace("{x}", CarePackagePlugin.get().getNumberFormat().format(block.getX()))
                        .replace("{y}", CarePackagePlugin.get().getNumberFormat().format(block.getY()))
                        .replace("{z}", CarePackagePlugin.get().getNumberFormat().format(block.getZ()))
                ));
            }
        }

        Carepackages.get().spawnCarepackage(block.getLocation(), player.getUniqueId());
    }

    public void completeCarePackage(Player player, Block block) {
        Carepackage carepackage = Carepackages.get().getCarePackage(block);
        if (carepackage == null) return;

        Prize next = CarePackagePlugin.get().getPrizePool().next();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), next.getCmd().replace("{player}", player.getName()));

        if (carepackage.getBreaksRemaining() - 1 > 0) {
            block.setType(Material.SPONGE);
            carepackage.setBreaksRemaining(carepackage.getBreaksRemaining() - 1);
            player.sendMessage(Txt.colorize(Conf.get().msgRewardsRemaining
                    .replace("{rewards}", CarePackagePlugin.get().getNumberFormat().format(carepackage.getBreaksRemaining())))
            );
        } else {
            Carepackages.get().despawnCarepackage(carepackage);

            if (Conf.get().announceCarePackageClaimed && (carepackage.getSpawnedByUuid() == null || Conf.get().announceCarePackageClaimedForPlayerPlacedCarePackage)) {
                for (String line : Conf.get().announceCarePackageClaimedMsg) {
                    MixinMessage.get().messageAll(Txt.colorize(line
                            .replace("{player}", player.getName())
                            .replace("{x}", CarePackagePlugin.get().getNumberFormat().format(block.getX()))
                            .replace("{y}", CarePackagePlugin.get().getNumberFormat().format(block.getY()))
                            .replace("{z}", CarePackagePlugin.get().getNumberFormat().format(block.getZ()))
                    ));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals(Conf.get().carePackageWorldName)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Location location = event.getBlock().getLocation();
        if (!location.getWorld().getName().equals(Conf.get().carePackageWorldName)) return;

        Block block = event.getBlock();

        if (block.getType() != Material.SPONGE) {
            event.setCancelled(true);
            return;
        }

        if (!this.damagedThisTick.add(block)) return;

        long durability = blockDurabilityMap.computeIfAbsent(location, key -> Conf.get().carePackageBreakTicks);

        durability--;

        if (durability <= 0) {
            blockDurabilityMap.remove(location);

            String breakSound = CraftMagicNumbers.getBlock(block).stepSound.getBreakSound();
            PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect = new PacketPlayOutNamedSoundEffect(breakSound, block.getX(), block.getY(), block.getZ(), 1.0F, 1.0F);

            int entityID = entityIDMap.computeIfAbsent(location, key -> ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE / 2));
            PacketPlayOutBlockBreakAnimation packetPlayOutBlockBreakAnimation = new PacketPlayOutBlockBreakAnimation(entityID, new BlockPosition(block.getX(), block.getY(), block.getZ()), -1);

            for (org.bukkit.entity.Entity entity : block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 32, 32, 32)) {
                if (!(entity instanceof Player)) continue;
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packetPlayOutNamedSoundEffect);
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packetPlayOutBlockBreakAnimation);
            }

            completeCarePackage(event.getPlayer(), block);
        } else {
            double percent = 1.0D - ((double) durability / 20.0D);
            int entityID = entityIDMap.computeIfAbsent(location, key -> ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE / 2));
            event.getPlayer().sendBlockChange(block.getLocation(), block.getType(), block.getData());
            PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(entityID, new BlockPosition(block.getX(), block.getY(), block.getZ()), (int) (percent * 9));
            for (org.bukkit.entity.Entity entity : block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 32, 32, 32)) {
                if (!(entity instanceof Player)) continue;
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
            }

            blockDurabilityMap.put(location, durability);
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Block block = entityPlayer.getBukkitEntity().getTargetBlock(TRANSPARENT_MATERIALS, 5);
        if (block.getType() != Material.SPONGE) {
            event.setCancelled(true);
            return;
        }

        Location location = block.getLocation();
        if (!location.getWorld().getName().equals(Conf.get().carePackageWorldName)) return;

        if (!this.damagedThisTick.add(block)) return;

        long durability = blockDurabilityMap.computeIfAbsent(location, key -> Conf.get().carePackageBreakTicks);

        durability--;

        if (durability <= 0) {
            blockDurabilityMap.remove(location);

            String breakSound = CraftMagicNumbers.getBlock(block).stepSound.getBreakSound();
            PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect = new PacketPlayOutNamedSoundEffect(breakSound, block.getX(), block.getY(), block.getZ(), 1.0F, 1.0F);

            int entityID = entityIDMap.computeIfAbsent(location, key -> ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE / 2));
            PacketPlayOutBlockBreakAnimation packetPlayOutBlockBreakAnimation = new PacketPlayOutBlockBreakAnimation(entityID, new BlockPosition(block.getX(), block.getY(), block.getZ()), -1);

            for (org.bukkit.entity.Entity entity : block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 32, 32, 32)) {
                if (!(entity instanceof Player)) continue;
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packetPlayOutNamedSoundEffect);
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packetPlayOutBlockBreakAnimation);
            }

            completeCarePackage(player, block);
        } else {
            double percent = 1.0D - ((double) durability / Conf.get().carePackageBreakTicks);
            int entityID = entityIDMap.computeIfAbsent(location, key -> ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE / 2));
            PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(entityID, new BlockPosition(block.getX(), block.getY(), block.getZ()), (int) (percent * 9));
            for (org.bukkit.entity.Entity entity : block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 32, 32, 32)) {
                if (!(entity instanceof Player)) continue;
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
            }

            blockDurabilityMap.put(location, durability);
        }
    }

}