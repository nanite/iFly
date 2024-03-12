package dev.wuffs.ifly.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.wuffs.ifly.blocks.Blocks;
import dev.wuffs.ifly.blocks.TBDBlockEntityRenderer;

public class IFlyClient {
    public static void setup() {
        BlockEntityRendererRegistry.register(Blocks.TBDE.get(), TBDBlockEntityRenderer::new);
    }
}
