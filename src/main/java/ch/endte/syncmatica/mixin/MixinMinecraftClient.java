package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.packet.ActorClientPlayHandler;
import ch.endte.syncmatica.util.SyncLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Shadow private boolean integratedServerRunning;

    @Inject(method = "startIntegratedServer", at = @At("TAIL"))
    private void syncmatica$startIntegratedServer(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld, CallbackInfo ci)
    {
        if (this.integratedServerRunning)
            SyncmaticaReference.setIntegratedServer(true);
    }

    @Inject(method = "disconnect()V", at = @At("HEAD"))
    private void syncmatica$shutdown(final CallbackInfo ci)
    {
        ScreenHelper.close();
        SyncLog.debug("MixinMinecraftClient#shutdownSyncmatica(): calling Syncmatica.shutdown()");
        Syncmatica.shutdown();
        LitematicManager.clear();

        SyncLog.debug("MixinMinecraftClient#shutdownSyncmatica(): calling ActorClientPlayHandler.getInstance().reset()");
        ActorClientPlayHandler.getInstance().reset();
        SyncmaticaReference.setIntegratedServer(false);
    }
}
