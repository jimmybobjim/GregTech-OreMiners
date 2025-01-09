package org.jimmybobjim.oreminers.common.blockEntity;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.data.GTOreMinersMaterials;

public class GTOreMinersBlockEntities {
    @SuppressWarnings("unchecked")
    public static final BlockEntityEntry<VeinCoreBlockEntity> VEIN_CORE = GTOreMiners.REGISTRATE
            .blockEntity("vein_core", VeinCoreBlockEntity::new)
            .validBlocks(GTOreMinersMaterials.VEIN_CORES.values().toArray(BlockEntry[]::new))
            .register();

    public static void init() {}
}
