package ch.endte.syncmatica.network.interfaces;


public interface ISyncmaticaPayloadClientManager
{
    /**
     * Registers a handler for receiving Syncmatica NBTCompound packets.
     * @param handler
     */
    void registerSyncmaticaClientHandler(ISyncmaticaPayloadClientListener handler);

    /**
     * Un-Registers a handler for receiving Syncmatica NBTCompound packets.
     * @param handler
     */
    void unregisterSyncmaticaClientHandler(ISyncmaticaPayloadClientListener handler);
}
