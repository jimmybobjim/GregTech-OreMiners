package org.jimmybobjim.oreminers.common.block;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.Platform;
import lombok.Getter;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.client.render.VeinCoreBlockRenderer;
import org.jimmybobjim.oreminers.common.blockEntity.VeinCoreBlockEntity;
import org.jimmybobjim.oreminers.common.machine.trait.VeinCoreMinerLogic;
import org.jimmybobjim.oreminers.util.Util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class VeinCoreBlock extends Block implements EntityBlock, VeinCoreMinerMachineMineable {
    /**
     * @param veinCoreTier The tier of vein core block miner required to extract from this vein core
     * @param EUt The EU/t consumed when extracting (generally using {@link com.gregtechceu.gtceu.api.GTValues#VA})
     * @param duration The duration of the recipe
     * @param totalDrops How many times the vein core can be extracted from until it fully depletes (-1 for infinite)
     */
    public record RecipeData(
            int veinCoreTier,
            int EUt,
            int duration,
            int totalDrops
    ) {}

    public static final DecimalFormat purityFormat = new DecimalFormat("00.00");
    public static final DecimalFormat remainingFormat = new DecimalFormat("00.000");

    /**
     * @return The tint color of the most common material in the vein generator
     */
    @OnlyIn(Dist.CLIENT)
    public static ItemColor getItemTint() {
        return (itemstack, index) -> {
            if (itemstack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof VeinCoreBlock veinCoreBlock) {
                return veinCoreBlock.getMaxMaterial()
                        .map(material -> material.getLayerARGB(index))
                        .orElse(-1);
            }
            return -1;
        };
    }

    /**
     * @return The tint color of the most common material in the vein generator
     */
    @OnlyIn(Dist.CLIENT)
    public static BlockColor getBlockTint() {
        return (state, getter, pos, index) -> {
            if (state.getBlock() instanceof VeinCoreBlock veinCoreBlock) {
                return veinCoreBlock.getMaxMaterial()
                        .map(material -> material.getLayerARGB(index))
                        .orElse(-1);
            }
            return -1;
        };
    }

    // do not access any of these fields directly if they have a `set` boolean, use the associated getter instead
    // things will probably break and suck to debug
    private final List<Map.Entry<List<ItemStack>, Integer>> drops = new ArrayList<>();
    private int maxDropsChance;
    private boolean dropsSet = false;

    private Optional<Material> maxMaterial = Optional.empty();
    private boolean maxMaterialSet = false;

    @Getter
    private final RecipeData recipeData;
    @Getter
    private final TagPrefix oreTag;
    private final Supplier<VeinGenerator> generator;

    public VeinCoreBlock(Properties properties, TagPrefix oreTag, Supplier<VeinGenerator> generator, RecipeData recipeData, boolean registerModel) {
        super(properties);
        this.recipeData = recipeData;
        this.oreTag = oreTag;
        this.generator = generator;

        if (registerModel && Platform.isClient()) VeinCoreBlockRenderer.create(this);
    }

    private Optional<Material> getMaxMaterial() {
        if (maxMaterialSet) return maxMaterial;
        maxMaterialSet = true;
        maxMaterial = generator.get().getValidMaterialsChances().stream()
                .max(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue);
        return maxMaterial;
    }

    private List<ItemStack> copyMaterial(Material material) {
        return List.of(GTUtil.copyAmount(
                material.getProperty(PropertyKey.ORE).getOreMultiplier() * 2
                        * (TagPrefix.ORES.get(oreTag).isDoubleDrops() ? 2 : 1),
                ChemicalHelper.get(TagPrefix.crushed, material)));
    }

    private void setDrops(ServerLevel level, BlockPos pos) {
        dropsSet = true;
        maxDropsChance = generator.get().getAllEntries().stream()
                .map(Util.setMapEntryKey(value -> value.map(
                        state -> Util.getDefaultDrops(level, pos, state, VeinCoreMinerLogic.PICKAXE_TOOL.get()),
                        this::copyMaterial)))
                .map(Util.setMapEntryKey(value -> value.stream().filter(Predicate.not(ItemStack::isEmpty)).toList()))
                .filter(Predicate.not(entry -> entry.getKey().isEmpty()))
                .peek(drops::add)
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    private List<Map.Entry<List<ItemStack>, Integer>> getDrops(Level level, BlockPos pos) {
        if (!dropsSet && level instanceof ServerLevel serverLevel) setDrops(serverLevel, pos);
        return drops;
    }

    private int getMaxDropsChance(Level level, BlockPos pos) {
        if (!dropsSet && level instanceof ServerLevel serverLevel) setDrops(serverLevel, pos);
        return maxDropsChance;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VeinCoreBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity) {
            blockEntity.onPlace(state, level, pos);
        }
    }

    @Override
    public @Nullable GTRecipe getRecipe(Level level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity)) return null;

        List<ItemStack> drops = pickDrops(level, pos);

        if (drops.isEmpty()) return null;

        GTRecipeBuilder builder = GTRecipeBuilder.ofRaw()
                .duration(recipeData.duration())
                .EUt(recipeData.EUt())
                .inputFluids(GTMaterials.DrillingFluid.getFluid(10));

        drops.forEach(drop -> builder.chancedOutput(drop, blockEntity.getDropChance(), 0));

        oreTag.secondaryMaterials().forEach(materialStack -> {
            if (materialStack.material().hasProperty(PropertyKey.DUST)) {
                builder.chancedOutput(ChemicalHelper.getGem(materialStack), 6700, 800);
            }
        });

        return builder.buildRawRecipe();
    }

    private List<ItemStack> pickDrops(Level level, BlockPos pos) {
        int maxDropsChance = getMaxDropsChance(level, pos);
        if (maxDropsChance <= 0) return List.of();
        int random = level.getRandom().nextInt(maxDropsChance);

        for (Map.Entry<List<ItemStack>, Integer> drop : getDrops(level, pos)) {
            random -= drop.getValue();
            if (random < 0) return drop.getKey();
        }

        GTOreMiners.LOGGER.error("cannot find drops for {}", getDescriptionId());
        return List.of();
    }

    @Override
    public void deplete(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity) {
            int totalDrops = recipeData.totalDrops();
            if (totalDrops == -1) return;
            blockEntity.deplete(1.0 / totalDrops);
        }
    }

    @Override
    public int getVeinCoreTier(Level level, BlockPos pos, BlockState state) {
        return recipeData.veinCoreTier();
    }

    @Override
    public void addDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {
        if (!(level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity)) return;

        textList.add(Component.translatable("gt_oreminers.block.vein_core.remaining", remainingFormat.format(blockEntity.getRemaining()*100)));
        textList.add(Component.translatable("gt_oreminers.block.vein_core.purity", purityFormat.format(blockEntity.getPurity()*100)));
    }
}
