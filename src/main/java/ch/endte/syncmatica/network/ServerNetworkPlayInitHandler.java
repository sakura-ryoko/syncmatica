package ch.endte.syncmatica.network;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.test.ServerDebugSuite;

public class ServerNetworkPlayInitHandler
{
    /**
     * Should be called when Server is starting
     */
    public static void registerPlayChannels()
    {
        PayloadTypeRegister.registerDefaultTypes(Syncmatica.MOD_ID);
        PayloadTypeRegister.registerDefaultPlayChannels();
        ServerDebugSuite.checkGlobalChannels();
    }
    /**
     * Should be called when Client joins a server
     */
    public static void registerReceivers()
    {
        ServerNetworkPlayRegister.registerDefaultReceivers();
        ServerDebugSuite.checkGlobalChannels();
    }
    public static void unregisterReceivers()
    {
        ServerNetworkPlayRegister.unregisterDefaultReceivers();
        ServerDebugSuite.checkGlobalChannels();
    }
}
