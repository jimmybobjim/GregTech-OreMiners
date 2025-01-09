package org.jimmybobjim.oreminers.common.blockEntity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.util.Util;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter @Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VeinCoreBlockEntity extends BlockEntity {
    private double purity = -1;
    private double remaining = 1;

    public VeinCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public VeinCoreBlockEntity(BlockPos pos, BlockState state) {
        super(GTOMBlockEntities.VEIN_CORE.get(), pos, state);
    }

    public static double getPurity(Level level, BlockPos pos, BlockState state) {
        return Math.abs(Util.getRandom(level, state, pos).nextGaussian());
    }

    public void onPlace(BlockState state, Level level, BlockPos pos) {
        GTOreMiners.LOGGER.info("onPlace triggered");
        purity = getPurity(level, pos, state);
    }

    private void saveData(CompoundTag tag) {
        tag.putDouble("purity", purity);
        tag.putDouble("remaining", remaining);
    }

    private void loadData(CompoundTag tag) {
        GTOreMiners.LOGGER.info("loading the following tag: {}", tag);

        purity = tag.getDouble("purity");
        remaining = tag.getDouble("remaining");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadData(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadData(tag);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) handleUpdateTag(tag);
    }
}
