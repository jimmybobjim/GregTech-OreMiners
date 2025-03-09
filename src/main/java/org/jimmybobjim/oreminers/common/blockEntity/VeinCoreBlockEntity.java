package org.jimmybobjim.oreminers.common.blockEntity;

import lombok.Getter;
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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VeinCoreBlockEntity extends BlockEntity {
    private double purity = -1;
    @Getter
    private double remaining = 1;

    public VeinCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public VeinCoreBlockEntity(BlockPos pos, BlockState state) {
        super(GTOMBlockEntities.VEIN_CORE.get(), pos, state);
    }

    public static double generatePurity(Level level, BlockPos pos, BlockState state) {
        return Util.generateVeinCorePurity(Util.getRandom(level, state, pos));
    }

    public void setPurity(double purity) {
        this.purity = purity;
        setChanged();
    }

    public double getPurity() {
        if (purity != -1) return purity;

        if (level == null) {
            GTOreMiners.LOGGER.warn("Cannot retrieve vein core purity at ({}). Level is null", worldPosition.toShortString());
            return -1;
        }

        return generatePurity(level, worldPosition, getBlockState());
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
        setChanged();
    }

    public void deplete() {
        remaining -= 0.00001;
        setChanged();
    }

    public void onPlace(BlockState state, Level level, BlockPos pos) {
        purity = generatePurity(level, pos, state);
    }

    private void saveData(CompoundTag tag) {
        tag.putDouble("purity", purity);
        tag.putDouble("remaining", remaining);
    }

    private void loadData(CompoundTag tag) {
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
