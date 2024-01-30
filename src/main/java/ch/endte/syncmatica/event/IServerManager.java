package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.IServerListener;

public interface IServerManager
{
    void registerServerHandler(IServerListener handler);
    void unregisterServerHandler(IServerListener handler);
}
