package ch.endte.syncmatica.network;

import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.RedirectFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.payload.PacketType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import static ch.endte.syncmatica.Syncmatica.*;

/**
 * If I can get this to work, so be it.
 */
public class ActorClientPlayNetworkHandler
{
    private static ActorClientPlayNetworkHandler instance;
    private static ClientPlayNetworkHandler clientPlayNetworkHandler;
    private CommunicationManager clientCommunication;
    private ExchangeTarget exTarget;

    public static ActorClientPlayNetworkHandler getInstance()
    {
        if (instance == null)
        {
            instance = new ActorClientPlayNetworkHandler();
        }

        return instance;
    }

    public void startEvent(final ClientPlayNetworkHandler handler)
    {
        setClientContext(handler);
        startClient();
    }

    public void startClient()
    {
        if (clientPlayNetworkHandler == null)
        {
            throw new RuntimeException("Tried to start client before receiving a connection");
        }
        final IFileStorage data = new RedirectFileStorage();
        final SyncmaticManager man = new SyncmaticManager();
        exTarget = new ExchangeTarget(clientPlayNetworkHandler);
        final CommunicationManager comms = new ClientCommunicationManager(exTarget);
        Syncmatica.initClient(comms, data, man);
        clientCommunication = comms;
        ScreenHelper.init();
        LitematicManager.getInstance().setActiveContext(Syncmatica.getContext(CLIENT_CONTEXT));
    }

    public void packetEvent(final PacketType type, final PacketByteBuf data, final ClientPlayNetworkHandler clientContext)
    {
        if (clientCommunication == null)
        {
            ActorClientPlayNetworkHandler.getInstance().startEvent(clientContext);
        }
        // No longer called from a Mixin.
        packetEvent(type, data);
    }
    public void packetNbtEvent(final PacketType type, final NbtCompound data, final ClientPlayNetworkHandler clientContext)
    {
        if (clientCommunication == null)
        {
            ActorClientPlayNetworkHandler.getInstance().startEvent(clientContext);
        }
        packetNbtEvent(type, data);
    }

    public void packetEvent(final PacketType type, final PacketByteBuf bufSupplier)
    {
        if (clientCommunication.handlePacket(type))
        {
            clientCommunication.onPacket(exTarget, type, bufSupplier);
        }
    }

    public void packetNbtEvent(final PacketType type, final NbtCompound data)
    {
        if (clientCommunication.handlePacket(type))
        {
            clientCommunication.onNbtPacket(exTarget, type, data);
        }
    }

    public void reset()
    {
        clientCommunication = null;
        exTarget = null;
        clientPlayNetworkHandler = null;
    }

    private static void setClientContext(final ClientPlayNetworkHandler clientContext) { ActorClientPlayNetworkHandler.clientPlayNetworkHandler = clientContext; }

}
