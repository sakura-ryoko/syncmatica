package ch.endte.syncmatica.network;

import ch.endte.syncmatica.event.SyncmaticaPayloadServerHandler;
import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ServerNetworkPlayHandler
{
    // Data Payloads
    public static void sendSyncmaticaServerPayload(SyncmaticaPayload payload, ServerPlayerEntity player)
    {
        // Client-bound packet sent from the Server
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            DebugService.printDebug("ServerNetworkPlayHandler#sendSyncmaticaServer(): sending payload id: {} to player: {}", payload.getId(), player.getName().getLiteralString());
        }
    }

    public static void receiveSyncmaticaServerPayload(SyncmaticaPayload payload, ServerPlayNetworking.Context context)
    {
        DebugService.printDebug("ServerNetworkPlayHandler#receiveSyncmaticaServer(): received payload id: {}, size in bytes {}", payload.getId(), payload.data().getSizeInBytes());
        ((SyncmaticaPayloadServerHandler) SyncmaticaPayloadServerHandler.getInstance()).receiveSyncmaticaServerPayload(payload.data(), context, payload.getId().id());
    }
}
