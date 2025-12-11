package ch.endte.syncmatica.network.handler;

import javax.annotation.Nonnull;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import ch.endte.syncmatica.network.actor.ActorClientPlayHandler;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Network packet senders / receivers (Client Context)
 */
public class ClientPlayHandler
{
    public static void decodeSyncData(@Nonnull SyncmaticaPacket data, ClientPacketListener handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        ActorClientPlayHandler.getInstance().packetEvent(data.getType(), data.getPacket(), handler, ci);
    }

    public static void encodeSyncData(@Nonnull SyncmaticaPacket data, ClientPacketListener handler)
    {
        SyncmaticaPacket.Payload payload = new SyncmaticaPacket.Payload(data);
        if (handler != null)
        {
            sendSyncPacket(payload, handler);
        }
        else
        {
            sendSyncPacket(payload);
        }
    }

    public static void receiveSyncPayload(@Nonnull SyncmaticaPacket data)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        ActorClientPlayHandler.getInstance().packetEvent(data.getType(), data.getPacket(), Minecraft.getInstance().getConnection(), ci);
    }

    public static void receiveSyncPayload(SyncmaticaPacket.Payload payload, ClientPlayNetworking.Context context)
    {
        // Has threading issues ?
        if (context.client().getConnection() != null)
        {
            decodeSyncData(payload.data(), context.client().getConnection());
        }
        else
        {
            decodeSyncData(payload.data(), Minecraft.getInstance().getConnection());
        }
    }

    public static <T extends CustomPacketPayload> void sendSyncPacket(@Nonnull T payload)
    {
        if (ClientPlayNetworking.canSend(payload.type()))
        {
            ClientPlayNetworking.send(payload);
        }
    }

    public static <T extends CustomPacketPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ClientPacketListener handler)
    {
        Packet<?> packet = new ServerboundCustomPayloadPacket(payload);

        if (handler.shouldHandleMessage(packet))
        {
            handler.send(packet);
        }
    }
}
