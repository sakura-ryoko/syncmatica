package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.interfaces.IPlayerListener;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener implements IPlayerListener
{
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerJoin(): invoked.");
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerLeave(): invoked.");
    }
    /**
     * Convert to the above interface.
     */
    /*
    @Unique
    private ExchangeTarget exTarget = null;

    @Unique
    private ServerCommunicationManager comManager = null;

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void onConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        //IServerPlayerNetworkHandler impl = (IServerPlayerNetworkHandler) player.networkHandler;
        //impl.syncmatica$operateComms(sm -> sm.onPlayerJoin(impl.syncmatica$getExchangeTarget(), player));
    }

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
