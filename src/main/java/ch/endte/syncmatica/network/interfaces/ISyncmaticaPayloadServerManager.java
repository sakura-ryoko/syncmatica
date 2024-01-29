package ch.endte.syncmatica.network.interfaces;


public interface ISyncmaticaPayloadServerManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void registerSyncmaticaServerHandler(ISyncmaticaPayloadServerListener handler);
    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void unregisterSyncmaticaServerHandler(ISyncmaticaPayloadServerListener handler);
}
