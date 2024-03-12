package dev.wuffs.ifly.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.wuffs.ifly.IFly;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(IFly.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITY = DeferredRegister.create(IFly.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<Block> ASHARD = BLOCKS.register("ascension_shard", AscensionShardBlock::new);
    public static final RegistrySupplier<BlockEntityType<AscensionShardBlockEntity>> ASHARD_BENTITY = BLOCKENTITY.register("ascension_shard", () -> BlockEntityType.Builder.of(AscensionShardBlockEntity::new, ASHARD.get()).build(null));

}
