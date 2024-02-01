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
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.channels.SyncmaticaNbtData;
import fi.dy.masa.malilib.util.PayloadUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

import static ch.endte.syncmatica.Syncmatica.*;

/**
 * TODO: Move some of this to a different file, such as SyncmaticaPayloadListener...
 * Also, we shouldn't be using the " Server/ClientPlayNetworkHandler " to send packets, but
 * if I can get this to work, to "plugin" to the Communications Manager / Target Exchange, so be it.
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

    // Payload has been received --> onPacket
    public <T extends CustomPayload> void packetEvent(final ClientPlayNetworkHandler clientContext, final T payload)
    {
        final Identifier id = payload.getId().id();
        //final Supplier<PacketByteBuf> bufSupplier = payload::byteBuf;
        if (clientCommunication == null)
        {
            ActorClientPlayNetworkHandler.getInstance().startEvent(clientContext);
        }
        // #FIXME Do we even want to do this ?
        //packetEvent(PacketType.getType(id), payload);
        // No longer called from a Mixin.
        //if ()
        //{
//            ci.cancel(); // prevent further unnecessary comparisons and reporting a warning
//        }
    }

    public void packetEvent(final PacketType type, final PacketByteBuf bufSupplier)
    {
        if (clientCommunication.handlePacket(type))
        {
            clientCommunication.onPacket(exTarget, type, bufSupplier);
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
