package org.jimmybobjim.oreminers.common.block;

import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.lowdragmc.lowdraglib.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOMPropertyKeys;
import org.jimmybobjim.oreminers.client.render.VeinCoreBlockRenderer;
import org.jimmybobjim.oreminers.common.blockEntity.VeinCoreBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class VeinCoreBlock extends MaterialBlock implements EntityBlock, VeinCoreMinerMachineMineable {
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
    public List<ItemStack> getDrops(Level level, BlockPos pos, BlockState state) {
        return List.of(new ItemStack(Blocks.DIRT));
    }

    @Override
    public void deplete(Level level, BlockPos pos, BlockState state) {

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
}
