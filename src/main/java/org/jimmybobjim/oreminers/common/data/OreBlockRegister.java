package org.jimmybobjim.oreminers.common.data;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.MaterialBlockItem;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.level.block.Blocks;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOreMinersPropertyKeys;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOreMinersTagPrefixes;
import org.jimmybobjim.oreminers.common.block.VeinCoreBlock;

import java.util.Map;

public class OreBlockRegister {


    // TODO figure out what to do with the GTRegistrate
    public static void generateCoreBlocks() {
        GTOreMiners.LOGGER.info("generating core blocks...");

        for (MaterialRegistry registry : GTCEuAPI.materialManager.getRegistries()) {
            GTRegistrate registrate = registry.getRegistrate();
            for (Material material : registry.getAllMaterials()) {
                if (material.hasProperty(GTOreMinersPropertyKeys.VEIN_CORE_BLOCK)) {
                    for (Map.Entry<TagPrefix, GTOreMinersTagPrefixes.VeinCoreData> entry : GTOreMinersTagPrefixes.VEIN_CORES.entrySet()) {
                        if (entry.getKey().isIgnored(material)) continue;
                        registerCoreBlock(GTOreMiners.REGISTRATE, material, entry.getKey(), entry.getValue().oreType(), entry.getValue().stoneName());
                    }
                }
            }
        }
    }

    public static void registerCoreBlock(GTRegistrate registrate, Material material, TagPrefix oreTag, TagPrefix.OreType oreType, String oreName) {
        GTOreMiners.LOGGER.info("registering core block on material {}", material);

        BlockEntry<VeinCoreBlock> coreBlock = registrate
                .block(
                        "%s%s_vein_core".formatted(oreTag != GTOreMinersTagPrefixes.stoneVeinCore ? FormattingUtil.toLowerCaseUnder(oreName) + "_" : "", material.getName()),
                        properties -> new VeinCoreBlock(properties, oreTag, material, true)
                )
                .initialProperties( () -> {
                    if (oreType.stoneType().get().isAir()) {
                        return Blocks.IRON_BLOCK;
                    } else {
                        return oreType.stoneType().get().getBlock();
                    }
                })
                .properties(properties -> GTBlocks.copy(oreType.template().get(), properties).strength(55, 1200).noLootTable())
                .transform(GTBlocks.unificationBlock(oreTag, material))
                .blockstate(NonNullBiConsumer.noop())
                .setData(ProviderType.LANG, NonNullBiConsumer.noop())
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop())
                .color(() -> MaterialBlock::tintedColor)
                .item(MaterialBlockItem::create)
                .onRegister(MaterialBlockItem::onRegister)
                .model(NonNullBiConsumer.noop())
                .color(() -> MaterialBlockItem::tintColor)
                .build()
                .register();
    }
}
