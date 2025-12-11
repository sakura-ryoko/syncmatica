package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.data.FileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer
{
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"), method = "runServer")
    private void syncmatica$onServerStarting(CallbackInfo ci)
    {
        final MinecraftServer server = (MinecraftServer) (Object) this;
        Syncmatica.debug("MixinMinecraftServer#onServerStarting()");

        if (server.isDedicatedServer())
        {
            Reference.setDedicatedServer(true);
            Reference.setOpenToLan(false);
        }
        if (server.isSingleplayer())
        {
            Reference.setOpenToLan(false);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;", ordinal = 0), method = "runServer")
    private void syncmatica$onServerStarted(CallbackInfo ci)
    {
        final MinecraftServer server = (MinecraftServer) (Object) this;
        Syncmatica.debug("MixinMinecraftServer#onServerStarted()");

        if (server.isDedicatedServer())
        {
            Reference.setDedicatedServer(true);
            Reference.setOpenToLan(false);
        }
        if (server.isSingleplayer())
        {
            Reference.setOpenToLan(false);
        }

        // Process Syncmatica Server Context
        Syncmatica.initServer(
                new ServerCommunicationManager(),
                new FileStorage(),
                new SyncmaticManager(),
                !server.isDedicatedServer(),
                server.getWorldPath(LevelResource.ROOT).normalize()
        ).startup();
    }

    @Inject(at = @At("TAIL"), method = "stopServer")
    private void syncmatica$onServerStopped(CallbackInfo info)
    {
        //final MinecraftServer server = (MinecraftServer) (Object) this;
        Syncmatica.debug("MixinMinecraftServer#onServerStopped()");

        Reference.setIntegratedServer(false);
        Syncmatica.shutdown();
    }
}
