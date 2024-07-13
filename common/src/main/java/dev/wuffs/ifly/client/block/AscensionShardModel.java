package dev.wuffs.ifly.client.block;

import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class AscensionShardModel extends DefaultedBlockGeoModel<AscensionShardBlockEntity> {
    public AscensionShardModel() {
        super(ResourceLocation.fromNamespaceAndPath(AscensionShard.MOD_ID,"ascension_shard"));
    }
}
