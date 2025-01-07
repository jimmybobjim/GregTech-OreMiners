package org.jimmybobjim.oreminers.common.block;

import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.lowdragmc.lowdraglib.Platform;
import org.jimmybobjim.oreminers.client.render.VeinCoreBlockRenderer;

public class VeinCoreBlock extends MaterialBlock {

    public VeinCoreBlock(Properties properties, TagPrefix tagPrefix, Material material, boolean registerModel) {
        super(properties, tagPrefix, material, false);

        if (registerModel && Platform.isClient()) {
            VeinCoreBlockRenderer.create(this);
        }
    }
}
