package dev.wuffs.ifly;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.flight.FlightManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class TempCommands {

    private Predicate<CommandSourceStack> requiresOPorSP() {
        return source -> source.getServer().isSingleplayer() || source.hasPermission(2);
    }
    public void register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> as = Commands.literal("as");

        as.then(Commands.literal("enable")
                .then(Commands.literal("on")
                        .requires(requiresOPorSP())
                        .executes(ctx -> {
                            AscensionShardBlockEntity.ENABLED = true;
                            return Command.SINGLE_SUCCESS;
                        })
                ).then(Commands.literal("off")
                        .requires(requiresOPorSP())
                        .executes(ctx -> {
                            AscensionShardBlockEntity.ENABLED = false;
                            return Command.SINGLE_SUCCESS;
                        })
                ));

        as.then(Commands.literal("debug")
                .requires(requiresOPorSP())
                .then(Commands.argument("block", BlockPosArgument.blockPos()).executes(context -> {
                    var blockPos = BlockPosArgument.getLoadedBlockPos(context, "block");
                    var world = context.getSource().getLevel();
                    var blockEntity = world.getBlockEntity(blockPos);

                    if (blockEntity instanceof AscensionShardBlockEntity) {
                        // Use the block pos to lookup the volume data in the flight manager
                        FlightManager.FlightBounds volume = FlightManager.get(context.getSource().getLevel()).getVolume(blockPos);
                        if (volume != null) {
                            context.getSource().sendSuccess(() -> Component.literal("Flight volume data"), false);
                            CompoundTag volumeData = volume.writeToCompound();
                            context.getSource().sendSuccess(() -> Component.literal(NbtUtils.prettyPrint(volumeData)), false);
                        } else {
                            context.getSource().sendFailure(Component.literal("No flight volume found at this location"));
                        }
                    } else {
                        context.getSource().sendFailure(Component.literal("No Ascension Shard block found at this location"));
                    }

                    return 1;
                })));

        dispatcher.register(as);
    }
}
