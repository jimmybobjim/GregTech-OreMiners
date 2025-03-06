package org.jimmybobjim.oreminers.common.machine.trait;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.common.block.VeinCoreMinerMachineMineable;
import org.jimmybobjim.oreminers.common.machine.VeinCoreMinerMachine;

import java.util.List;
import java.util.function.Supplier;

public class VeinCoreMinerLogic extends RecipeLogic {
    public static final Supplier<ItemStack> PICKAXE_TOOL = () -> {
        ItemStack tool = GTMaterialItems.TOOL_ITEMS.get(GTMaterials.Neutronium, GTToolType.PICKAXE).get().get();
        tool.enchant(Enchantments.BLOCK_FORTUNE, 3);
        return tool;
    };

    public VeinCoreMinerLogic(VeinCoreMinerMachine machine) {
        super(machine);
    }

    @Override
    public VeinCoreMinerMachine getMachine() {
        return (VeinCoreMinerMachine) super.getMachine();
    }

    @Override
    public void findAndHandleRecipe() {
        VeinCoreMinerMachine machine = getMachine();
        Level level = machine.getLevel();
        if (level == null) return;
        BlockPos pos = machine.getVeinCorePos();
        BlockState state = level.getBlockState(pos);
        if (VeinCoreMinerMachineMineable.isTierTooHigh(level, pos, state, machine.getTier())) {
            machine.addDisplayText(List.of(Component.translatable("gt_oreminers.multiblock.vein_core_miner.tier_too_high")));
            return;
        };
        List<ItemStack> drops = VeinCoreMinerMachineMineable.getVeinCoreDrops(level, pos, state, PICKAXE_TOOL.get());
        if (drops.isEmpty()) return;

        if (level instanceof ServerLevel) {
            lastRecipe = null;

            GTRecipe recipe = GTRecipeBuilder.ofRaw()
                    .duration(20)
                    .EUt(GTValues.VA[0])
                    .inputFluids(GTMaterials.DrillingFluid.getFluid(2))
                    .outputItems(drops.get(0))
                    .buildRawRecipe();

            if (recipe.matchRecipe(machine).isSuccess() && recipe.matchTickRecipe(machine).isSuccess()) {
                setupRecipe(recipe);
                VeinCoreMinerMachineMineable.depleteVeinCore(level, pos, state);
            }
        }
    }
}
