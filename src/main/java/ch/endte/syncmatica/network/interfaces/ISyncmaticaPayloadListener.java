package ch.endte.syncmatica.network.interfaces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

public interface ISyncmaticaPayloadListener
{
    default void sendSyncmaticaPayload(NbtCompound data) { }
    default void receiveSyncmaticaPayload(NbtCompound data, ClientPlayNetworking.Context ctx) { }
    default void encodeSyncmaticaPayload(NbtCompound data) { }
    default void decodeSyncmaticaPayload(NbtCompound data) { }
}
