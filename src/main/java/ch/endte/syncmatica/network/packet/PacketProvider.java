package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.network.interfaces.SyncmaticaPayloadHandler;
import ch.endte.syncmatica.service.DebugService;

public class PacketProvider
{
    static SyncmaticaPayloadListener syncmaticaListener = new SyncmaticaPayloadListener();
    public static void registerPayloads()
    {
        DebugService.printDebug("PacketProvider#registerPayloads(): registerSyncmaticaHandler()");
        SyncmaticaPayloadHandler.getInstance().registerSyncmaticaHandler(syncmaticaListener);
    }

    public static void unregisterPayloads()
    {
        DebugService.printDebug("PacketProvider#unregisterPayloads(): unregisterSyncmaticaHandler()");
        SyncmaticaPayloadHandler.getInstance().unregisterSyncmaticaHandler(syncmaticaListener);
    }
}
