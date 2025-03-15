package org.jimmybobjim.oreminers.common.machine.trait;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.common.block.VeinCoreMinerMachineMineable;
import org.jimmybobjim.oreminers.common.machine.VeinCoreMinerMachine;

import java.util.function.Supplier;

// FIXME getting this error message after every cycle:
//  "Trying to add more outputs than RecipeType can support, id: gtceu:raw, Max itemOutputs: 0"
//  Everything seems to work, but it should still probably be fixed to not pollute the logs
// FIXME recipe idles for a single tick after it finishes
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

        machine.setVeinCoreTierTooHighFlag(
                VeinCoreMinerMachineMineable.isVeinCoreTierTooHigh(level, pos, state, machine.getTier()));

        if (!machine.canWork()) return;

        if (level instanceof ServerLevel) {
            GTRecipe recipe = VeinCoreMinerMachineMineable.getVeinCoreRecipe(level, pos, state, PICKAXE_TOOL.get());
            if (recipe == null) return;
            lastRecipe = null;
            if (recipe.matchRecipe(machine).isSuccess() && recipe.matchTickRecipe(machine).isSuccess()) {
                setupRecipe(recipe);
            }
        }
    }

    @Override
    public void onRecipeFinish() {
        super.onRecipeFinish();

        VeinCoreMinerMachine machine = getMachine();
        Level level = machine.getLevel();
        if (level == null) return;
        BlockPos pos = machine.getVeinCorePos();
        BlockState state = level.getBlockState(pos);
        VeinCoreMinerMachineMineable.depleteVeinCore(level, pos, state);
    }

    public void changeVeinCore() {
        resetRecipeLogic();
    }
}
