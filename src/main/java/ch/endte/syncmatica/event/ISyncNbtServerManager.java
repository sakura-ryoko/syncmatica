package ch.endte.syncmatica.event;


import ch.endte.syncmatica.interfaces.ISyncNbtServerListener;

public interface ISyncNbtServerManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     */
    void registerSyncNbtServerHandler(ISyncNbtServerListener handler);
    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     */
    void unregisterSyncNbtServerHandler(ISyncNbtServerListener handler);
}
