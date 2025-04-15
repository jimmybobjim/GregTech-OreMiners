package org.jimmybobjim.oreminers.common.data;

import org.jimmybobjim.oreminers.vein.generator.VeinCoreGenerator;

public class GTOMOres {
    public static void modify() {
        GTOMBlocks.VEIN_CORE_DEFINITIONS.forEach(definition -> definition.getGenerators().forEach(
                generator -> VeinCoreGenerator.replaceGenerator(
                        generator.get(), definition.getBlockStates())
                )
        );
    }
}
