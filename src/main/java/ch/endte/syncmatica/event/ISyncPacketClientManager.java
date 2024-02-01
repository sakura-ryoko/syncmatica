package ch.endte.syncmatica.event;


import ch.endte.syncmatica.interfaces.ISyncPacketClientListener;

public interface ISyncPacketClientManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void registerSyncPacketClientHandler(ISyncPacketClientListener handler);

    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     * @param handler
     */
    void unregisterSyncPacketClientHandler(ISyncPacketClientListener handler);
}
