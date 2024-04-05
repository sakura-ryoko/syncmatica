package ch.endte.syncmatica.mixin;

import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class MixinServerCommonNetworkHandler
{
    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void syncmatica$onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci)
    {
        //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): invoked");
        // NO-OP
    }
}
