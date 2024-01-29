package ch.endte.syncmatica.network.c2s;

import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import ch.endte.syncmatica.SyncmaticaReference;

public class ClientNetworkPlayRegister
{
    static ClientPlayNetworking.PlayPayloadHandler<SyncmaticaC2SPayload> S2CSyncmaticaHandler;
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
            ClientPlayNetworking.registerGlobalReceiver(SyncmaticaC2SPayload.TYPE, S2CSyncmaticaHandler);
            receiversInit = true;
        }
    }

    public static void unregisterDefaultReceivers()
    {
        // Do when disconnecting from server/world
        if (SyncmaticaReference.isClient())
        {
            DebugService.printDebug("ClientHandlerManager#unregisterDefaultReceivers(): isClient() true.  Unregister handlers.");
            ClientPlayNetworking.unregisterGlobalReceiver(SyncmaticaC2SPayload.TYPE.id());
            receiversInit = false;
        }
    }
    static
    {
        S2CSyncmaticaHandler = ClientNetworkPlayHandler::receiveC2SSyncmatica;
    }
}
