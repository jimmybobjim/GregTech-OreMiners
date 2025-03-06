package org.jimmybobjim.oreminers.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.machine.trait.VeinCoreMinerLogic;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VeinCoreMinerMachine extends WorkableElectricMultiblockMachine {
    public static final String VEIN_CORE_POS = "VeinCorePos";

    /**
     * the maximum tier of vein core this miner can mine
     */
    private final int tier;

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

    public BlockPos getVeinCorePos() {
        Object pos = getMultiblockState().getMatchContext().get(VEIN_CORE_POS);

        if (pos instanceof BlockPos) {
            return (BlockPos) pos;
        }

        GTOreMiners.LOGGER.warn("missing VeinCore pos");
        return BlockPos.ZERO;
    }
}
