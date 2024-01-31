package ch.endte.syncmatica.interfaces;

import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface ISyncmaticaPayloadListener
{
    default void sendSyncmaticaPayload(NbtCompound data) { }
    default void receiveSyncmaticaPayload(NbtCompound data, ClientPlayNetworking.Context ctx) { }
    default void encodeSyncmaticaPayload(NbtCompound data, SyncmaticaPacketType type) { }
    default void decodeSyncmaticaPayload(NbtCompound data) { }
}
