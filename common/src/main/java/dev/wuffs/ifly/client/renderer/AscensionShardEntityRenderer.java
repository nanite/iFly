package dev.wuffs.ifly.client.renderer;

import dev.wuffs.ifly.blocks.AscensionShardBlockEntity;
import dev.wuffs.ifly.client.block.AscensionShardModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AscensionShardEntityRenderer extends GeoBlockRenderer<AscensionShardBlockEntity> {

    public AscensionShardEntityRenderer(){
        super(new AscensionShardModel());
    }
}
