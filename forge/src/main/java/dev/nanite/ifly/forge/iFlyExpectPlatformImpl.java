package dev.nanite.ifly.forge;

import dev.nanite.ifly.iFlyExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class iFlyExpectPlatformImpl {
    /**
     * This is our actual method to {@link iFlyExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
