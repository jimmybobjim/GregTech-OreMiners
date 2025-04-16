package org.jimmybobjim.oreminers.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Util {
    public static RandomSource getRandom(Level level, BlockState state, BlockPos pos) {
        return RandomSource.create(pos.asLong()^state.hashCode()^level.dimension().location().hashCode());
    }

    public static <K1, K2, V> Map.Entry<K2, V> setMapEntryKey(Map.Entry<K1, V> entry, Function<K1, K2> mapper) {
        return Map.entry(mapper.apply(entry.getKey()), entry.getValue());
    }

    public static <K1, K2, V> Function<Map.Entry<K1, V>, Map.Entry<K2, V>> setMapEntryKey(Function<K1, K2> mapper) {
        return entry -> setMapEntryKey(entry, mapper);
    }

    public static double generateVeinCorePurity(RandomSource random) {
        return Math.max(0, Math.min(1, 0.5*random.nextFloat() + 0.3*random.nextFloat() + 0.1*random.nextFloat()));
    }

    public static List<ItemStack> getDefaultDrops(ServerLevel level, BlockPos pos, BlockState state, ItemStack pickaxeTool) {
        return state.getDrops(new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, pickaxeTool));
    }

    public static Component join(Component separator, List<? extends Component> elements) {
        Iterator<? extends Component> iterator = elements.iterator();
        if (!iterator.hasNext()) return Component.empty();
        MutableComponent mutableComponent = iterator.next().copy();

        while (iterator.hasNext()) {
            mutableComponent.append(separator).append(iterator.next());
        }

        return mutableComponent;
    }

    public static Component formatPercent(double percent) {
        ChatFormatting color;

        // some arbitrary values, should probably revisit later
        if (percent > 1.0) color = ChatFormatting.WHITE;
        else if (percent >= 0.8) color = ChatFormatting.GREEN;
        else if (percent >= 0.6) color = ChatFormatting.GOLD;
        else if (percent >= 0.4) color = ChatFormatting.YELLOW;
        else if (percent >= 0.2) color = ChatFormatting.RED;
        else if (percent >= 0.0) color = ChatFormatting.DARK_RED;
        else color = ChatFormatting.BLACK;

        return Component.literal(new DecimalFormat("00.000%").format(percent)).withStyle(color);
    }
}
