package org.jimmybobjim.oreminers.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

    private boolean invalidFlag = false;
    @Setter
    private boolean veinCoreTierTooHighFlag = false;

    public VeinCoreMinerMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
    }

    public boolean canWork() {
        return !invalidFlag && !veinCoreTierTooHighFlag;
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
            addErrors(data);
            if (data.isEmpty()) addVeinCoreData(data);
        }

        if (!data.isEmpty()) {
            textList.add(Component.literal("---------------"));
            textList.addAll(data);
        }
    }

    protected void addErrors(List<Component> data) {
        if (invalidFlag) data.add(Component.translatable("gt_oreminers.multiblock.pattern.error.invalid_block").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        if (veinCoreTierTooHighFlag) data.add(Component.translatable("gt_oreminers.multiblock.vein_core_miner.tier_too_high").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
    }

    private void addVeinCoreData(List<Component> data) {
        Level level = getLevel();
        @Nullable BlockPos pos = veinCorePos;
        if (level == null || pos == null) return;
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
}
