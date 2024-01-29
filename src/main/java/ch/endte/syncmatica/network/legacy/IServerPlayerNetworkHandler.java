package ch.endte.syncmatica.network.legacy;

import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;

import java.util.function.Consumer;

@Deprecated
public interface IServerPlayerNetworkHandler {
    void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation);

    ExchangeTarget syncmatica$getExchangeTarget();
}
