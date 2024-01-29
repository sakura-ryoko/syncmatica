package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.network.interfaces.ISyncmaticaPayloadClientListener;
import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

public class SyncmaticaPayloadClientListener implements ISyncmaticaPayloadClientListener
{
    public void sendSyncmaticaClientPayload(NbtCompound data)
    {
        DebugService.printDebug("SyncmaticaPayloadClientListener#sendSyncmaticaClientPayload(): sending payload of size: {}", data.getSizeInBytes());
    }
    public void receiveSyncmaticaClientPayload(NbtCompound data, ClientPlayNetworking.Context ctx)
    {
        DebugService.printDebug("SyncmaticaPayloadClientListener#receiveSyncmaticaClientPayload(): receiving payload of size: {}", data.getSizeInBytes());
    }

    public void encodeSyncmaticaClientPayload(NbtCompound data)
    {
        // Client->Server (C2S) encoder
        DebugService.printDebug("SyncmaticaPayloadClientListener#encodeSyncmaticaClientPayload(): encoding payload of size: {}", data.getSizeInBytes());
    }

    public void decodeSyncmaticaClientPayload(NbtCompound data)
    {
        // Server->Client (S2C) decoder
        DebugService.printDebug("SyncmaticaPayloadClientListener#decodeSyncmaticaClientPayload(): decoding payload of size: {}", data.getSizeInBytes());
    }
}
