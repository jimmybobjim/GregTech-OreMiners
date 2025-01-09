package org.jimmybobjim.oreminers.api.propertyKeys;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

public class VeinCoreBlockProperty implements IMaterialProperty {
    private final int level;

    /**
     *
     * @param level the tier core block miner required to extract from this vein core
     */
    public VeinCoreBlockProperty(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.ORE, true);
    }
}
