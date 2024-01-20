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

    public static final RegistrySupplier<Block> TBD = BLOCKS.register("tbd", TbdBlock::new);
    public static final RegistrySupplier<BlockEntityType<TbdBlockEntity>> TBDE = BLOCKENTITY.register("tbd", () -> BlockEntityType.Builder.of(TbdBlockEntity::new, TBD.get()).build(null));

}
