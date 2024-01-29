package ch.endte.syncmatica.network.interfaces;


public interface ISyncmaticaPayloadManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void registerSyncmaticaHandler(ISyncmaticaPayloadListener handler);

    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void unregisterSyncmaticaHandler(ISyncmaticaPayloadListener handler);
}
