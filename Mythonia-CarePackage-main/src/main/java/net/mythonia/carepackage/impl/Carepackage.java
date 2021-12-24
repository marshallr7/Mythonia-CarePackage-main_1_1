package net.mythonia.carepackage.impl;

import co.vanitymc.randall.CraftHologram;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.mythonia.carepackage.entity.Carepackages;
import net.mythonia.carepackage.util.BlockCoordinates;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Carepackage {

    @NotNull
    private final BlockCoordinates blockCoordinates;
    private final long spawnedMillis;
    private final UUID spawnedByUuid;
    @NotNull
    private Integer breaksRemaining;

    @Setter
    private transient CraftHologram hologram;

    public void setBreaksRemaining(int breaksRemaining) {
        this.breaksRemaining = breaksRemaining;
        Carepackages.get().changed();
    }

}