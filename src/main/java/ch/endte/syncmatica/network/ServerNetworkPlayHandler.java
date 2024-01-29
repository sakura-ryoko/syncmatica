package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.s2c.SyncmaticaS2CPayload;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ServerNetworkPlayHandler
{
    // Data Payloads
    public static void sendS2CSyncmatica(SyncmaticaS2CPayload payload, ServerPlayerEntity player)
    {
        // Client-bound packet sent from the Server
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            DebugService.printDebug("ServerNetworkPlayHandler#sendS2CSyncmatica(): sending payload id: {}", payload.getId());
        }
    }

    public static void receiveC2SSyncmatica(SyncmaticaC2SPayload payload, ServerPlayNetworking.Context context)
    {
        DebugService.printDebug("ServerNetworkPlayHandler#receiveC2SSyncmatica(): received payload id: {}, size in bytes {}", payload.getId(), payload.data().getSizeInBytes());
    }
}
