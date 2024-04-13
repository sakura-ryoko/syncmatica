package ch.endte.syncmatica.network.client;

import ch.endte.syncmatica.network.payload.PayloadManager;

public class ClientNetworkPlayInitHandler
{
    /**
     * Should be called when Client opens the main screen
     */
    public static void registerPlayChannels()
    {
        //SyncLog.debug("ClientNetworkPlayInitHandler#registerPlayChannels(): called.");
        PayloadManager.registerPlayChannels();
    }
    /**
     * Should be called when Client joins a server
     */
    public static void registerReceivers()
    {
        //SyncLog.debug("ClientNetworkPlayInitHandler#registerReceivers(): called.");
        ClientNetworkPlayRegister.registerReceivers();
    }

    /**
     * Should be called when Client Leaves / Disconnects
     */
    public static void unregisterReceivers()
    {
        //SyncLog.debug("ClientNetworkPlayInitHandler#unregisterReceivers(): called.");
        ClientNetworkPlayRegister.unregisterReceivers();
    }
}
