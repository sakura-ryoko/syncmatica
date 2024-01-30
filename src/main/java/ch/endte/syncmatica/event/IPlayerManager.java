package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.IPlayerListener;

public interface IPlayerManager
{
    void registerPlayerHandler(IPlayerListener handler);
    void unregisterPlayerHandler(IPlayerListener handler);
}
