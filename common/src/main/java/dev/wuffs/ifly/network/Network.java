package dev.wuffs.ifly.network;

import dev.ftb.mods.ftblibrary.util.NetworkHelper;
import dev.wuffs.ifly.network.debug.C2SDebugScreen;
import dev.wuffs.ifly.network.debug.S2CDebugScreen;

public class Network {

    public static void register() {
        NetworkHelper.registerC2S(C2SDebugScreen.TYPE, C2SDebugScreen.STREAM_CODEC, C2SDebugScreen::handle);
        NetworkHelper.registerC2S(C2SGUIInteract.TYPE, C2SGUIInteract.STREAM_CODEC, C2SGUIInteract::handle);
        NetworkHelper.registerC2S(C2SOpenIflyScreen.TYPE, C2SOpenIflyScreen.STREAM_CODEC, C2SOpenIflyScreen::handle);


        NetworkHelper.registerS2C(S2CDebugScreen.TYPE, S2CDebugScreen.STREAM_CODEC, S2CDebugScreen::handle);
        NetworkHelper.registerS2C(S2COpenIflyScreen.TYPE, S2COpenIflyScreen.STREAM_CODEC, S2COpenIflyScreen::handle);


    }
}
