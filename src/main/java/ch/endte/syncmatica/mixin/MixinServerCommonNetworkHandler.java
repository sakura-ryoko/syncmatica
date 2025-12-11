package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import ch.endte.syncmatica.network.handler.ServerPlayHandler;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class MixinServerCommonNetworkHandler
{
    // This exists because of the Communications Manager / Exchange Target system,
    // and FAPI networking is too slow to register the receivers
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$handlePacket(ServerboundCustomPayloadPacket packet, CallbackInfo ci)
    {
        if (packet.payload().type().id().getNamespace().equals(Reference.MOD_ID))
        {
            SyncmaticaPacket.Payload payload = (SyncmaticaPacket.Payload) packet.payload();
            Object thiss = this;

            if (thiss instanceof ServerGamePacketListenerImpl handler)
            {
                ServerPlayHandler.decodeSyncData(payload.data(), handler);
            }

            // Cancel unnecessary processing if a PacketType we own is caught
            if  (ci.isCancellable())
                ci.cancel();
        }
    }
}
