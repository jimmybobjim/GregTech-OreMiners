package org.jimmybobjim.oreminers.api.propertyKeys;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

/**
 * @param veinCoreTier The tier of vein core block miner required to extract from this vein core
 * @param EUt The EU/t consumed when extracting (generally using {@link com.gregtechceu.gtceu.api.GTValues#VA})
 * @param duration The duration of the recipe
 * @param totalDrops How many times the vein core can be extracted from until it fully depletes (-1 for infinite)
 */
public record VeinCoreBlockProperty(
        int veinCoreTier,
        int EUt,
        int duration,
        int totalDrops
) implements IMaterialProperty {
    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.ORE, true);
    }
}
