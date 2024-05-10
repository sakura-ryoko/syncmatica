package ch.endte.syncmatica.network.client;

import javax.annotation.Nonnull;
import java.util.Objects;
import ch.endte.syncmatica.network.channels.*;
import ch.endte.syncmatica.network.packet.ActorClientPlayHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.util.PayloadUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Main Fabric API Networking-based packet senders / receivers (Client Context)
 */
public class ClientPlayHandler
{
    public static <T extends CustomPayload> void sendSyncPacket(@Nonnull T payload)
    {
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
        }
    }

    public static <T extends CustomPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ClientPlayNetworkHandler handler)
    {
        Packet<?> packet = new CustomPayloadC2SPacket(payload);
        if (handler.accepts(packet))
        {
            handler.sendPacket(packet);
        }
    }

    public static void receiveSyncPacket(PacketType type, SyncByteBuf data, @Nonnull ClientPlayNetworkHandler handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        PacketByteBuf out = PayloadUtils.fromSyncBuf(data);
        //SyncLog.debug("ClientPlayHandler#receiveSyncPacket(): received payload id: {}, size in bytes {}", type.getId().toString(), out.readableBytes());

        ActorClientPlayHandler.getInstance().packetEvent(type, out, handler, ci);
    }

    public static void receiveCancelShare(SyncCancelShare data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_SHARE, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveCancelLitematic(SyncCancelLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_LITEMATIC, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveConfirmUser(SyncConfirmUser data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CONFIRM_USER, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveFeature(SyncFeature data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveFeatureRequest(SyncFeatureRequest data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE_REQUEST, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveFinishedLitematic(SyncFinishedLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FINISHED_LITEMATIC, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveMessage(SyncMessage data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MESSAGE, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveModify(SyncModify data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveModifyFinish(SyncModifyFinish data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_FINISH, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveModifyRequest(SyncModifyRequest data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveModifyRequestAccept(SyncModifyRequestAccept data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_ACCEPT, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveModifyRequestDeny(SyncModifyRequestDeny data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_DENY, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveReceivedLitematic(SyncReceivedLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.RECEIVED_LITEMATIC, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveRegisterMetadata(SyncRegisterMetadata data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_METADATA, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveRegisterVersion(SyncRegisterVersion data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_VERSION, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveRemoveSyncmatic(SyncRemoveSyncmatic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REMOVE_SYNCMATIC, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }
    public static void receiveRequestDownload(SyncRequestDownload data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REQUEST_LITEMATIC, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static void receiveSendLitematic(SyncSendLitematic data, ClientPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.SEND_LITEMATIC, data.byteBuf(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }
}
