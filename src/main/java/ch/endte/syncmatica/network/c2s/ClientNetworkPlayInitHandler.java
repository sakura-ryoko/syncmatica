package ch.endte.syncmatica.network.c2s;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.PayloadTypeRegister;
import ch.endte.syncmatica.network.test.ClientDebugSuite;

public class ClientNetworkPlayInitHandler
{
    /**
     * Should be called when Client opens the main screen
     */
    public static void registerPlayChannels()
    {
        PayloadTypeRegister.registerDefaultTypes(Syncmatica.MOD_ID);
        PayloadTypeRegister.registerDefaultPlayChannels();
        ClientDebugSuite.checkGlobalChannels();
    }
    /**
     * Should be called when Client joins a server
     */
    public static void registerReceivers()
    {
        ClientNetworkPlayRegister.registerDefaultReceivers();
        ClientDebugSuite.checkGlobalChannels();
    }
    public static void unregisterReceivers()
    {
        ClientNetworkPlayRegister.unregisterDefaultReceivers();
        ClientDebugSuite.checkGlobalChannels();
    }
}
