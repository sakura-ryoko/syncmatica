package ch.endte.syncmatica.network.legacy;

import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.RedirectFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import fi.dy.masa.malilib.util.PayloadUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
public class ActorClientPlayNetworkHandler {

    private static ActorClientPlayNetworkHandler instance;
    private static ClientPlayNetworkHandler clientPlayNetworkHandler;
    private CommunicationManager clientCommunication;
    private ExchangeTarget exTarget;

    @Deprecated
    public static ActorClientPlayNetworkHandler getInstance() {
        if (instance == null) {

            instance = new ActorClientPlayNetworkHandler();
        }

        return instance;
    }

    @Deprecated
    public void startEvent(final ClientPlayNetworkHandler clientPlayNetworkHandler) {
        setClientPlayNetworkHandler(clientPlayNetworkHandler);
        startClient();
    }

    @Deprecated
    public void startClient() {
        if (clientPlayNetworkHandler == null) {
            throw new RuntimeException("Tried to start client before receiving a connection");
        }
        final IFileStorage data = new RedirectFileStorage();
        final SyncmaticManager man = new SyncmaticManager();
        exTarget = new ExchangeTarget(clientPlayNetworkHandler);
        final CommunicationManager comms = new ClientCommunicationManager(exTarget);
        Syncmatica.initClient(comms, data, man);
        clientCommunication = comms;
        ScreenHelper.init();
        LitematicManager.getInstance().setActiveContext(Syncmatica.getContext(Syncmatica.CLIENT_CONTEXT));
    }

    @Deprecated
    public void packetEvent(final ClientPlayNetworkHandler clientPlayNetworkHandler, final SyncmaticaPayload payload, final CallbackInfo ci) {
        final Identifier id = payload.getId().id();
        PacketByteBuf bufSupplier = PayloadUtils.fromNbt(payload.data(), SyncmaticaPayload.KEY);
        if (clientCommunication == null) {
            ActorClientPlayNetworkHandler.getInstance().startEvent(clientPlayNetworkHandler);
        }
        if (packetEvent(id, bufSupplier)) {

            ci.cancel(); // prevent further unnecessary comparisons and reporting a warning
        }
    }

    @Deprecated
    public boolean packetEvent(final Identifier id, final PacketByteBuf bufSupplier) {
        if (clientCommunication.handlePacket(id)) {
            clientCommunication.onPacket(exTarget, id, bufSupplier);

            return true;
        }

        return false;
    }

    @Deprecated
    public void reset() {
        clientCommunication = null;
        exTarget = null;
        clientPlayNetworkHandler = null;
    }

    @Deprecated
    private static void setClientPlayNetworkHandler(final ClientPlayNetworkHandler clientPlayNetworkHandler) {
        ActorClientPlayNetworkHandler.clientPlayNetworkHandler = clientPlayNetworkHandler;
    }
}
