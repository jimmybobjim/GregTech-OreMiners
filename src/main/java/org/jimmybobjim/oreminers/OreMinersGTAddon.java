package org.jimmybobjim.oreminers;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.data.worldgen.WorldGeneratorUtils;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;
import org.jimmybobjim.oreminers.common.blockEntity.GTOMBlockEntities;
import org.jimmybobjim.oreminers.common.data.GTOMMaterials;
import org.jimmybobjim.oreminers.common.data.GTOMOres;
import org.jimmybobjim.oreminers.vein.generator.VeinCoreGenerator;

@SuppressWarnings("unused")
@GTAddon
public class OreMinersGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GTOreMiners.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        GTOMMaterials.init();
        GTOMBlockEntities.init();
    }

    @Override
    public String addonModId() {
        return GTOreMiners.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
        GTOMTagPrefixes.init();
    }

    @Override
    public void registerVeinGenerators() {
        WorldGeneratorUtils.VEIN_GENERATORS.put(VeinCoreGenerator.ID, VeinCoreGenerator.CODEC);
        WorldGeneratorUtils.VEIN_GENERATOR_FUNCTIONS.put(VeinCoreGenerator.ID, VeinCoreGenerator::new);
    }

    @Override
    public void registerOreVeins() {
        GTOMOres.modify();
    }
}
