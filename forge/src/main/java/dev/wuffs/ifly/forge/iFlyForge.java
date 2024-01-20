package dev.wuffs.ifly.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.wuffs.ifly.IFly;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IFly.MOD_ID)
public class iFlyForge {

    public iFlyForge() {
        EventBuses.registerModEventBus(IFly.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        IFly.init();
    }
}
