package org.jimmybobjim.oreminers.api.pattern;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jimmybobjim.oreminers.common.machine.VeinCoreMinerMachine;

public class GTOMPredicates {
    public static TraceabilityPredicate veinCoreBlock() {
        return new TraceabilityPredicate(multiblockState -> {
            BlockPos pos = multiblockState.getPos();
            BlockPos currentPos = multiblockState.getMatchContext().getOrPut(VeinCoreMinerMachine.VEIN_CORE_POS, pos);
            if (!currentPos.equals(pos)) {
                multiblockState.setError(new PatternStringError("gt_oreminers.multiblock.pattern.error.vein_core"));
                return false;
            }
            return true;
        }, null).addTooltips(Component.translatable("gt_oreminers.multiblock.pattern.error.vein_core"));
    }
}
