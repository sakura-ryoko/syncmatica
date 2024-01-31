package ch.endte.syncmatica.network;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.test.ServerDebugSuite;
import ch.endte.syncmatica.util.SyncLog;

public class ServerNetworkPlayInitHandler
{
    /**
     * Should be called when Server is starting
     */
    public static void registerPlayChannels()
    {
        PayloadTypeRegister.registerTypes(SyncmaticaReference.MOD_ID);
        PayloadTypeRegister.registerPlayChannels();
        ServerDebugSuite.checkGlobalChannels();
    }
    public static void registerReceivers()
    {
        ServerNetworkPlayRegister.registerReceivers();
        ServerDebugSuite.checkGlobalChannels();
        SyncLog.debug("ServerNetworkPlayInitHandler#ServerNetworkPlayInitHandler(): Ready to receive packets.");
    }
    public static void unregisterReceivers()
    {
        ServerNetworkPlayRegister.unregisterReceivers();
        ServerDebugSuite.checkGlobalChannels();
    }
}
