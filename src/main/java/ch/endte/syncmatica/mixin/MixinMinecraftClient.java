package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.packet.ActorClientPlayNetworkHandler;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Shadow private boolean integratedServerRunning;

    /*
    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    private void syncmatica$onInitComplete(RunArgs args, CallbackInfo ci)
    {
        // Register Play Channels
        if (SyncmaticaReference.isClient())
        {
            SyncLog.initLogger();
            ClientNetworkPlayInitHandler.registerPlayChannels();
        }
    }

    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("HEAD"))
    private void syncmatica$onLoadWorldPre(@Nullable ClientWorld worldClientIn, CallbackInfo ci)
    {
        SyncLog.debug("syncmatica$onLoadWorldPre()");
    }
*/
    @Inject(method = "startIntegratedServer", at = @At("TAIL"))
    private void syncmatica$startIntegratedServer(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld, CallbackInfo ci)
    {
        if (this.integratedServerRunning)
            SyncmaticaReference.setIntegratedServer(true);
    }

    @Inject(method = "disconnect()V", at = @At("HEAD"))
    private void syncmatica$shutdown(final CallbackInfo ci)
    {
        // #REMOVE: ChannelManager.onDisconnected();
        //SyncLog.debug("MixinMinecraftClient#shutdownSyncmatica(): calling ScreenHelper.close()");
        ScreenHelper.close();
        SyncLog.debug("MixinMinecraftClient#shutdownSyncmatica(): calling Syncmatica.shutdown()");
        Syncmatica.shutdown();
        //SyncLog.debug("MixinMinecraftClient#shutdownSyncmatica(): calling LitematicManager.clear()");
        LitematicManager.clear();

        //if (SyncmaticaReference.isIntegratedServer())
        //{
            //ServerNetworkPlayInitHandler.unregisterReceivers();
        //}
        //ClientNetworkPlayInitHandler.unregisterReceivers();

        SyncLog.debug("MixinMinecraftClient#shutdownSyncmatica(): calling ActorClientPlayNetworkHandler.getInstance().reset()");
        ActorClientPlayNetworkHandler.getInstance().reset();
        SyncmaticaReference.setIntegratedServer(false);
    }
}
