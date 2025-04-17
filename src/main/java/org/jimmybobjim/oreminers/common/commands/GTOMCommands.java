package org.jimmybobjim.oreminers.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.jimmybobjim.oreminers.common.blockEntity.VeinCoreBlockEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GTOMCommands {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("gt_oreminers")
                .then(literal("vein_core")
                        .then(literal("set_purity")
                                .requires(source -> source.hasPermission(2))
                                .then(argument("position", BlockPosArgument.blockPos())
                                        .then(argument("purity", DoubleArgumentType.doubleArg(0))
                                                .executes(setVeinCore(VeinCoreBlockEntity::getPurity, VeinCoreBlockEntity::setPurity, "purity"))
                                        )
                                )
                        )
                        .then(literal("get_purity")
                                .then(argument("position", BlockPosArgument.blockPos())
                                        .executes(getVeinCore(VeinCoreBlockEntity::getPurity, "purity"))
                                )
                        )
                        .then(literal("set_remaining")
                                .requires(source -> source.hasPermission(2))
                                .then(argument("position", BlockPosArgument.blockPos())
                                        .then(argument("remaining", DoubleArgumentType.doubleArg(0))
                                                .executes(setVeinCore(VeinCoreBlockEntity::getRemaining, VeinCoreBlockEntity::setRemaining, "remaining"))
                                        )
                                )
                        )
                        .then(literal("get_remaining")
                                .then(argument("position", BlockPosArgument.blockPos())
                                        .executes(getVeinCore(VeinCoreBlockEntity::getRemaining, "remaining"))
                                )
                        )
                )
        );
    }

    private static Command<CommandSourceStack> setVeinCore(Function<VeinCoreBlockEntity, Double> getter, BiConsumer<VeinCoreBlockEntity, Double> setter, String name) {
        return context -> {
            ServerLevel level = context.getSource().getLevel();

            BlockPos pos = BlockPosArgument.getBlockPos(context, "position");

            if (!(level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity)) {
                context.getSource().sendFailure(Component.translatable("command.gt_oreminers.vein_core.not_vein_core", pos.toShortString()));
                return -1;
            }

            double oldVal = getter.apply(blockEntity);
            double newVal = context.getArgument(name, Double.class);
            setter.accept(blockEntity, newVal);

            context.getSource().sendSuccess(() -> Component.translatable("command.gt_oreminers.vein_core.set_%s.success".formatted(name), pos.toShortString(), oldVal, newVal), true);

            return 1;
        };
    }

    private static Command<CommandSourceStack> getVeinCore(Function<VeinCoreBlockEntity, Double> getter, String name) {
        return context -> {
            ServerLevel level = context.getSource().getLevel();

            BlockPos pos = BlockPosArgument.getBlockPos(context, "position");

            if (!(level.getBlockEntity(pos) instanceof VeinCoreBlockEntity blockEntity)) {
                context.getSource().sendFailure(Component.translatable("command.gt_oreminers.vein_core.not_vein_core", pos.toShortString()));
                return -1;
            }

            double val = getter.apply(blockEntity);
            context.getSource().sendSuccess(() -> Component.translatable("command.gt_oreminers.vein_core.get_%s.success".formatted(name), pos.toShortString(), val), false);

            return 1;
        };
    }
}
