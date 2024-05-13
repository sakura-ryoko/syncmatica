package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameMode;

@Mixin(value = IntegratedServer.class)
public class MixinIntegratedServer
{
    @Inject(method = "setupServer", at = @At("RETURN"))
    private void syncmatica$setupServer(CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            Syncmatica.debug("syncmatica$setupServer(): Integrated Server detected");
            Reference.setIntegratedServer(true);
            Reference.setOpenToLan(false);
            Reference.setDedicatedServer(false);
        }
    }

    @Inject(method = "openToLan", at = @At("RETURN"))
    private void syncmatica$checkOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            Syncmatica.debug("syncmatica$checkOpenToLan(): OpenToLan detected");
            Reference.setIntegratedServer(true);
            Reference.setOpenToLan(true);
            Reference.setDedicatedServer(false);

            Context ctx = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (ctx != null)
            {
                ctx.registerReceivers();
            }
        }
    }
}
