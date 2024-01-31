package ch.endte.syncmatica.network;

import ch.endte.syncmatica.event.SyncmaticaPayloadHandler;
import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * canSend()
 * Wraps: canSend(payload.getId().id());
 * -> Wraps Internally as:
 * `--> ClientNetworkingImpl.getAddon(MinecraftClient.getInstance().getNetworkHandler()).getSendableChannels().contains(payload.getId().id());
 * send()
 * Wraps internally as:
 * --> MinecraftClient.getInstance().getNetworkHandler().sendPacket();
 */
public class ClientNetworkPlayHandler
{
    public static void sendSyncmatica(SyncmaticaPayload payload)
    {
        // Server-bound packet sent from the Client
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
            SyncLog.debug("ClientNetworkPlayHandler#sendSyncmatica(): sending payload id: {}", payload.getId().id().toString());
        }
    }
    public static void receiveSyncmatica(SyncmaticaPayload payload, ClientPlayNetworking.Context ctx)
    {
        // Client-bound packet sent from the Server
        SyncLog.debug("ClientNetworkPlayHandler#receiveSyncmatica(): id: {} received ServUX Payload (size in bytes): {}", payload.getId().id().toString(), payload.data().getSizeInBytes());
        ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).receiveSyncmaticaPayload(payload.data(), ctx);
    }
}
