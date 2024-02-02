package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer
{
    @Shadow public abstract File getRunDirectory();

    @Inject(method = "openToLan", at = @At("TAIL"))
    private void syncmatica$openToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        SyncLog.debug("MixinIntegratedServer#syncmatica$openToLan(): invoked.");
        /*
        if (Syncmatica.getContext(Syncmatica.SERVER_CONTEXT) == null)
        {
            Syncmatica.initServer(
                    new ServerCommunicationManager(),
                    new FileStorage(),
                    new SyncmaticManager(),
                    SyncmaticaReference.isIntegratedServer(),
                    getRunDirectory()
            ).startup();
        }*/
    }
}
