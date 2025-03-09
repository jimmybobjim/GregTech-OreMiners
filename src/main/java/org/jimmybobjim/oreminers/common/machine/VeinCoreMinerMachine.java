package org.jimmybobjim.oreminers.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jimmybobjim.oreminers.common.block.VeinCoreMinerMachineMineable;
import org.jimmybobjim.oreminers.common.machine.trait.VeinCoreMinerLogic;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VeinCoreMinerMachine extends WorkableElectricMultiblockMachine implements IVeinCoreMinerMachine {
    /**
     * the maximum tier of vein core this miner can mine
     */
    private final int tier;

    @Getter
    @Setter
    @ApiStatus.Internal
    private BlockState oldState;

    @Getter
    private BlockPos veinCorePos = null;

    private boolean invalidFlag = false;
    @Setter
    private boolean tierTooHighFlag = false;

    public VeinCoreMinerMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new VeinCoreMinerLogic(this);
    }

    @Override
    public VeinCoreMinerLogic getRecipeLogic() {
        return (VeinCoreMinerLogic) super.getRecipeLogic();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        List<Component> data = new ArrayList<>();
        if (isFormed()) {
            if (invalidFlag) data.add(Component.translatable("gt_oreminers.multiblock.pattern.error.invalid_block"));
            if (tierTooHighFlag) data.add(Component.translatable("gt_oreminers.multiblock.vein_core_miner.tier_too_high"));
            if (data.isEmpty()) addVeinCoreData(data);
        }

        if (!data.isEmpty()) {
            textList.add(Component.literal("---------------"));
            textList.addAll(data);
        }
    }

    private void addVeinCoreData(List<Component> data) {
        Level level = getLevel();
        @Nullable BlockPos pos = veinCorePos;
        if (level == null || pos == null)  return;
        BlockState state = level.getBlockState(pos);
        data.add(state.getBlock().getName());
        if (state.getBlock() instanceof VeinCoreMinerMachineMineable veinCore) {
            veinCore.addDisplayText(level, pos, state, data);
        }
    }

    @Override
    @ApiStatus.Internal
    public void setVeinCorePos(BlockPos veinCorePos) {
        this.veinCorePos = veinCorePos;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updatePattern(BlockPos pos, BlockState oldState, BlockState newState) {
        invalidFlag = newState.isAir() || newState.liquid();

        getRecipeLogic().changeVeinCore();
    }

    /**
     * Vein core miners exist for every other tier
     * (miners above tier 3 are not added in this mod)
     */
    public static int veinCoreTierToVoltageTier(int veinCoreTier) {
        return veinCoreTier * 2;
    }
}
