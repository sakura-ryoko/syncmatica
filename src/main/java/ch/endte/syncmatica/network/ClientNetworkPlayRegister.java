package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import ch.endte.syncmatica.SyncmaticaReference;

public class ClientNetworkPlayRegister
{
    static ClientPlayNetworking.PlayPayloadHandler<SyncmaticaPayload> S2CSyncmaticaHandler;
    private static boolean receiversInit = false;


    public static void registerDefaultReceivers()
    {
        // Don't register more than once
        if (receiversInit)
            return;
        // Wait until world/server joined
        if (SyncmaticaReference.isClient())
        {
            if (SyncmaticaReference.isSinglePlayer())
                DebugService.printDebug("ClientHandlerManager#registerDefaultReceivers(): Game is running in Single Player Mode.");
            DebugService.printDebug("ClientHandlerManager#registerDefaultReceivers(): isClient() true.  Register handlers.");
            ClientPlayNetworking.registerGlobalReceiver(SyncmaticaPayload.TYPE, S2CSyncmaticaHandler);
            receiversInit = true;
        }
    }

    public static void unregisterDefaultReceivers()
    {
        // Do when disconnecting from server/world
        if (SyncmaticaReference.isClient())
        {
            DebugService.printDebug("ClientHandlerManager#unregisterDefaultReceivers(): isClient() true.  Unregister handlers.");
            ClientPlayNetworking.unregisterGlobalReceiver(SyncmaticaPayload.TYPE.id());
            receiversInit = false;
        }
    }
    static
    {
        S2CSyncmaticaHandler = ClientNetworkPlayHandler::receiveC2SSyncmatica;
    }
}
