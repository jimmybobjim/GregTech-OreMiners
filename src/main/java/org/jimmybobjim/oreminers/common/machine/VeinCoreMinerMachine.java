package org.jimmybobjim.oreminers.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VeinCoreMinerMachine extends WorkableElectricMultiblockMachine implements IVeinCoreMinerMachine {
    /**
     * the maximum tier of vein core this miner can mine
     */
    @Getter
    private final int tier;

    @Getter
    @Setter
    @ApiStatus.Internal
    private BlockState oldState;

    @Getter
    private BlockPos veinCorePos = null;

    private boolean invalidVeinCoreFlag = false;
    @Setter
    private boolean veinCoreTierTooHighFlag = false;
    private boolean veinCoreDepletedFlag = false;

    public VeinCoreMinerMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
    }

    public boolean canWork() {
        return !invalidVeinCoreFlag && !veinCoreTierTooHighFlag && !veinCoreDepletedFlag;
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
        List<Component> errors = new ArrayList<>();
        addErrors(errors);
        textList.addAll(errors);
        // this includes !isFormed, so we don't need to check for it again later
        if (!errors.isEmpty()) return;

        MultiblockDisplayText.builder(textList, true)
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addEnergyUsageLine(energyContainer)
                .addWorkingStatusLine()
                .addProgressLine(recipeLogic.getProgress(), recipeLogic.getMaxProgress(), recipeLogic.getProgressPercent());

        textList.add(Component.literal("---------------").withStyle(ChatFormatting.GRAY));
        addVeinCoreData(textList);
    }

    protected void addErrors(List<Component> errors) {
        if (!isFormed) errors.add(Component.translatable("gtceu.multiblock.invalid_structure").withStyle(ChatFormatting.RED));
        if (invalidVeinCoreFlag) errors.add(Component.translatable("gt_oreminers.multiblock.pattern.error.invalid_block").withStyle(ChatFormatting.RED));
        if (veinCoreTierTooHighFlag) errors.add(Component.translatable("gt_oreminers.multiblock.vein_core_miner.tier_too_high").withStyle(ChatFormatting.RED));
        if (veinCoreDepletedFlag) errors.add(Component.translatable("gt_oreminers.multiblock.vein_core_miner.depleted").withStyle(ChatFormatting.RED));
    }

    private void addVeinCoreData(List<Component> textList) {
        Level level = getLevel();
        @Nullable BlockPos pos = veinCorePos;
        if (level == null || pos == null) return;
        BlockState state = level.getBlockState(pos);
        VeinCoreMinerMachineMineable.addVeinCoreDisplayText(level, pos, state, textList);
    }

    @Override
    @ApiStatus.Internal
    public void setVeinCorePos(BlockPos veinCorePos) {
        this.veinCorePos = veinCorePos;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updatePattern(BlockPos pos, BlockState oldState, BlockState newState) {
        invalidVeinCoreFlag = newState.isAir() || newState.liquid();
        veinCoreDepletedFlag = getLevel() == null || VeinCoreMinerMachineMineable.isVeinCoreDepleted(getLevel(), pos, newState);

        getRecipeLogic().changeVeinCore();
    }
}
