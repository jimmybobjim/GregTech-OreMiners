package org.jimmybobjim.oreminers;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jimmybobjim.oreminers.common.commands.GTOMCommands;
import org.jimmybobjim.oreminers.common.data.GTOMDatagen;
import org.jimmybobjim.oreminers.common.data.machines.GTOMMultiMachines;
import org.jimmybobjim.oreminers.compat.create.CreateCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GTOreMiners.MOD_ID)
public class GTOreMiners {
    public static final String MOD_ID = "gt_oreminers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GTOreMiners.MOD_ID);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public GTOreMiners(FMLJavaModLoadingContext context) {
        GTOMDatagen.init();
        REGISTRATE.registerRegistrate();

        IEventBus eventBus = context.getModEventBus();

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::addMaterialRegistries);
        eventBus.addGenericListener(MachineDefinition.class, this::registerMachines);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(GTOMCommands::register);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (LDLib.isModLoaded("create")) {
                CreateCompat.init();
            }
        });
    }

    private void addMaterialRegistries(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(GTOreMiners.MOD_ID);
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GTOMMultiMachines.init();
    }
}
