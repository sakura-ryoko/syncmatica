package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.Context;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static ch.endte.syncmatica.Syncmatica.*;

/**
 * If I can get this to work, so be it.
 */
public class ActorClientPlayHandler
{
    private static ActorClientPlayHandler instance;
    private static ClientPlayNetworkHandler clientPlayNetworkHandler;
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

    public void startEvent(final ClientPlayNetworkHandler handler)
    {
        if (clientPlayNetworkHandler == null)
        {
            setClientContext(handler);
        }
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
        Context ctx = Syncmatica.initClient(comms, data, man);
        clientCommunication = comms;
        ScreenHelper.init();
        LitematicManager.getInstance().setActiveContext(Objects.requireNonNull(getContext(CLIENT_CONTEXT)));
    }

    public void packetEvent(final PacketType type, final PacketByteBuf data, final ClientPlayNetworkHandler clientContext, CallbackInfo ci)
    {
        if (clientCommunication == null)
        {
            ActorClientPlayHandler.getInstance().startEvent(clientContext);
        }
        if (packetEvent(type, data))
            if (ci.isCancellable())
                ci.cancel();
    }
    public void packetNbtEvent(final PacketType type, final NbtCompound data, final ClientPlayNetworkHandler clientContext, CallbackInfo ci)
    {
        if (clientCommunication == null)
        {
            ActorClientPlayHandler.getInstance().startEvent(clientContext);
        }
        if (packetNbtEvent(type, data))
            if (ci.isCancellable())
                ci.cancel();
    }

    public boolean packetEvent(final PacketType type, final PacketByteBuf bufSupplier)
    {
        if (clientCommunication.handlePacket(type))
        {
            clientCommunication.onPacket(exTarget, type, bufSupplier);
            return true;
        }
        return false;
    }

    public boolean packetNbtEvent(final PacketType type, final NbtCompound data)
    {
        if (clientCommunication.handlePacket(type))
        {
            clientCommunication.onNbtPacket(exTarget, type, data);
            return true;
        }
        return false;
    }

    public void reset()
    {
        clientCommunication = null;
        exTarget = null;
        clientPlayNetworkHandler = null;
    }

    private static void setClientContext(final ClientPlayNetworkHandler clientHandler) { ActorClientPlayHandler.clientPlayNetworkHandler = clientHandler; }
}
