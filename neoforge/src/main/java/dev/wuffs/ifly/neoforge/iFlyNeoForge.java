package dev.wuffs.ifly.neoforge;

import dev.wuffs.ifly.IFly;
import dev.wuffs.ifly.client.IFlyClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(IFly.MOD_ID)
public class iFlyNeoForge {
    public iFlyNeoForge(IEventBus modEventBus) {
        IFly.init();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
         IFlyClient.setup();
    }
}
