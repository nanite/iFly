package dev.nanite.ifly;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(iFly.MOD_ID)
public class iFlyForge {
    public iFlyForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(iFly.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        iFly.init();
    }
}
