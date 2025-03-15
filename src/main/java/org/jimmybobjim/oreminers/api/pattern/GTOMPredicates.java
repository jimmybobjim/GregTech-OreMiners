package org.jimmybobjim.oreminers.api.pattern;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.machine.IVeinCoreMinerMachine;

public class GTOMPredicates {
    public static TraceabilityPredicate veinCoreBlock() {
        return new TraceabilityPredicate(multiblockState -> {
            if (!(multiblockState.getController() instanceof IVeinCoreMinerMachine machine)) {
                GTOreMiners.LOGGER.error("veinCoreBlock TraceabilityPredicate used on unsupported machine");
                return false;
            }

            BlockState newState = multiblockState.getBlockState();
            BlockState oldState = machine.getOldState();
            BlockPos pos = multiblockState.getPos();

            machine.setVeinCorePos(pos);

            if (!newState.equals(oldState)) {
                machine.updatePattern(pos, oldState, newState);
            }
            machine.setOldState(newState);

            return true;
        }, null);
    }
}
