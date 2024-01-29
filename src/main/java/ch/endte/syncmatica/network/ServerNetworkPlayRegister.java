package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import ch.endte.syncmatica.service.DebugService;
import ch.endte.syncmatica.SyncmaticaReference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworkPlayRegister
{
    static ServerPlayNetworking.PlayPayloadHandler<SyncmaticaPayload> C2SSyncmaticaHandler;
    
    public static void registerDefaultReceivers()
    {
        // Do when the server starts, not before
        if (SyncmaticaReference.isServer())
        {
            DebugService.printDebug("ServerHandlerManager#registerDefaultReceivers(): isServer() true.");
            DebugService.printDebug("ServerHandlerManager#registerDefaultReceivers(): registerServuxHandler()");
            ServerPlayNetworking.registerGlobalReceiver(SyncmaticaPayload.TYPE, C2SSyncmaticaHandler);
        }
    }

    public static void unregisterDefaultReceivers()
    {
        // Do when server stops
        if (SyncmaticaReference.isServer())
        {
            DebugService.printDebug("ServerHandlerManager#unregisterDefaultReceivers(): isServer() true.");
            DebugService.printDebug("ServerHandlerManager#unregisterDefaultReceivers(): registerSyncmaticaHandler()");
            ServerPlayNetworking.unregisterGlobalReceiver(SyncmaticaPayload.TYPE.id());
        }
    }
    static
    {
        C2SSyncmaticaHandler = ServerNetworkPlayHandler::receiveC2SSyncmatica;
    }
}
