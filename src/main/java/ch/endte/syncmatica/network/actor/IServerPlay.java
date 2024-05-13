package ch.endte.syncmatica.network.actor;

import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;

import java.util.function.Consumer;

public interface IServerPlay
{
    void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation);

    ExchangeTarget syncmatica$getExchangeTarget();
}
