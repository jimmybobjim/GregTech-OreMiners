package org.jimmybobjim.oreminers;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;
import org.jimmybobjim.oreminers.common.blockEntity.GTOMBlockEntities;
import org.jimmybobjim.oreminers.common.data.GTOMMaterials;

import java.util.function.Consumer;

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
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        //CustomRecipes.init(provider);
    }

    @Override
    public void registerVeinGenerators() {
        IGTAddon.super.registerVeinGenerators();
    }

    // If you have custom ingredient types, uncomment this & change to match your capability.
    // KubeJS WILL REMOVE YOUR RECIPES IF THESE ARE NOT REGISTERED.
    /*
    public static final ContentJS<Double> PRESSURE_IN = new ContentJS<>(NumberComponent.ANY_DOUBLE, GregitasRecipeCapabilities.PRESSURE, false);
    public static final ContentJS<Double> PRESSURE_OUT = new ContentJS<>(NumberComponent.ANY_DOUBLE, GregitasRecipeCapabilities.PRESSURE, true);

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(CustomRecipeCapabilities.PRESSURE, Pair.of(PRESSURE_IN, PRESSURE_OUT));
    }
    */
}
