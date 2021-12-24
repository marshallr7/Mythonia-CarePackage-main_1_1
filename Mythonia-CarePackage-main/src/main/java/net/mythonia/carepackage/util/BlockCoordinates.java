package net.mythonia.carepackage.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class BlockCoordinates {

    @NonNull
    private final Integer blockX, blockY, blockZ;

    private transient Integer hashCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockCoordinates that = (BlockCoordinates) o;
        return blockX.equals(that.blockX) && blockY.equals(that.blockY) && blockZ.equals(that.blockZ);
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(blockX, blockY, blockZ);
        }
        return hashCode;
    }

}