package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.event.SyncmaticaPayloadHandler;
import ch.endte.syncmatica.event.SyncmaticaPayloadServerHandler;
import ch.endte.syncmatica.util.SyncLog;

public class PacketProvider
{
    static SyncmaticaPayloadListener syncmaticaClientListener = new SyncmaticaPayloadListener();
    static SyncmaticaPayloadServerListener syncmaticaServerListener = new SyncmaticaPayloadServerListener();
    public static void registerPayloads()
    {
        SyncLog.debug("PacketProvider#registerPayloads(): registerSyncmaticaHandler()");
        if (SyncmaticaReference.isClient())
            SyncmaticaPayloadHandler.getInstance().registerSyncmaticaHandler(syncmaticaClientListener);
        if (SyncmaticaReference.isServer())
            SyncmaticaPayloadServerHandler.getInstance().registerSyncmaticaServerHandler(syncmaticaServerListener);
    }

    public static void unregisterPayloads()
    {
        SyncLog.debug("PacketProvider#unregisterPayloads(): unregisterSyncmaticaHandler()");
        if (SyncmaticaReference.isClient())
            SyncmaticaPayloadHandler.getInstance().unregisterSyncmaticaHandler(syncmaticaClientListener);
        if (SyncmaticaReference.isServer())
            SyncmaticaPayloadServerHandler.getInstance().unregisterSyncmaticaServerHandler(syncmaticaServerListener);
    }
}
