package org.jimmybobjim.oreminers.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface VeinCoreMinerMachineMineable {
    static List<ItemStack> getVeinCoreDrops(Level level, BlockPos veinCorePos, BlockState veinCoreState, ItemStack pickaxeTool) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            return block.getDrops(level, veinCorePos, veinCoreState);
        } else if (level instanceof ServerLevel serverLevel) {
            return veinCoreState.getDrops(new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.BLOCK_STATE, veinCoreState)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(veinCorePos))
                    .withParameter(LootContextParams.TOOL, pickaxeTool));
        } else {
            return List.of();
        }
    }

    static void depleteVeinCore(Level level, BlockPos veinCorePos, BlockState veinCoreState) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            block.deplete(level, veinCorePos, veinCoreState);
        } else if (level instanceof ServerLevel) {
            level.setBlock(veinCorePos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    static boolean isTierTooHigh(Level level, BlockPos veinCorePos, BlockState veinCoreState, int machineTier) {
        return veinCoreState instanceof VeinCoreMinerMachineMineable block && block.getTier(level, veinCorePos, veinCoreState) > machineTier;
    }

    List<ItemStack> getDrops(Level level, BlockPos pos, BlockState state);

    void deplete(Level level, BlockPos pos, BlockState state);

    int getTier(Level level, BlockPos pos, BlockState state);
}
