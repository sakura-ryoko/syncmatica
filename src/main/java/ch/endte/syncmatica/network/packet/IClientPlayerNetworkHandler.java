package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;

import java.util.function.Consumer;

/**
 * I set up a Client version of this interface in case we need to move the "onCustomPayload" call to "ClientCommonNetworkHandler"
 * ... You know, like in case Mojang removes it.
 */
public interface IClientPlayerNetworkHandler
{
    void syncmatica$operateComms(final Consumer<ClientCommunicationManager> operation);

    ExchangeTarget syncmatica$getExchangeTarget();
}
