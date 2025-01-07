package org.jimmybobjim.oreminers.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.api.propertyKeys.VeinCoreBlockProperty;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOreMinersPropertyKeys;

public class GTOreMinersMaterials {
    // TODO add a vein core for all ore veins
    public static void modify (PostMaterialEvent event) {
        GTOreMiners.LOGGER.info("adding GregTech: OreMiners vein core blocks");

        setVeinCore(GTMaterials.Coal, 1);
        setVeinCore(GTMaterials.Redstone, 1);
    }

    public static void setVeinCore(Material material, int level) {
        material.setProperty(GTOreMinersPropertyKeys.VEIN_CORE_BLOCK, new VeinCoreBlockProperty(level));
    }
}
