package ch.endte.syncmatica.network;

import ch.endte.syncmatica.event.SyncmaticaPayloadServerHandler;
import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * canSend()
 * Wraps: canSend(player.networkHandler, payload.getId().id());
 * --> Wraps Internally as:
 * `--> ServerNetworkingImpl.getAddon(player.networkHandler).getSendableChannels().contains(payload.getId().id());
 * send()
 * Wraps internally as:
 * --> player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(payload));
 */
public abstract class ServerNetworkPlayHandler
{
    public static void sendSyncmaticaServerPayload(SyncmaticaPayload payload, ServerPlayerEntity player)
    {
        // Client-bound packet sent by the Server
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            SyncLog.debug("ServerNetworkPlayHandler#sendSyncmaticaServer(): sending payload id: {} to player: {}", payload.getId(), player.getName().getLiteralString());
        }
    }

    public static void receiveSyncmaticaServerPayload(SyncmaticaPayload payload, ServerPlayNetworking.Context context)
    {
        // Server-bound packet sent by a Client
        SyncLog.debug("ServerNetworkPlayHandler#receiveSyncmaticaServer(): received payload id: {}, size in bytes {}", payload.getId().id().toString(), payload.data().getSizeInBytes());
        ((SyncmaticaPayloadServerHandler) SyncmaticaPayloadServerHandler.getInstance()).receiveSyncmaticaServerPayload(payload.data(), context);
    }
}
