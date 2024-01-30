package ch.endte.syncmatica.event;


import ch.endte.syncmatica.interfaces.ISyncmaticaPayloadServerListener;

public interface ISyncmaticaPayloadServerManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     */
    void registerSyncmaticaServerHandler(ISyncmaticaPayloadServerListener handler);
    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     */
    void unregisterSyncmaticaServerHandler(ISyncmaticaPayloadServerListener handler);
}
