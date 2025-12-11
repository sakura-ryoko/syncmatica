package ch.endte.syncmatica.network.actor;

import java.util.Objects;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.RedirectFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.PacketType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ch.endte.syncmatica.Syncmatica.CLIENT_CONTEXT;
import static ch.endte.syncmatica.Syncmatica.getContext;

/**
 * If I can get this to work, so be it.
 */
public class ActorClientPlayHandler
{
    private static ActorClientPlayHandler instance;
    private static ClientPacketListener clientPlayNetworkHandler;
    private CommunicationManager clientCommunication;
    private ExchangeTarget exTarget;

    public static ActorClientPlayHandler getInstance()
    {
        if (instance == null)
        {
            instance = new ActorClientPlayHandler();
        }

        return instance;
    }

    public void startEvent(final ClientPacketListener handler)
    {
        Syncmatica.debug("ActorClientPlayHandler#startEvent()");
        if (clientPlayNetworkHandler == null)
        {
            setClientContext(handler);
        }
        startClient();
    }

    public void startClient()
    {
        Syncmatica.debug("ActorClientPlayHandler#startClient()");
        /*
        if (clientPlayNetworkHandler == null)
        {
            throw new RuntimeException("Tried to start client before receiving a connection");
        }
         */
        final IFileStorage data = new RedirectFileStorage();
        final SyncmaticManager man = new SyncmaticManager();
        exTarget = new ExchangeTarget(clientPlayNetworkHandler);
        final CommunicationManager comms = new ClientCommunicationManager(exTarget);
        Syncmatica.initClient(comms, data, man);
        clientCommunication = comms;
        ScreenHelper.init();
        LitematicManager.getInstance().setActiveContext(Objects.requireNonNull(getContext(CLIENT_CONTEXT)));
    }

    public void packetEvent(final PacketType type, final FriendlyByteBuf data, final ClientPacketListener clientContext, CallbackInfo ci)
    {
        if (clientCommunication == null)
        {
            ActorClientPlayHandler.getInstance().startEvent(clientContext);
        }
        if (packetEvent(type, data))
            if (ci.isCancellable())
                ci.cancel();
    }

    public boolean packetEvent(final PacketType type, final FriendlyByteBuf bufSupplier)
    {
        if (clientCommunication.handlePacket(type))
        {
            clientCommunication.onPacket(exTarget, type, bufSupplier);
            return true;
        }
        return false;
    }

    public void reset()
    {
        Syncmatica.debug("ActorClientPlayHandler#reset()");
        clientCommunication = null;
        exTarget = null;
        clientPlayNetworkHandler = null;
    }

    private static void setClientContext(final ClientPacketListener clientHandler) { ActorClientPlayHandler.clientPlayNetworkHandler = clientHandler; }
}
