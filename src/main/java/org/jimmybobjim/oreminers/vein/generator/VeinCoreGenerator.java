package org.jimmybobjim.oreminers.vein.generator;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.data.worldgen.generator.veins.NoopVeinGenerator;
import com.gregtechceu.gtceu.api.data.worldgen.ores.OreBlockPlacer;
import com.lowdragmc.lowdraglib.Platform;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.GTOreMiners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VeinCoreGenerator extends VeinGenerator {
    public static final ResourceLocation ID = GTOreMiners.id("core");

    public static final Codec<VeinCoreGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DIRECT_CODEC.fieldOf("generator").forGetter(VeinCoreGenerator::getGenerator),
            Codec.unboundedMap(TagPrefix.CODEC, BlockState.CODEC).fieldOf("").forGetter(VeinCoreGenerator::getCores)
    ).apply(instance, VeinCoreGenerator::new));

    public static void replaceGenerator(GTOreDefinition definition, Map<TagPrefix, BlockState> veinCores) {
        definition.veinGenerator(new VeinCoreGenerator(definition.veinGenerator(), veinCores));
    }

    private VeinGenerator generator = NoopVeinGenerator.INSTANCE;
    private Map<TagPrefix, BlockState> cores = new HashMap<>();

    public VeinCoreGenerator(GTOreDefinition gtOreDefinition) {
        super(gtOreDefinition);
    }

    @Override
    public List<Map.Entry<Either<BlockState, Material>, Integer>> getAllEntries() {
        List<Map.Entry<Either<BlockState, Material>, Integer>> entries = generator.getAllEntries();
        entries.add(Map.entry(Either.left(cores.get(TagPrefix.ore)), 1));
        return entries;
    }

    @Override
    public Map<BlockPos, OreBlockPlacer> generate(WorldGenLevel level, RandomSource random, GTOreDefinition entry, BlockPos origin) {
        Map<BlockPos, OreBlockPlacer> generatedBlocks = generator.generate(level, random, entry, origin);
        placeCore(generatedBlocks, origin, cores);
        return generatedBlocks;
    }

    private static void placeCore(Map<BlockPos, OreBlockPlacer> generatedBlocks, BlockPos origin, Map<TagPrefix, BlockState> core) {
        if (Platform.isDevEnv()) GTOreMiners.LOGGER.info("placed vein core at ({})", origin.toShortString());

        int x = SectionPos.sectionRelative(origin.getX());
        int y = SectionPos.sectionRelative(origin.getY());
        int z = SectionPos.sectionRelative(origin.getZ());

        generatedBlocks.put(origin, (access, section) ->
                ChemicalHelper.getOrePrefix(section.getBlockState(x, y, z)).ifPresentOrElse(
                        oreTag -> section.setBlockState(x, y, z, core.get(oreTag), false),
                        () -> {
                            if (Platform.isDevEnv()) GTOreMiners.LOGGER.info("failed to place vein core");
                        }));
    }

    @Override
    public VeinGenerator build() {
        VeinGenerator built = generator.build();
        return built == null ? null : new VeinCoreGenerator(built, cores);
    }

    @Override
    public VeinGenerator copy() {
        VeinGenerator generatorCopy = generator.copy();
        return generatorCopy == null ? null : new VeinCoreGenerator(generatorCopy, cores);
    }

    @Override
    public Codec<? extends VeinGenerator> codec() {
        return CODEC;
    }
}
