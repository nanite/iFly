package dev.wuffs.ifly.neoforge;

import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.client.AscensionShardClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.GeckoLib;

@Mod(AscensionShard.MOD_ID)
public class iFlyNeoForge {
    public iFlyNeoForge(IEventBus modEventBus) {
        GeckoLib.initialize(modEventBus);
        new AscensionShard();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
         AscensionShardClient.setup();
    }
}
