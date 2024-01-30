package ch.endte.syncmatica.network.legacy;

@Deprecated
//@Mixin(ServerCommonNetworkHandler.class)
public class MixinServerCommonNetworkHandler {
    /*
    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void onCustomSyncmaticaPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        Object thiss = this;
        if (thiss instanceof ServerPlayNetworkHandler impl && packet.payload() instanceof SyncmaticaS2CPayload payload) {
            // ChannelManager.onChannelRegisterHandle(playerNetworkHandler.ggetExchangeTarget(), packet.getChannel(), packet.getData());
            if (PacketType.containsIdentifier(payload.getIdentifier())) {
                NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), payload.getIdentifier(), payload.getPacket()));
            }
        }
    }
     */
}
