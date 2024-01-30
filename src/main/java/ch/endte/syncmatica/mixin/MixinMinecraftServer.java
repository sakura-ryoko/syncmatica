package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.event.ServerHandler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    /*
    @Inject(method = "startServer", at = @At("RETURN"))
    private static <S extends MinecraftServer> void initSyncmatica(final Function<Thread, S> serverFactory, final CallbackInfoReturnable<S> ci) {
        final MinecraftServer returnValue = ci.getReturnValue();
        Syncmatica.initServer(
                new ServerCommunicationManager(),
                new FileStorage(),
                new SyncmaticManager(),
                !returnValue.isDedicated(),
                returnValue.getSavePath(WorldSavePath.ROOT).toFile()
        ).startup();
    }

    // at
    @Inject(method = "shutdown", at = @At("TAIL"))
    public void shutdownSyncmatica(final CallbackInfo ci) {
        Syncmatica.shutdown();
    }
     */

    /**
     *  Call Backs for IServerManager
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
    private void syncmatica_onServerStarting(CallbackInfo ci)
    {
        ((ServerHandler) ServerHandler.getInstance()).onServerStarting((MinecraftServer) (Object) this);
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", ordinal = 0), method = "runServer")
    private void syncmatica_onServerStarted(CallbackInfo ci)
    {
        ((ServerHandler) ServerHandler.getInstance()).onServerStarted((MinecraftServer) (Object) this);
    }
    @Inject(at = @At("HEAD"), method = "shutdown")
    private void syncmatica_onServerStopping(CallbackInfo info)
    {
        ((ServerHandler) ServerHandler.getInstance()).onServerStopping((MinecraftServer) (Object) this);
    }

    @Inject(at = @At("TAIL"), method = "shutdown")
    private void syncmatica_onServerStopped(CallbackInfo info)
    {
        ((ServerHandler) ServerHandler.getInstance()).onServerStopped((MinecraftServer) (Object) this);
    }
}
