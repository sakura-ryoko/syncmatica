package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.interfaces.ISyncmaticaPayloadServerListener;
import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.network.ServerNetworkPlayHandler;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncmaticaPayloadServerListener implements ISyncmaticaPayloadServerListener
{
    public void sendSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        DebugService.printDebug("SyncmaticaPayloadServerListener#sendSyncmaticaServerPayload(): sending payload of size: {} to player: {}", data.getSizeInBytes(), player.getName().getLiteralString());
        SyncmaticaPayload payload = new SyncmaticaPayload(data);
        ServerNetworkPlayHandler.sendSyncmaticaServerPayload(payload, player);
    }
    public void receiveSyncmaticaServerPayload(NbtCompound data, ServerPlayNetworking.Context ctx, Identifier id)
    {
        decodeSyncmaticaServerPayload(data, ctx.player(), id);
    }

    public void encodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player, Identifier id)
    {
        // Client->Server (C2S) encoder
        NbtCompound nbt = new NbtCompound();
        nbt.copyFrom(data);
        DebugService.printDebug("SyncmaticaPayloadServerListener#encodeSyncmaticaServerPayload(): encoding payload of size: {} for player {}", data.getSizeInBytes(), player.getName().getLiteralString());
        sendSyncmaticaServerPayload(nbt, player);
    }

    public void decodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player, Identifier id)
    {
        // Server->Client (S2C) decoder
        DebugService.printDebug("SyncmaticaPayloadServerListener#decodeSyncmaticaServerPayload(): decoding payload of size: {} from player {}", data.getSizeInBytes(), player.getName().getLiteralString());
        String hello = data.getString("hello");
        DebugService.printDebug("SyncmaticaPayloadServerListener#decodeSyncmaticaServerPayload(): id: {}, received: {}", id, hello);
    }
}
