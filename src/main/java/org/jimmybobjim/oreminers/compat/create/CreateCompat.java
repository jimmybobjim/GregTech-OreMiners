package org.jimmybobjim.oreminers.compat.create;

import com.simibubi.create.content.contraptions.ContraptionMovementSetting;
import org.jimmybobjim.oreminers.common.data.GTOMMaterials;

public class CreateCompat {
    public static void init() {
        GTOMMaterials.VEIN_CORES.values().forEach(entry ->
                ContraptionMovementSetting.register(entry.get(), () -> ContraptionMovementSetting.UNMOVABLE));
    }
}
