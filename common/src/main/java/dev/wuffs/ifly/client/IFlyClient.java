package dev.wuffs.ifly.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.wuffs.ifly.blocks.Blocks;
import dev.wuffs.ifly.blocks.TBDBlockEntityRenderer;
import net.minecraft.client.Minecraft;

public class IFlyClient {
    public static void setup(Minecraft minecraft) {
        BlockEntityRendererRegistry.register(Blocks.TBDE.get(), TBDBlockEntityRenderer::new);
    }
}
