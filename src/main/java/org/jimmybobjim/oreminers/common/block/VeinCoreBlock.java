package org.jimmybobjim.oreminers.common.block;

import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOMPropertyKeys;
import org.jimmybobjim.oreminers.api.propertyKeys.VeinCoreBlockProperty;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;
import org.jimmybobjim.oreminers.client.render.VeinCoreBlockRenderer;
import org.jimmybobjim.oreminers.common.blockEntity.VeinCoreBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.List;

@ParametersAreNonnullByDefault
public class VeinCoreBlock extends MaterialBlock implements EntityBlock, VeinCoreMinerMachineMineable {
    public static final DecimalFormat purityFormat = new DecimalFormat("00.00");
    public static final DecimalFormat remainingFormat = new DecimalFormat("00.000");

    public VeinCoreBlock(Properties properties, TagPrefix tagPrefix, Material material, boolean registerModel) {
        super(properties, tagPrefix, material, false);

        if (registerModel && Platform.isClient()) {
            VeinCoreBlockRenderer.create(this);
        }
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
        if (!material.hasProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK)) return null;

        VeinCoreBlockProperty property = material.getProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK);

        ItemStack drops = GTUtil.copyAmount(
                material.getProperty(PropertyKey.ORE).getOreMultiplier() * 2 * (GTOMTagPrefixes.VEIN_CORES.get(tagPrefix).oreType().isDoubleDrops() ? 2 : 1),
                ChemicalHelper.get(TagPrefix.crushed, material));

        if (drops.isEmpty()) return null;

        GTRecipeBuilder builder = GTRecipeBuilder.ofRaw()
                .duration(property.duration())
                .EUt(property.EUt())
                .inputFluids(GTMaterials.DrillingFluid.getFluid(10))
                .chancedOutput(drops, blockEntity.getDropChance(), 0);

        GTOMTagPrefixes.VEIN_CORES.get(tagPrefix).oreTag().secondaryMaterials().forEach(materialStack -> {
            if (materialStack.material().hasProperty(PropertyKey.DUST)) {
                builder.chancedOutput(ChemicalHelper.getGem(materialStack), 6700, 800);
            }
        });

        return builder.buildRawRecipe();
    }

    @Override
    public void deplete(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity && material.hasProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK)) {
            int totalDrops = material.getProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK).totalDrops();
            if (totalDrops == -1) return;
            blockEntity.deplete(1.0 / totalDrops);
        }
    }

    @Override
    public int getVeinCoreTier(Level level, BlockPos pos, BlockState state) {
        if (!material.hasProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK)) {
            GTOreMiners.LOGGER.warn("vein core block {} is missing vein_core_block property key", this);
            return -1;
        }

        return material.getProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK).veinCoreTier();
    }

    @Override
    public void addDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {
        if (!(level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity)) return;

        textList.add(Component.translatable("gt_oreminers.block.vein_core.remaining", remainingFormat.format(blockEntity.getRemaining()*100)));
        textList.add(Component.translatable("gt_oreminers.block.vein_core.purity", purityFormat.format(blockEntity.getPurity()*100)));
    }
}
