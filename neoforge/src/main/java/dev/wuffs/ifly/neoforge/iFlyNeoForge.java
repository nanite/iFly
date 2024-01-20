package dev.wuffs.ifly.neoforge;

import dev.wuffs.ifly.IFly;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(IFly.MOD_ID)
public class iFlyNeoForge {
    public iFlyNeoForge(IEventBus modEventBus) {
        IFly.init();
    }
}
