package ch.endte.syncmatica.network.legacy;

import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerCommonNetworkHandler.class)
@Deprecated
public class MixinServerCommonNetworkHandler {
    /**
     * it doesn't work
     * *
    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void syncmatica$onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci)
    {
        SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): invoked");
        Object thiss = this;
        if (thiss instanceof ServerPlayNetworkHandler impl)
        {
            CustomPayload payload = packet.payload();
            PacketType type = PacketType.getType(payload.getId().id());

            if (type != null)
            {
                RegisterVersion recv = (RegisterVersion) payload;
                SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), recv.byteBuf().readableBytes());
                NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                // Dangerous to just cast it, but what else can we do here?
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, recv.byteBuf()));
            }
            // Not for us
        }
    }*/
}
