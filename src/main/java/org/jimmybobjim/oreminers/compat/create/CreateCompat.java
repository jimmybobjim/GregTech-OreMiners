package org.jimmybobjim.oreminers.compat.create;

import com.simibubi.create.content.contraptions.ContraptionMovementSetting;
import org.jimmybobjim.oreminers.common.data.GTOMBlocks;

public class CreateCompat {
    public static void init() {
        GTOMBlocks.VEIN_CORE_DEFINITIONS.stream().flatMap(GTOMBlocks.VeinCoreDefinition::blocksStream).forEach(entry ->
                ContraptionMovementSetting.register(entry.get(), () -> ContraptionMovementSetting.UNMOVABLE));
    }
}
