package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.server.ServerPlayRegister;
import ch.endte.syncmatica.util.SyncLog;
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
            SyncLog.debug("syncmatica$setupServer(): Integrated Server detected");
            SyncmaticaReference.setIntegratedServer(true);
            SyncmaticaReference.setOpenToLan(false);
            SyncmaticaReference.setDedicatedServer(false);
        }
    }

    @Inject(method = "openToLan", at = @At("RETURN"))
    private void syncmatica$checkOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            SyncLog.debug("syncmatica$setupServer(): OpenToLan detected");
            SyncmaticaReference.setIntegratedServer(true);
            SyncmaticaReference.setOpenToLan(true);
            SyncmaticaReference.setDedicatedServer(false);

            ServerPlayRegister.registerReceivers();
        }
    }
}
