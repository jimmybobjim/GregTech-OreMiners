package org.jimmybobjim.oreminers.common.data;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTOres;
import org.jimmybobjim.oreminers.vein.generator.VeinCoreGenerator;

public class GTOMOres {
    // TODO flesh out all the ore veins
    public static void modify() {
        VeinCoreGenerator.overrideGenerator(GTOres.COAL_VEIN, GTMaterials.Coal);
    }
}
