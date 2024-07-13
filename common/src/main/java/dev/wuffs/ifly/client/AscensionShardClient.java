package dev.wuffs.ifly.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.wuffs.ifly.blocks.Blocks;
import dev.wuffs.ifly.client.renderer.AscensionShardEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AscensionShardClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AscensionShardClient.class);
    public static void setup() {
        LOGGER.info("Setting up Ascension Shard Client");
        BlockEntityRendererRegistry.register(Blocks.ASHARD_BENTITY.get(), context -> new AscensionShardEntityRenderer());
    }
}
