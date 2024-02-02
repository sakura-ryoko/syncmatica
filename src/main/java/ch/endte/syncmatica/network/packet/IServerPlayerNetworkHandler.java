package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;

import java.util.function.Consumer;

public interface IServerPlayerNetworkHandler {
    void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation);

    ExchangeTarget syncmatica$getExchangeTarget();
}
