package dev.wuffs.ifly.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.client.AscensionShardClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AscensionShard.MOD_ID)
public class iFlyForge {

    public iFlyForge() {
        EventBuses.registerModEventBus(AscensionShard.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        new AscensionShard();
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        AscensionShardClient.setup();
    }
}
