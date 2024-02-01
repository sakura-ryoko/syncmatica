package ch.endte.syncmatica.event;


import ch.endte.syncmatica.interfaces.ISyncPacketServerListener;

public interface ISyncPacketServerManager
{
    /**
     * Registers a handler for receiving Carpet Hello NBTCompound packets.
     */
    void registerSyncPacketServerHandler(ISyncPacketServerListener handler);
    /**
     * Un-Registers a handler for receiving Carpet Hello NBTCompound packets.
     */
    void unregisterSyncPacketServerHandler(ISyncPacketServerListener handler);
}
