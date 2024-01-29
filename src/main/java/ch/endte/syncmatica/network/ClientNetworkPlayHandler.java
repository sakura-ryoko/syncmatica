package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.interfaces.SyncmaticaPayloadClientHandler;
import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkPlayHandler
{

    public static void sendSyncmaticaClient(SyncmaticaPayload payload)
    {
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
            DebugService.printDebug("ClientNetworkPlayHandler#sendSyncmaticaClient(): sending payload id: {}", payload.getId());
        }
    }
    public static void receiveSyncmaticaClient(SyncmaticaPayload payload, ClientPlayNetworking.Context ctx)
    {
        DebugService.printDebug("ClientNetworkPlayHandler#receiveSyncmaticaClient(): id: {} received Syncmatica Payload (size in bytes): {}", payload.getId(), payload.data().getSizeInBytes());
        ((SyncmaticaPayloadClientHandler) SyncmaticaPayloadClientHandler.getInstance()).receiveSyncmaticaClientPayload(payload.data(), ctx);
    }
}
