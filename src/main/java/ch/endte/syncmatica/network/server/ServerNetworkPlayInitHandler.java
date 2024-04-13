package ch.endte.syncmatica.network.server;

import ch.endte.syncmatica.network.payload.PayloadManager;
import ch.endte.syncmatica.util.SyncLog;

public class ServerNetworkPlayInitHandler
{
    /**
     * Should be called when Server is starting
     */
    public static void registerPlayChannels()
    {
        PayloadManager.registerPlayChannels();
        //PacketProvider.registerPayloads();
    }
    public static void registerReceivers()
    {
        ServerNetworkPlayRegister.registerReceivers();
        SyncLog.debug("ServerNetworkPlayInitHandler#ServerNetworkPlayInitHandler(): Ready to receive packets.");
    }
    public static void unregisterReceivers()
    {
        ServerNetworkPlayRegister.unregisterReceivers();
    }
}
