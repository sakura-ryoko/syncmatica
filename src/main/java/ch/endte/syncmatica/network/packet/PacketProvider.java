package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.event.SyncmaticaPayloadServerHandler;
import ch.endte.syncmatica.service.DebugService;
import fi.dy.masa.malilib.event.SyncmaticaPayloadHandler;

public class PacketProvider
{
    static SyncmaticaPayloadListener syncmaticaClientListener = new SyncmaticaPayloadListener();
    static SyncmaticaPayloadServerListener syncmaticaServerListener = new SyncmaticaPayloadServerListener();
    public static void registerPayloads()
    {
        DebugService.printDebug("PacketProvider#registerPayloads(): registerSyncmaticaHandler()");
        if (SyncmaticaReference.isClient())
            SyncmaticaPayloadHandler.getInstance().registerSyncmaticaHandler(syncmaticaClientListener);
        if (SyncmaticaReference.isServer())
            SyncmaticaPayloadServerHandler.getInstance().registerSyncmaticaServerHandler(syncmaticaServerListener);
    }

    public static void unregisterPayloads()
    {
        DebugService.printDebug("PacketProvider#unregisterPayloads(): unregisterSyncmaticaHandler()");
        if (SyncmaticaReference.isClient())
            SyncmaticaPayloadHandler.getInstance().unregisterSyncmaticaHandler(syncmaticaClientListener);
        if (SyncmaticaReference.isServer())
            SyncmaticaPayloadServerHandler.getInstance().unregisterSyncmaticaServerHandler(syncmaticaServerListener);
    }
}
