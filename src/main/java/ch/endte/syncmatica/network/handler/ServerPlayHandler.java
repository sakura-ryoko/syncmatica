package ch.endte.syncmatica.network.handler;

import javax.annotation.Nonnull;
import ch.endte.syncmatica.network.actor.IServerPlay;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Network packet senders / receivers (Server Context)
 */
public abstract class ServerPlayHandler
{
    public static void decodeSyncData(@Nonnull SyncmaticaPacket data, @Nonnull ServerGamePacketListenerImpl handler)
    {
        IServerPlay iDo = ((IServerPlay) handler);

        iDo.syncmatica$operateComms(sm -> sm.onPacket(iDo.syncmatica$getExchangeTarget(), data.getType(), data.getPacket()));
    }

    public static void decodeSyncData(@Nonnull SyncmaticaPacket data, @Nonnull IServerPlay iDo)
    {
        iDo.syncmatica$operateComms(sm -> sm.onPacket(iDo.syncmatica$getExchangeTarget(), data.getType(), data.getPacket()));
    }

    public static void encodeSyncData(@Nonnull SyncmaticaPacket data, @Nonnull ServerGamePacketListenerImpl handler)
    {
        sendSyncPacket(new SyncmaticaPacket.Payload(data), handler);
    }

    public static void encodeSyncData(@Nonnull SyncmaticaPacket data, @Nonnull ServerPlayer player)
    {
        sendSyncPacket(new SyncmaticaPacket.Payload(data), player);
    }

    public static void receiveSyncPayload(SyncmaticaPacket.Payload payload, ServerPlayNetworking.Context context)
    {
        decodeSyncData(payload.data(), context.player().connection);
    }

    public static <T extends CustomPacketPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ServerPlayer player)
    {
        if (ServerPlayNetworking.canSend(player, payload.type()))
        {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static <T extends CustomPacketPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ServerGamePacketListenerImpl handler)
    {
        Packet<?> packet = new ClientboundCustomPayloadPacket(payload);

        if (handler.shouldHandleMessage(packet))
        {
            handler.send(packet);
        }
    }
}
