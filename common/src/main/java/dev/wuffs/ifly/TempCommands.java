package dev.wuffs.ifly;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Predicate;

public class TempCommands {

    private Predicate<CommandSourceStack> requiresOPorSP() {
        return source -> source.getServer().isSingleplayer() || source.hasPermission(2);
    }
    public void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("as")
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
    }
}
