package ch.endte.syncmatica.interfaces;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public interface ISyncPacketClientListener
{
    default void sendSyncPacketClient(SyncByteBuf data) { }
    default void receiveSyncPacketClient(SyncByteBuf data, ClientPlayNetworking.Context ctx) { }
}
