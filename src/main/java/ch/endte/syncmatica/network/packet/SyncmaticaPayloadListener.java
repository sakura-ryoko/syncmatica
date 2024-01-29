package ch.endte.syncmatica.network.packet;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.ISyncmaticaPayloadListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

public class SyncmaticaPayloadListener implements ISyncmaticaPayloadListener
{
    public void sendSyncmaticaPayload(NbtCompound data)
    {
        MaLiLib.printDebug("SyncmaticaPayloadListener#sendSyncmaticaPayload(): sending payload of size: {}", data.getSizeInBytes());
    }
    public void receiveSyncmaticaPayload(NbtCompound data, ClientPlayNetworking.Context ctx)
    {
        MaLiLib.printDebug("SyncmaticaPayloadListener#receiveSyncmaticaPayload(): receiving payload of size: {}", data.getSizeInBytes());
    }

    public void encodeSyncmaticaPayload(NbtCompound data)
    {
        // Client->Server (C2S) encoder
        MaLiLib.printDebug("SyncmaticaPayloadListener#encodeSyncmaticaPayload(): encoding payload of size: {}", data.getSizeInBytes());
    }

    public void decodeSyncmaticaPayload(NbtCompound data)
    {
        // Server->Client (S2C) decoder
        MaLiLib.printDebug("SyncmaticaPayloadListener#decodeSyncmaticaPayload(): decoding payload of size: {}", data.getSizeInBytes());
    }
}
