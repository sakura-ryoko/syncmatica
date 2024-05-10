package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.data.FileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.network.client.ClientPlayRegister;
import ch.endte.syncmatica.network.payload.PayloadManager;
import ch.endte.syncmatica.network.server.ServerPlayRegister;
import ch.endte.syncmatica.util.SyncLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer
{
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
    private void syncmatica$onServerStarting(CallbackInfo ci)
    {
        final MinecraftServer server = (MinecraftServer) (Object) this;
        SyncLog.debug("MixinMinecraftServer#onServerStarting(): invoked.");

        if (server.isDedicated())
        {
            SyncmaticaReference.setDedicatedServer(true);
            SyncmaticaReference.setOpenToLan(false);
        }
        if (server.isSingleplayer())
        {
            SyncmaticaReference.setSinglePlayer(true);
            SyncmaticaReference.setOpenToLan(false);
        }

        // Register in case for whatever reason they aren't already
        PayloadManager.registerPlayChannels();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", ordinal = 0), method = "runServer")
    private void syncmatica$onServerStarted(CallbackInfo ci)
    {
        final MinecraftServer server = (MinecraftServer) (Object) this;
        SyncLog.debug("MixinMinecraftServer#onServerStarted(): invoked.");

        if (server.isDedicated())
        {
            SyncmaticaReference.setDedicatedServer(true);
            SyncmaticaReference.setOpenToLan(false);
        }
        if (server.isSingleplayer())
        {
            SyncmaticaReference.setSinglePlayer(true);
            SyncmaticaReference.setOpenToLan(false);
        }
        if (SyncmaticaReference.isServer() || SyncmaticaReference.isDedicatedServer() || SyncmaticaReference.isIntegratedServer() || SyncmaticaReference.isOpenToLan())
        {
            ServerPlayRegister.registerReceivers();
        }
        if (SyncmaticaReference.isClient() || SyncmaticaReference.isSinglePlayer())
        {
            ClientPlayRegister.registerReceivers();
        }

        SyncLog.debug("MixinMinecraftServer#onServerStarted(): processing Syncmatica.initServer().");
        // Process Syncmatica Server Context
        Syncmatica.initServer(
                new ServerCommunicationManager(),
                new FileStorage(),
                new SyncmaticManager(),
                SyncmaticaReference.isIntegratedServer(),
                server.getSavePath(WorldSavePath.ROOT).toFile()
        ).startup();
    }

    @Inject(at = @At("HEAD"), method = "shutdown")
    private void syncmatica$onServerStopping(CallbackInfo info)
    {
        //final MinecraftServer server = (MinecraftServer) (Object) this;
        SyncLog.debug("MixinMinecraftServer#onServerStopping(): invoked.");
    }

    @Inject(at = @At("TAIL"), method = "shutdown")
    private void syncmatica$onServerStopped(CallbackInfo info)
    {
        //final MinecraftServer server = (MinecraftServer) (Object) this;
        SyncLog.debug("MixinMinecraftServer#onServerStopped(): invoked.");

        if (SyncmaticaReference.isServer() || SyncmaticaReference.isDedicatedServer() || SyncmaticaReference.isIntegratedServer() || SyncmaticaReference.isOpenToLan())
        {
            ServerPlayRegister.unregisterReceivers();
        }
        if (SyncmaticaReference.isClient() || SyncmaticaReference.isSinglePlayer())
        {
            ClientPlayRegister.unregisterReceivers();
        }
        SyncmaticaReference.setIntegratedServer(false);
        SyncmaticaReference.setSinglePlayer(false);

        // Process Syncmatica Shutdown
        SyncLog.debug("MixinMinecraftServer#onServerStopped(): processing Syncmatica.shutdown().");
        Syncmatica.shutdown();
    }
}
