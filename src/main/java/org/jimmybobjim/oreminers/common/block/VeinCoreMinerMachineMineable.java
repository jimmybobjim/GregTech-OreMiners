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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jimmybobjim.oreminers.common.machine.VeinCoreMinerMachine;
import org.jimmybobjim.oreminers.util.ChancedItemDrop;

import java.util.List;

public interface VeinCoreMinerMachineMineable {
    static List<ItemStack> getDefaultDrops(ServerLevel level, BlockPos veinCorePos, BlockState veinCoreState, ItemStack pickaxeTool) {
        return veinCoreState.getDrops(new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, veinCoreState)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(veinCorePos))
                .withParameter(LootContextParams.TOOL, pickaxeTool));
    }

    static List<ItemStack> getVeinCoreDrops(Level level, BlockPos veinCorePos, BlockState veinCoreState, ItemStack pickaxeTool) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            return block.getDrops(level, veinCorePos, veinCoreState);
        } else if (level instanceof ServerLevel serverLevel) {
            return getDefaultDrops(serverLevel, veinCorePos, veinCoreState, pickaxeTool);
        } else {
            return List.of();
        }
    }

    static GTRecipe getVeinCoreRecipe(Level level, BlockPos veinCorePos, BlockState veinCoreState, ItemStack pickaxeTool) {
        if (veinCoreState.getBlock() instanceof VeinCoreMinerMachineMineable block) {
            return block.getRecipe(level, veinCorePos, veinCoreState);
        }

        List<ItemStack> drops = level instanceof ServerLevel serverLevel
                ? getDefaultDrops(serverLevel, veinCorePos, veinCoreState, pickaxeTool)
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

    static boolean isTierTooHigh(Level level, BlockPos veinCorePos, BlockState veinCoreState, int machineTier) {
        return veinCoreState instanceof VeinCoreMinerMachineMineable block && block.getTier(level, veinCorePos, veinCoreState) > machineTier;
    }

    List<ItemStack> getDrops(Level level, BlockPos pos, BlockState state);

    default List<ChancedItemDrop> getChancedDrops(Level level, BlockPos pos, BlockState state) {
        return List.of();
    }

    default GTRecipe getRecipe(Level level, BlockPos pos, BlockState state) {
        GTRecipeBuilder builder = GTRecipeBuilder.ofRaw()
                .duration(600)
                .EUt(GTValues.VA[VeinCoreMinerMachine.veinCoreTierToVoltageTier(getTier(level, pos, state))])
                .inputFluids(GTMaterials.DrillingFluid.getFluid(10));

        getChancedDrops(level, pos, state).forEach(drop -> builder.chancedOutput(drop.stack(), drop.chance(), drop.tierChanceBoost()));
        getDrops(level, pos, state).forEach(drop -> builder.chancedOutput(drop, (int) Math.round(getDropChance(level, pos, state)*10000), 0));

        return builder.buildRawRecipe();
    }

    double getDropChance(Level level, BlockPos pos, BlockState state);

    void deplete(Level level, BlockPos pos, BlockState state);

    int getTier(Level level, BlockPos pos, BlockState state);

    default void addDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {}
}
