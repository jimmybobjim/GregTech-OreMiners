package org.jimmybobjim.oreminers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Util {
    public static RandomSource getRandom(Level level, BlockState state, BlockPos pos) {
        return RandomSource.create(pos.asLong()^state.hashCode()^level.dimension().location().hashCode());
    }
}
