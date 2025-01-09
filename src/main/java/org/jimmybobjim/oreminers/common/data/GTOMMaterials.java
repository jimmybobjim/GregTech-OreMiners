package org.jimmybobjim.oreminers.common.data;

import com.google.common.collect.ImmutableTable;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.MaterialBlockItem;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.level.block.Blocks;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOMPropertyKeys;
import org.jimmybobjim.oreminers.api.propertyKeys.VeinCoreBlockProperty;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;
import org.jimmybobjim.oreminers.common.block.VeinCoreBlock;

import java.util.Map;

public class GTOMMaterials {
    private static final ImmutableTable.Builder<TagPrefix, Material, BlockEntry<VeinCoreBlock>> BUILDER = ImmutableTable.builder();
    public static ImmutableTable<TagPrefix, Material, BlockEntry<VeinCoreBlock>> VEIN_CORES;

    // TODO figure out what to do with the GTRegistrate
    public static void generateVeinCoreBlocks() {
        GTOreMiners.LOGGER.info("generating core blocks...");

        for (MaterialRegistry registry : GTCEuAPI.materialManager.getRegistries()) {
            GTRegistrate registrate = registry.getRegistrate();
            for (Material material : registry.getAllMaterials()) {
                if (material.hasProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK)) {
                    for (Map.Entry<TagPrefix, GTOMTagPrefixes.VeinCoreData> entry : GTOMTagPrefixes.VEIN_CORES.entrySet()) {
                        if (entry.getKey().isIgnored(material)) continue;
                        registerVeinCoreBlock(GTOreMiners.REGISTRATE, material, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    public static void registerVeinCoreBlock(GTRegistrate registrate, Material material, TagPrefix oreTag, GTOMTagPrefixes.VeinCoreData data) {
        GTOreMiners.LOGGER.info("registering core block on material {}", material);

        TagPrefix.OreType oreType = data.oreType();
        String stoneName = data.stoneName();

        BlockEntry<VeinCoreBlock> veinCoreBlock = registrate
                .block(
                        "%s%s_vein_core".formatted(oreTag != GTOMTagPrefixes.stoneVeinCore ? FormattingUtil.toLowerCaseUnder(stoneName) + "_" : "", material.getName()),
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

        BUILDER.put(oreTag, material, veinCoreBlock);
    }

    public static void init() {
        generateVeinCoreBlocks();
        VEIN_CORES = BUILDER.build();
    }

    // TODO add a vein core for all ore veins
    @SuppressWarnings("unused")
    public static void modify(PostMaterialEvent event) {
        GTOreMiners.LOGGER.info("adding GregTech: OreMiners vein core blocks");

        setVeinCore(GTMaterials.Coal, 1);
        setVeinCore(GTMaterials.Redstone, 1);
    }

    public static void setVeinCore(Material material, int level) {
        material.setProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK, new VeinCoreBlockProperty(level));
    }
}
