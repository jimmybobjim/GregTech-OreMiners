package org.jimmybobjim.oreminers.common.block;

import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
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
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;
import org.jimmybobjim.oreminers.client.render.VeinCoreBlockRenderer;
import org.jimmybobjim.oreminers.common.blockEntity.VeinCoreBlockEntity;
import org.jimmybobjim.oreminers.util.ChancedItemDrop;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    /**
     * copied from {@link com.gregtechceu.gtceu.data.recipe.generated.OreRecipeHandler#processOre(TagPrefix, Material, OreProperty, Consumer)}
     */
    @Override
    public List<ItemStack> getDrops(Level level, BlockPos pos, BlockState state) {
        OreProperty property = material.getProperty(PropertyKey.ORE);

        return List.of(GTUtil.copyAmount(
                property.getOreMultiplier() * 2 * (GTOMTagPrefixes.VEIN_CORES.get(tagPrefix).oreType().isDoubleDrops() ? 2 : 1),
                ChemicalHelper.get(TagPrefix.crushed, material)));
    }

    /**
     * copied from {@link com.gregtechceu.gtceu.data.recipe.generated.OreRecipeHandler#processOre(TagPrefix, Material, OreProperty, Consumer)}
     */
    @Override
    public List<ChancedItemDrop> getChancedDrops(Level level, BlockPos pos, BlockState state) {
        List<ChancedItemDrop> drops = new ArrayList<>();

        GTOMTagPrefixes.VEIN_CORES.get(tagPrefix).oreTag().secondaryMaterials().forEach(materialStack -> {
            if (materialStack.material().hasProperty(PropertyKey.DUST)) {
                drops.add(new ChancedItemDrop(ChemicalHelper.getGem(materialStack), 6700, 800));
            }
        });

        return drops;
    }

    @Override
    public void deplete(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity) {
            blockEntity.deplete();
        }
    }

    public double getRemaining(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity) {
            return blockEntity.getRemaining();
        }

        GTOreMiners.LOGGER.warn("Cannot get remaining from vein core");
        return -1;
    }

    @Override
    public double getDropChance(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity) {
            return blockEntity.getPurity();
        }

        GTOreMiners.LOGGER.warn("Cannot get purity of vein core");
        return -1;
    }

    @Override
    public int getTier(Level level, BlockPos pos, BlockState state) {
        if (material.hasProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK)) {
            return material.getProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK).getLevel();
        } else {
            GTOreMiners.LOGGER.warn("vein core block {} is missing vein_core_block property key", this);
            return -1;
        }
    }

    @Override
    public void addDisplayText(Level level, BlockPos pos, BlockState state, List<Component> textList) {
        textList.add(Component.translatable("gt_oreminers.block.vein_core.remaining", remainingFormat.format(getRemaining(level, pos, state)*100)));
        textList.add(Component.translatable("gt_oreminers.block.vein_core.purity", purityFormat.format(getDropChance(level, pos, state)*100)));
    }
}
