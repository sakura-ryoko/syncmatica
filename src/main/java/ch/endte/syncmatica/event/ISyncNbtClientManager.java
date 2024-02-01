package ch.endte.syncmatica.event;


import ch.endte.syncmatica.interfaces.ISyncNbtClientListener;

public interface ISyncNbtClientManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void registerSyncNbtClientHandler(ISyncNbtClientListener handler);

    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void unregisterSyncNbtClientHandler(ISyncNbtClientListener handler);
}
