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

/**
 * you generally shouldn't be calling the individual methods added to a class by this interface,
 * the static methods of the same name include handling for non-{@code VeinCoreMinerMachineMineable} blocks
 * and thus are preferred
 */
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

    static boolean isVeinCoreDepleted(Level level, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof VeinCoreMinerMachineMineable block && block.isDepleted(level, pos, state);
    }

    static boolean isVeinCoreTierTooHigh(Level level, BlockPos veinCorePos, BlockState veinCoreState, int machineTier) {
        return veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block
                && block.getVeinCoreTier(level, veinCorePos, veinCoreState) > machineTier;
    }

    static void addVeinCoreDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {
        if (state.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            block.addDisplayText(level, pos, state, textList);
        } else {
            textList.add(state.getBlock().getName());
        }
    }

    @Nullable GTRecipe getRecipe(Level level, BlockPos pos, BlockState state);

    void deplete(Level level, BlockPos pos, BlockState state);

    boolean isDepleted(Level level, BlockPos pos, BlockState state);

    int getVeinCoreTier(Level level, BlockPos pos, BlockState state);

    default void addDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {}
}
