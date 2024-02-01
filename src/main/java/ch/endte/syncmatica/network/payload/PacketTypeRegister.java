package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class PacketTypeRegister
{
    // This is how it looks in the static context per a MOD,
    // Where each channel must each include its own Custom Payload Records.
    // --> The send/receive handlers can be made into an interface.
    private static boolean playRegistered = false;
    public static <T extends CustomPayload> void registerPlayChannel(CustomPayload.Id<T> id, PacketCodec<PacketByteBuf, T> codec)
    {
        SyncLog.debug("PacketTypeRegister#registerPlayChannel(): register {} // {}:{}", id.hashCode(), id.id().getNamespace(), id.id().getPath());
        PayloadTypeRegistry.playC2S().register(id, codec);
        PayloadTypeRegistry.playS2C().register(id, codec);
    }
    public static void registerPlayChannels()
    {
        // Don't invoke more than once
        if (playRegistered)
            return;

        SyncLog.debug("PacketTypeRegister#registerPlayChannels(): registering play channels.");
        registerPlayChannel(CancelShare.TYPE, CancelShare.CODEC);
        registerPlayChannel(ConfirmUser.TYPE, ConfirmUser.CODEC);
        registerPlayChannel(Feature.TYPE, Feature.CODEC);
        registerPlayChannel(FeatureRequest.TYPE, FeatureRequest.CODEC);
        registerPlayChannel(FinishedLitematic.TYPE, FinishedLitematic.CODEC);
        registerPlayChannel(Message.TYPE, Message.CODEC);
        registerPlayChannel(Modify.TYPE, Modify.CODEC);
        registerPlayChannel(ModifyFinish.TYPE, ModifyFinish.CODEC);
        registerPlayChannel(ModifyRequest.TYPE, ModifyRequest.CODEC);
        registerPlayChannel(ModifyRequestAccept.TYPE, ModifyRequestAccept.CODEC);
        registerPlayChannel(ModifyRequestDeny.TYPE, ModifyRequestDeny.CODEC);
        registerPlayChannel(ReceivedLitematic.TYPE, ReceivedLitematic.CODEC);
        registerPlayChannel(RegisterMetadata.TYPE, RegisterMetadata.CODEC);
        registerPlayChannel(RegisterVersion.TYPE, RegisterVersion.CODEC);
        registerPlayChannel(RemoveSyncmatic.TYPE, RemoveSyncmatic.CODEC);
        registerPlayChannel(RequestDownload.TYPE, RequestDownload.CODEC);
        registerPlayChannel(SendLitematic.TYPE, SendLitematic.CODEC);
        registerPlayChannel(SyncmaticaNbtData.TYPE, SyncmaticaNbtData.CODEC);

        playRegistered = true;
    }
}
