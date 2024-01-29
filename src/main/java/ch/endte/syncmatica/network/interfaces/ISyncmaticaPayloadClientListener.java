package ch.endte.syncmatica.network.interfaces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ISyncmaticaPayloadClientListener
{
    // Client
    default void sendSyncmaticaClientPayload(NbtCompound data) { }
    default void receiveSyncmaticaClientPayload(NbtCompound data, ClientPlayNetworking.Context ctx) { }
    default void encodeSyncmaticaClientPayload(NbtCompound data) { }
    default void decodeSyncmaticaClientPayload(NbtCompound data) { }
}
