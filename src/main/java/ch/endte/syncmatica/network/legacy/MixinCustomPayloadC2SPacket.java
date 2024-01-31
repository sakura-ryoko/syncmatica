package ch.endte.syncmatica.network.legacy;

/**
 * The readPayload() method has been removed by Mojang
 */
//@Mixin(CustomPayloadC2SPacket.class)
@Deprecated
public class MixinCustomPayloadC2SPacket {
    /*
    @Inject(method = "readPayload", at = @At(value = "HEAD"), cancellable = true)
    private static void readPayload(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
        if (id.getNamespace().equals(Syncmatica.MOD_ID)) {
            cir.setReturnValue(new SyncmaticaPayload(id, buf));
        }
    }
     */
}
