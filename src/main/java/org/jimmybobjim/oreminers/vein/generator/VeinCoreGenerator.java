package org.jimmybobjim.oreminers.vein.generator;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.data.worldgen.generator.veins.NoopVeinGenerator;
import com.gregtechceu.gtceu.api.data.worldgen.ores.OreBlockPlacer;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VeinCoreGenerator extends VeinGenerator {
    public static final ResourceLocation ID = GTOreMiners.id("core");

    public static final Codec<VeinCoreGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DIRECT_CODEC.fieldOf("generator").forGetter(val -> val.generator),
            Codec.either(BlockState.CODEC, GTCEuAPI.materialManager.codec()).fieldOf("core").forGetter(val -> val.core)
    ).apply(instance, VeinCoreGenerator::new));

    public static GTOreDefinition overrideGenerator(GTOreDefinition vein, Either<BlockState, Material> core) {
        return vein.veinGenerator(new VeinCoreGenerator(vein.veinGenerator(), core));
    }

    public static GTOreDefinition overrideGenerator(GTOreDefinition vein, BlockState core) {
        return vein.veinGenerator(new VeinCoreGenerator(vein.veinGenerator(), Either.left(core)));
    }

    public static GTOreDefinition overrideGenerator(GTOreDefinition vein, Material core) {
        return vein.veinGenerator(new VeinCoreGenerator(vein.veinGenerator(), Either.right(core)));
    }

    private VeinGenerator generator = NoopVeinGenerator.INSTANCE;
    private Either<BlockState, Material> core = Either.left(Blocks.AIR.defaultBlockState());

    public VeinCoreGenerator(GTOreDefinition gtOreDefinition) {
        super(gtOreDefinition);
    }

    @Override
    public List<Map.Entry<Either<BlockState, Material>, Integer>> getAllEntries() {
        List<Map.Entry<Either<BlockState, Material>, Integer>> entries = generator.getAllEntries();
        entries.add(Map.entry(core, 1));
        return entries;
    }

    @Override
    public Map<BlockPos, OreBlockPlacer> generate(WorldGenLevel level, RandomSource random, GTOreDefinition entry, BlockPos origin) {
        Map<BlockPos, OreBlockPlacer> generatedBlocks = generator.generate(level, random, entry, origin);
        placeCore(generatedBlocks, origin, core);
        return generatedBlocks;
    }

    private static void placeCore(Map<BlockPos, OreBlockPlacer> generatedBlocks, BlockPos origin, Either<BlockState, Material> core) {
        int x = SectionPos.sectionRelative(origin.getX());
        int y = SectionPos.sectionRelative(origin.getY());
        int z = SectionPos.sectionRelative(origin.getZ());

        generatedBlocks.put(origin, (access, section) -> {
            core.ifLeft(state -> {
                section.setBlockState(x, y, z, state, false);
            }).ifRight(material -> {
                BlockState currentState = access.getBlockState(origin);
                Optional<TagPrefix> oreTag = ChemicalHelper.getOrePrefix(currentState);
                if (oreTag.isEmpty()) return;
                TagPrefix veinCoreTag = GTOMTagPrefixes.VEIN_CORES_FROM_ORE.get(oreTag.get());
                Block block = ChemicalHelper.getBlock(veinCoreTag, material);
                if (block == null || block.defaultBlockState().isAir()) return;
                BlockState state = block.defaultBlockState();
                section.setBlockState(x, y, z, state, false);
            });
        });
    }

    @Override
    public VeinGenerator build() {
        VeinGenerator built = generator.build();
        return built == null ? null : new VeinCoreGenerator(built, core);
    }

    @Override
    public VeinGenerator copy() {
        VeinGenerator generatorCopy = generator.copy();
        return generatorCopy == null ? null : new VeinCoreGenerator(generatorCopy, core);
    }

    @Override
    public Codec<? extends VeinGenerator> codec() {
        return CODEC;
    }
}
