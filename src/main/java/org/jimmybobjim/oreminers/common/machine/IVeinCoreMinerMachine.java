package org.jimmybobjim.oreminers.common.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IVeinCoreMinerMachine {
    void setOldState(BlockState oldState);
    BlockState getOldState();

    void setVeinCorePos(BlockPos veinCorePos);

    void updatePattern(BlockPos pos, BlockState oldState, BlockState newState);
}
