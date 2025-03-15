package org.jimmybobjim.oreminers.common.data.machines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterialBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.BedrockOreMinerMachine;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.machine.VeinCoreMinerMachine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static org.jimmybobjim.oreminers.api.pattern.GTOMPredicates.veinCoreBlock;

public class GTOMMultiMachines {
    public static void init() {
        GTOreMiners.LOGGER.info("init GT: OreMiners multimachines");
    }

    public static final MultiblockMachineDefinition TIER_1_VEIN_CORE_MINER = GTOreMiners.REGISTRATE
            .multiblock("tier_1_vein_core_miner", blockEntity -> new VeinCoreMinerMachine(blockEntity, 1))
            .rotationState(RotationState.NON_Y_AXIS)
            .langValue("Tier 1 Vein Core Miner")
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("F F", "F F", "XXX", " C ", " F ", " F ", "   ", "   ")
                    .aisle("   ", " V ", "XCX", "CCC", "FCF", "FCF", " F ", " F ")
                    .aisle("F F", "F F", "XSX", " C ", " F ", " F ", "   ", "   ")
                    .where('S', controller(blocks(definition.get())))
                    .where('X', blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setMaxGlobalLimited(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setMaxGlobalLimited(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(1))
                    )
                    .where('C', blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                    .where('F', blocks(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.frameGt, GTMaterials.Steel).get()))
                    .where('V', veinCoreBlock())
                    .build())
            .workableCasingRenderer(BedrockOreMinerMachine.getBaseTexture(1),
                    GTCEu.id("block/multiblock/bedrock_ore_miner"))
            .register();
}
