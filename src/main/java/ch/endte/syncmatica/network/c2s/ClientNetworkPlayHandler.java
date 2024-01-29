package ch.endte.syncmatica.network.c2s;

import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkPlayHandler
{

    public static void sendC2SSyncmatica(SyncmaticaC2SPayload payload)
    {
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
            DebugService.printDebug("ClientNetworkPlayHandler#sendC2SSyncmatica(): sending payload id: {}", payload.getId());
        }
    }
    public static void receiveC2SSyncmatica(SyncmaticaC2SPayload payload, ClientPlayNetworking.Context ctx)
    {
        DebugService.printDebug("ClientNetworkPlayHandler#receiveC2SSyncmatica(): id: {} received Syncmatica Payload (size in bytes): {}", payload.getId(), payload.data().getSizeInBytes());
    }
}
