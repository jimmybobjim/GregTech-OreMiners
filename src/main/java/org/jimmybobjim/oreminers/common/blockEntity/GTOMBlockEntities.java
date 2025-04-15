package org.jimmybobjim.oreminers.common.blockEntity;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.data.GTOMBlocks;

public class GTOMBlockEntities {
    @SuppressWarnings("unchecked")
    public static final BlockEntityEntry<VeinCoreBlockEntity> VEIN_CORE = GTOreMiners.REGISTRATE
            .blockEntity("vein_core", VeinCoreBlockEntity::new)
            .validBlocks(GTOMBlocks.VEIN_CORE_DEFINITIONS.stream().flatMap(GTOMBlocks.VeinCoreDefinition::blocksStream).toArray(BlockEntry[]::new))
            .register();

    public static void init() {}
}
