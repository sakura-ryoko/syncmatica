package ch.endte.syncmatica.interfaces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

public interface ISyncNbtClientListener
{
    default void sendSyncNbtClient(NbtCompound data) { }
    default void receiveSyncNbtClient(NbtCompound data, ClientPlayNetworking.Context ctx) { }
}
