package dev.wuffs.ifly.neoforge;

import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.client.AscensionShardClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(AscensionShard.MOD_ID)
public class iFlyNeoForge {
    public iFlyNeoForge(IEventBus modEventBus) {
        new AscensionShard();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
         AscensionShardClient.setup();
    }
}
