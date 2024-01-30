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
        PayloadTypeRegister.registerTypes(Syncmatica.MOD_ID);
        PayloadTypeRegister.registerPlayChannels();
        ServerDebugSuite.checkGlobalChannels();
    }
    /**
     * Should be called when Client joins a server
     */
    public static void registerReceivers()
    {
        ServerNetworkPlayRegister.registerReceivers();
        ServerDebugSuite.checkGlobalChannels();
    }
    public static void unregisterReceivers()
    {
        ServerNetworkPlayRegister.unregisterReceivers();
        ServerDebugSuite.checkGlobalChannels();
    }
}
