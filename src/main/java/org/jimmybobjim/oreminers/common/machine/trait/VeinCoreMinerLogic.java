package org.jimmybobjim.oreminers.common.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.common.block.VeinCoreMinerMachineMineable;
import org.jimmybobjim.oreminers.common.machine.VeinCoreMinerMachine;

import javax.annotation.Nullable;
import java.util.function.Supplier;

// TODO need to search recipe every time
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

    // NOTE getting this warning log after every call:
    //  "Trying to add more outputs than RecipeType can support, id: gtceu:raw, Max itemOutputs: 0"
    //  Everything seems to work, and they don't appear in production so it's probably fine?
    //  This could potentially be GTM's fault too
    public @Nullable GTRecipe getRecipe() {
        VeinCoreMinerMachine machine = getMachine();
        Level level = machine.getLevel();
        if (level == null) return null;
        BlockPos pos = machine.getVeinCorePos();
        BlockState state = level.getBlockState(pos);

        if (machine.checkFlags(level, pos, state)) return null;

        GTRecipe recipe = VeinCoreMinerMachineMineable.getVeinCoreRecipe(level, pos, state, PICKAXE_TOOL.get());
        if (recipe != null && recipe.matchRecipe(machine).isSuccess() && recipe.matchTickRecipe(machine).isSuccess()) {
            return recipe;
        }

        return null;
    }

    @Override
    public void findAndHandleRecipe() {
        lastRecipe = null;
        GTRecipe recipe = getRecipe();
        if (recipe != null) {
            if (machine.alwaysTryModifyRecipe()) {
                GTRecipe modified = machine.fullModifyRecipe(recipe);
                if (modified != null) recipe = modified;
            }

            if (recipe.matchRecipe(machine).isSuccess() && recipe.matchTickRecipe(machine).isSuccess()) {
                setupRecipe(recipe);
            }
        }
    }

    @Override
    public void onRecipeFinish() {
        machine.afterWorking();

        if (lastRecipe != null) {
            lastRecipe.postWorking(machine);
            lastRecipe.handleRecipeIO(IO.OUT, machine, chanceCaches);
        }

        deplete();

        GTRecipe recipe = getRecipe();
        if (recipe != null) {
            if (machine.alwaysTryModifyRecipe()) recipe = machine.fullModifyRecipe(recipe);

            if (recipe.matchRecipe(machine).isSuccess() && recipe.matchTickRecipe(machine).isSuccess()) {
                setupRecipe(recipe);
                return;
            }
        }

        if (suspendAfterFinish) {
            setStatus(Status.SUSPEND);
            suspendAfterFinish = false;
        } else {
            setStatus(Status.IDLE);
        }
        progress = 0;
        duration = 0;
    }

    protected void deplete() {
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
