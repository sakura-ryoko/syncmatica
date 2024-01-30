package ch.endte.syncmatica.network.legacy;

@Deprecated
//@Mixin(value = ServerPlayNetworkHandler.class, priority = 998)
public abstract class MixinServerPlayNetworkHandler implements IServerPlayerNetworkHandler {
    /*
    @Unique
    private ExchangeTarget exTarget = null;

    @Unique
    private ServerCommunicationManager comManager = null;

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    @Deprecated
    public void onDisconnected(final Text reason, final CallbackInfo ci) {
        ChannelManager.onDisconnected();
        syncmatica$operateComms(sm -> sm.onPlayerLeave(syncmatica$getExchangeTarget()));
    }

    @Deprecated
    public void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation) {
        if (comManager == null) {
            final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (con != null) {
                comManager = (ServerCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null) {
            operation.accept(comManager);
        }
    }

    @Deprecated
    public ExchangeTarget syncmatica$getExchangeTarget() {
        if (exTarget == null) {
            exTarget = new ExchangeTarget((ServerPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
     */
}
