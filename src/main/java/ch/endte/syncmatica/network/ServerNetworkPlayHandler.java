package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.interfaces.SyncmaticaPayloadServerHandler;
import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ServerNetworkPlayHandler
{
    // Data Payloads
    public static void sendSyncmaticaServer(SyncmaticaPayload payload, ServerPlayerEntity player)
    {
        // Client-bound packet sent from the Server
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            DebugService.printDebug("ServerNetworkPlayHandler#sendSyncmaticaServer(): sending payload id: {}", payload.getId());
        }
    }

    public static void receiveSyncmaticaServer(SyncmaticaPayload payload, ServerPlayNetworking.Context context)
    {
        DebugService.printDebug("ServerNetworkPlayHandler#receiveSyncmaticaServer(): received payload id: {}, size in bytes {}", payload.getId(), payload.data().getSizeInBytes());
        ((SyncmaticaPayloadServerHandler) SyncmaticaPayloadServerHandler.getInstance()).receiveSyncmaticaServerPayload(payload.data(), context);
    }
}
