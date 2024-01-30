package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.service.DebugService;
import fi.dy.masa.malilib.interfaces.ISyncmaticaPayloadListener;
import fi.dy.masa.malilib.network.ClientNetworkPlayHandler;
import fi.dy.masa.malilib.network.payload.SyncmaticaPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * Uses MaLiLib Network API for Client end
 */
public class SyncmaticaPayloadListener implements ISyncmaticaPayloadListener
{
    public void sendSyncmaticaPayload(NbtCompound data)
    {
        DebugService.printDebug("SyncmaticaPayloadListener#sendSyncmaticaPayload(): sending payload of size: {}", data.getSizeInBytes());
        SyncmaticaPayload payload = new SyncmaticaPayload(data);
        ClientNetworkPlayHandler.sendSyncmatica(payload);
    }
    public void receiveSyncmaticaPayload(NbtCompound data, ClientPlayNetworking.Context ctx, Identifier id)
    {
        decodeSyncmaticaPayload(data, id);
    }

    public void encodeSyncmaticaPayload(NbtCompound data, Identifier id)
    {
        // Client->Server (C2S) encoder
        NbtCompound nbt = new NbtCompound();
        nbt.copyFrom(data);
        DebugService.printDebug("SyncmaticaPayloadListener#encodeSyncmaticaPayload(): id: {}, encoding payload of size: {}", id.toString(), data.getSizeInBytes());
        sendSyncmaticaPayload(nbt);
    }

    public void decodeSyncmaticaPayload(NbtCompound data, Identifier id)
    {
        // Server->Client (S2C) decoder
        DebugService.printDebug("SyncmaticaPayloadListener#decodeSyncmaticaPayload(): id: {}, decoding payload of size: {}", id.toString(), data.getSizeInBytes());
        String hello = data.getString("hello");
        DebugService.printDebug("SyncmaticaPayloadListener#decodeSyncmaticaPayload(): id: {}, received: {}", id.toString(), hello);
    }
}
