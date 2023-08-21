package dev.nanite.ifly;

import net.fabricmc.api.ModInitializer;

public class iFlyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        iFly.init();
    }
}
