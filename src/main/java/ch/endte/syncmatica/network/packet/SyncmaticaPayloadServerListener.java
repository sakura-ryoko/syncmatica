package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.network.interfaces.ISyncmaticaPayloadServerListener;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncmaticaPayloadServerListener implements ISyncmaticaPayloadServerListener
{
    public void sendSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        DebugService.printDebug("SyncmaticaPayloadServerListener#sendSyncmaticaServerPayload(): sending payload of size: {} to player: {}", data.getSizeInBytes(), player.getName().getLiteralString());
    }
    public void receiveSyncmaticaServerPayload(NbtCompound data, ServerPlayNetworking.Context ctx)
    {
        DebugService.printDebug("SyncmaticaPayloadServerListener#receiveSyncmaticaServerPayload(): receiving payload of size: {} from player: {}", data.getSizeInBytes(), ctx.player().getName().getLiteralString());
    }

    public void encodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        // Client->Server (C2S) encoder
        DebugService.printDebug("SyncmaticaPayloadServerListener#encodeSyncmaticaServerPayload(): encoding payload of size: {} for player {}", data.getSizeInBytes(), player.getName().getLiteralString());
    }

    public void decodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        // Server->Client (S2C) decoder
        DebugService.printDebug("SyncmaticaPayloadServerListener#decodeSyncmaticaServerPayload(): decoding payload of size: {} from player {}", data.getSizeInBytes(), player.getName().getLiteralString());
    }
}
