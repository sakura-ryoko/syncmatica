package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.interfaces.ISyncmaticaPayloadListener;
import ch.endte.syncmatica.network.ClientNetworkPlayHandler;
import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

/**
 * Copy's My MaLiLib Network API for Clients -- Sakura
 */
public class SyncmaticaPayloadListener implements ISyncmaticaPayloadListener
{
    private CommunicationManager clientCommunication;
    private ExchangeTarget exTarget;

    public void sendSyncmaticaPayload(NbtCompound data)
    {
        SyncLog.debug("SyncmaticaPayloadListener#sendSyncmaticaPayload(): sending payload of size: {}", data.getSizeInBytes());
        SyncmaticaPayload payload = new SyncmaticaPayload(data);
        ClientNetworkPlayHandler.sendSyncmatica(payload);
    }
    public void receiveSyncmaticaPayload(NbtCompound data, ClientPlayNetworking.Context ctx)
    {
        decodeSyncmaticaPayload(data);
    }

    public void encodeSyncmaticaPayload(NbtCompound data, SyncmaticaPacketType type)
    {
        // Client->Server (C2S) encoder
        NbtCompound nbt = new NbtCompound();
        nbt.copyFrom(data);
        nbt.putString("packetType", type.toString());
        SyncLog.debug("SyncmaticaPayloadListener#encodeSyncmaticaPayload(): type: {}, encoding payload of size: {}", type.toString(), data.getSizeInBytes());
        sendSyncmaticaPayload(nbt);
    }

    public void decodeSyncmaticaPayload(NbtCompound data)
    {
        // Server->Client (S2C) decoder
        String type = data.getString("packetType");
        // Parse to (SyncmaticaPacketType)
        SyncLog.debug("SyncmaticaPayloadListener#decodeSyncmaticaPayload(): type: {}, decoding payload of size: {}", type, data.getSizeInBytes());
        String hello = data.getString(SyncmaticaPayload.KEY);
        SyncLog.debug("SyncmaticaPayloadListener#decodeSyncmaticaPayload(): type: {}, received: {}", type, hello);
    }
}
