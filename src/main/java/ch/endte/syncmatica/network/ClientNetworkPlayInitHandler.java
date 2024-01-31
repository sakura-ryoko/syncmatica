package ch.endte.syncmatica.network;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.packet.PacketProvider;
import ch.endte.syncmatica.network.test.ClientDebugSuite;
import ch.endte.syncmatica.util.SyncLog;

public class ClientNetworkPlayInitHandler
{
    /**
     * Should be called when Client opens the main screen
     */
    public static void registerPlayChannels()
    {
        SyncLog.debug("ClientNetworkPlayInitHandler#registerPlayChannels(): called.");
        PayloadTypeRegister.registerTypes(SyncmaticaReference.MOD_ID);
        PayloadTypeRegister.registerPlayChannels();
        PacketProvider.registerPayloads();
        ClientDebugSuite.checkGlobalChannels();
    }
    /**
     * Should be called when Client joins a server
     */
    public static void registerReceivers()
    {
        SyncLog.debug("ClientNetworkPlayInitHandler#registerReceivers(): called.");
        ClientNetworkPlayRegister.registerReceivers();
        ClientDebugSuite.checkGlobalChannels();
    }
    public static void unregisterReceivers()
    {
        SyncLog.debug("ClientNetworkPlayInitHandler#unregisterReceivers(): called.");
        ClientNetworkPlayRegister.unregisterReceivers();
        ClientDebugSuite.checkGlobalChannels();
    }
}
