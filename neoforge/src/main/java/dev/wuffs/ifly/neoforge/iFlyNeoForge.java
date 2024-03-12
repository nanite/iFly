package dev.wuffs.ifly.neoforge;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
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
//        EnvExecutor.runInEnv(Env.CLIENT, () -> IFlyClient::setup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
         IFlyClient.setup();
    }
}
