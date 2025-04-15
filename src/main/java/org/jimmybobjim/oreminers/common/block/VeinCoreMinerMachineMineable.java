package org.jimmybobjim.oreminers.common.block;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.util.Util;

import javax.annotation.Nullable;
import java.util.List;

public interface VeinCoreMinerMachineMineable {
    static @Nullable GTRecipe getVeinCoreRecipe(Level level, BlockPos veinCorePos, BlockState veinCoreState, ItemStack pickaxeTool) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            return block.getRecipe(level, veinCorePos, veinCoreState);
        }

        List<ItemStack> drops = level instanceof ServerLevel serverLevel
                ? Util.getDefaultDrops(serverLevel, veinCorePos, veinCoreState, pickaxeTool)
                : List.of();

        return GTRecipeBuilder.ofRaw()
                .duration(600)
                .EUt(GTValues.VA[2])
                .inputFluids(GTMaterials.DrillingFluid.getFluid(10))
                .outputItems(drops.toArray(ItemStack[]::new))
                .buildRawRecipe();

    }

    static void depleteVeinCore(Level level, BlockPos veinCorePos, BlockState veinCoreState) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            block.deplete(level, veinCorePos, veinCoreState);
        } else if (level instanceof ServerLevel) {
            level.setBlock(veinCorePos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    static boolean isVeinCoreTierTooHigh(Level level, BlockPos veinCorePos, BlockState veinCoreState, int machineTier) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            return block.getVeinCoreTier(level, veinCorePos, veinCoreState) > machineTier;
        }

        return false;
    }

    @Nullable GTRecipe getRecipe(Level level, BlockPos pos, BlockState state);

    void deplete(Level level, BlockPos pos, BlockState state);

    int getVeinCoreTier(Level level, BlockPos pos, BlockState state);

    default void addDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {}
}
