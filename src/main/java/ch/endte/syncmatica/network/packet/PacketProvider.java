package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.interfaces.SyncmaticaPayloadClientHandler;
import ch.endte.syncmatica.network.interfaces.SyncmaticaPayloadServerHandler;
import ch.endte.syncmatica.service.DebugService;

public class PacketProvider
{
    static SyncmaticaPayloadClientListener syncmaticaClientListener = new SyncmaticaPayloadClientListener();
    static SyncmaticaPayloadServerListener syncmaticaServerListener = new SyncmaticaPayloadServerListener();
    public static void registerPayloads()
    {
        DebugService.printDebug("PacketProvider#registerPayloads(): registerSyncmaticaHandler()");
        if (SyncmaticaReference.isClient())
            SyncmaticaPayloadClientHandler.getInstance().registerSyncmaticaClientHandler(syncmaticaClientListener);
        if (SyncmaticaReference.isServer())
            SyncmaticaPayloadServerHandler.getInstance().registerSyncmaticaServerHandler(syncmaticaServerListener);
    }

    public static void unregisterPayloads()
    {
        DebugService.printDebug("PacketProvider#unregisterPayloads(): unregisterSyncmaticaHandler()");
        if (SyncmaticaReference.isClient())
            SyncmaticaPayloadClientHandler.getInstance().unregisterSyncmaticaClientHandler(syncmaticaClientListener);
        if (SyncmaticaReference.isServer())
            SyncmaticaPayloadServerHandler.getInstance().unregisterSyncmaticaServerHandler(syncmaticaServerListener);
    }
}
