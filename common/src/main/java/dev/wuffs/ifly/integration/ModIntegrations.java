package dev.wuffs.ifly.integration;

import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModIntegrations {
    private static final List<TeamsInterface> PROVIDERS = new ArrayList<>();

    public static void init() {
        if (Platform.isModLoaded("ftbteams")) {
            PROVIDERS.add(new FTBTeamsIntegration());
        }

        if (Platform.isModLoaded("cadmus")) {
            PROVIDERS.add(new CadmusIntegration());
        }

        PROVIDERS.add(new VanillaTeamsIntegration());
    }

    @Nullable
    public static TeamsInterface factory(ResourceLocation id) {
        return PROVIDERS.stream().filter(p -> p.id().equals(id)).findFirst().orElse(null);
    }
}
