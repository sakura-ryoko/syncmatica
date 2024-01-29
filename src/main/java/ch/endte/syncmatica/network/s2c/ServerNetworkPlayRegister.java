package ch.endte.syncmatica.network.s2c;

import ch.endte.syncmatica.network.c2s.SyncmaticaC2SPayload;
import ch.endte.syncmatica.service.DebugService;
import ch.endte.syncmatica.SyncmaticaReference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworkPlayRegister
{
    static ServerPlayNetworking.PlayPayloadHandler<SyncmaticaC2SPayload> C2SSyncmaticaHandler;
    
    public static void registerDefaultReceivers()
    {
        // Do when the server starts, not before
        if (SyncmaticaReference.isServer())
        {
            DebugService.printDebug("ServerHandlerManager#registerDefaultReceivers(): isServer() true.");
            DebugService.printDebug("ServerHandlerManager#registerDefaultReceivers(): registerServuxHandler()");
            ServerPlayNetworking.registerGlobalReceiver(SyncmaticaC2SPayload.TYPE, C2SSyncmaticaHandler);
        }
    }

    public static void unregisterDefaultReceivers()
    {
        // Do when server stops
        if (SyncmaticaReference.isServer())
        {
            DebugService.printDebug("ServerHandlerManager#unregisterDefaultReceivers(): isServer() true.");
            DebugService.printDebug("ServerHandlerManager#unregisterDefaultReceivers(): registerSyncmaticaHandler()");
            ServerPlayNetworking.unregisterGlobalReceiver(SyncmaticaC2SPayload.TYPE.id());
        }
    }
    static
    {
        C2SSyncmaticaHandler = ServerNetworkPlayHandler::receiveC2SSyncmatica;
    }
}
