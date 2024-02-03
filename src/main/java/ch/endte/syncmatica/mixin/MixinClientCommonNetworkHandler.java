package ch.endte.syncmatica.mixin;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

/**
 * I only added this in case we need to move the "onCustomPayload" call in the future.
 */
@Mixin(ClientCommonNetworkHandler.class)
public class MixinClientCommonNetworkHandler
{
    // NO-OP
}
