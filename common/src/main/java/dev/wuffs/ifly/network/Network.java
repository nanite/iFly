package dev.wuffs.ifly.network;

import dev.architectury.networking.NetworkChannel;
import dev.wuffs.ifly.IFly;
import net.minecraft.resources.ResourceLocation;

public class Network {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(IFly.MOD_ID, "networking_channel"));
    public static void register() {
        CHANNEL.register(C2SOpenIflyScreen.class, C2SOpenIflyScreen::encode, C2SOpenIflyScreen::new, C2SOpenIflyScreen::apply);
        CHANNEL.register(S2COpenIflyScreen.class, S2COpenIflyScreen::encode, S2COpenIflyScreen::new, S2COpenIflyScreen::apply);

        CHANNEL.register(C2SGUIInteract.class, C2SGUIInteract::encode, C2SGUIInteract::new, C2SGUIInteract::apply);
    }
}
