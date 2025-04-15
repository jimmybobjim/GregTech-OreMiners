package org.jimmybobjim.oreminers.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import lombok.Getter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.block.VeinCoreBlock;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gregtechceu.gtceu.common.data.GTOres.*;

// TODO flesh out all the recipe data
@SuppressWarnings("unused")
public class GTOMBlocks {
    public static Set<VeinCoreDefinition> VEIN_CORE_DEFINITIONS = new HashSet<>();

    public static class VeinCoreDefinition {
        private final Map<TagPrefix, BlockEntry<VeinCoreBlock>> blocks = new HashMap<>();
        private final List<Supplier<GTOreDefinition>> generators;

        @Getter
        private final GTRegistrate registrate;
        @Getter
        private final String veinName;
        @Getter
        private final VeinCoreBlock.RecipeData recipeData;

        @SafeVarargs
        private VeinCoreDefinition(GTRegistrate registrate, String veinName, VeinCoreBlock.RecipeData recipeData, Supplier<GTOreDefinition>... generators) {
            if (generators.length == 0) throw new IllegalArgumentException("no generators specified");

            this.registrate = registrate;
            this.veinName = veinName;
            this.recipeData = recipeData;
            this.generators = new ArrayList<>(List.of(generators));
            TagPrefix.ORES.keySet().forEach(tagPrefix -> blocks.put(tagPrefix, registerVeinCoreBlock(this, tagPrefix)));

            VEIN_CORE_DEFINITIONS.add(this);
        }

        public Map<TagPrefix, BlockEntry<VeinCoreBlock>> getBlocks() {
            return ImmutableMap.copyOf(blocks);
        }

        public Map<TagPrefix, BlockState> getBlockStates() {
            return blocks.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getDefaultState()
            ));
        }

        public Stream<BlockEntry<VeinCoreBlock>> blocksStream() {
            return blocks.values().stream();
        }

        public BlockEntry<VeinCoreBlock> getBlock(TagPrefix tagPrefix) {
            return blocks.get(tagPrefix);
        }

        public List<Supplier<GTOreDefinition>> getGenerators() {
            return ImmutableList.copyOf(generators);
        }
    }

    public static VeinCoreDefinition BAUXITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "bauxite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> BAUXITE_VEIN_END
    );

    public static VeinCoreDefinition MAGNETITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "magnetite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> MAGNETITE_VEIN_END, () -> MAGNETITE_VEIN_OW
    );

    public static VeinCoreDefinition NAQUADA_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "naquadah",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> NAQUADAH_VEIN
    );

    public static VeinCoreDefinition PITCHBLENDE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "pitchblende",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> PITCHBLENDE_VEIN
    );

    public static VeinCoreDefinition SCHEELITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "scheelite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> SCHEELITE_VEIN
    );

    public static VeinCoreDefinition SHELDONITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "sheldonite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> SHELDONITE_VEIN
    );

    public static VeinCoreDefinition BANDED_IRON_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "banded_iron",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> BANDED_IRON_VEIN
    );

    public static VeinCoreDefinition BERYLLIUM_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "beryllium",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> BERYLLIUM_VEIN
    );

    public static VeinCoreDefinition CERTUS_QUARTZ_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "certus_quartz",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> CERTUS_QUARTZ_VEIN
    );

    public static VeinCoreDefinition MANGANESE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "manganese",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> MANGANESE_VEIN
    );

    public static VeinCoreDefinition MOLYBDENUM_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "molybdenum",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> MOLYBDENUM_VEIN
    );

    public static VeinCoreDefinition MONAZITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "monazite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> MONAZITE_VEIN
    );

    public static VeinCoreDefinition NETHER_QUARTZ_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "nether_quartz",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> NETHER_QUARTZ_VEIN
    );

    public static VeinCoreDefinition REDSTONE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "redstone",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> REDSTONE_VEIN, () -> REDSTONE_VEIN_OW
    );

    public static VeinCoreDefinition SALTPETER_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "saltpeter",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> SALTPETER_VEIN
    );

    public static VeinCoreDefinition SULFUR_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "sulfur",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> SULFUR_VEIN
    );

    public static VeinCoreDefinition TETRAHEDRITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "tetrahedrite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> TETRAHEDRITE_VEIN
    );

    public static VeinCoreDefinition TOPAZ_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "topaz",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> TOPAZ_VEIN
    );

    public static VeinCoreDefinition APATITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "apatite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> APATITE_VEIN
    );

    public static VeinCoreDefinition CASSITERITE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "cassiterite",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> CASSITERITE_VEIN
    );

    public static VeinCoreDefinition COAL_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "coal",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 0, 100_000),
            () -> COAL_VEIN
    );

    public static VeinCoreDefinition COPPER_TIN_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "copper_tin",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> COPPER_TIN_VEIN
    );

    public static VeinCoreDefinition GALENA_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "galena",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> GALENA_VEIN
    );

    public static VeinCoreDefinition GARNET_TIN_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "garnet_tin",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> GARNET_TIN_VEIN
    );

    public static VeinCoreDefinition GARNET_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "garnet",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> GARNET_VEIN
    );

    public static VeinCoreDefinition IRON_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "iron",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> IRON_VEIN
    );

    public static VeinCoreDefinition LUBRICANT_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "lubricant",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> LUBRICANT_VEIN
    );

    public static VeinCoreDefinition MINERAL_SAND_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "mineral_sand",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> MINERAL_SAND_VEIN
    );

    public static VeinCoreDefinition NICKEL_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "nickel",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> NICKEL_VEIN
    );

    public static VeinCoreDefinition SALTS_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "salts",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> SALTS_VEIN
    );

    public static VeinCoreDefinition OILSANDS_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "oilsands",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> OILSANDS_VEIN
    );

    public static VeinCoreDefinition DIAMOND_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "diamond",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> DIAMOND_VEIN
    );

    public static VeinCoreDefinition COPPER_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "copper",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> COPPER_VEIN
    );

    public static VeinCoreDefinition LAPIS_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "lapis",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> LAPIS_VEIN
    );

    public static VeinCoreDefinition MICA_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "mica",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> MICA_VEIN
    );

    public static VeinCoreDefinition OLIVINE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "olivine",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> OLIVINE_VEIN
    );

    public static VeinCoreDefinition SAPPHIRE_VEIN_CORES = addVeinCore(GTOreMiners.REGISTRATE, "sapphire",
            new VeinCoreBlock.RecipeData(1, GTValues.VA[1], 20, 100_000),
            () -> SAPPHIRE_VEIN
    );


    @SafeVarargs
    public static VeinCoreDefinition addVeinCore(GTRegistrate registrate, String veinName, VeinCoreBlock.RecipeData recipeData, Supplier<GTOreDefinition>... generators) {
        return new VeinCoreDefinition(registrate, veinName, recipeData, generators);
    }

    public static BlockEntry<VeinCoreBlock> registerVeinCoreBlock(VeinCoreDefinition definition, TagPrefix oreTag) {
        return registerVeinCoreBlock(
                definition.registrate,
                () -> definition.generators.get(0).get().veinGenerator(),
                "%s%s_vein_core".formatted(oreTag == TagPrefix.ore ? "" : FormattingUtil.toLowerCaseUnder(oreTag.name()) + "_", definition.veinName),
                definition.recipeData,
                oreTag);
    }

    public static BlockEntry<VeinCoreBlock> registerVeinCoreBlock(GTRegistrate registrate, Supplier<VeinGenerator> generator, String name, VeinCoreBlock.RecipeData recipeData, TagPrefix oreTag) {
        TagPrefix.OreType oreType = TagPrefix.ORES.get(oreTag);
        return registrate.block(name, properties -> new VeinCoreBlock(properties, oreTag, generator, recipeData, true))
                .initialProperties( () -> {
                    if (oreType.stoneType().get().isAir()) {
                        return Blocks.IRON_BLOCK;
                    } else {
                        return oreType.stoneType().get().getBlock();
                    }
                })
                .properties(properties -> GTBlocks.copy(oreType.template().get(), properties).strength(55, 1200).noLootTable())
                .blockstate(NonNullBiConsumer.noop())
                .setData(ProviderType.LANG, NonNullBiConsumer.noop())
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop())
                .color(() -> VeinCoreBlock::getBlockTint)
                .item(BlockItem::new)
                .model(NonNullBiConsumer.noop())
                .color(() -> VeinCoreBlock::getItemTint)
                .build()
                .register();
    }
}
