package dev.nanite.ifly.fabric;

import dev.nanite.ifly.iFly;
import net.fabricmc.api.ModInitializer;

public class iFlyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        iFly.init();
    }
}
