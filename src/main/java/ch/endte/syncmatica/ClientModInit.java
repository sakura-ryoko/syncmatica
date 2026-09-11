package ch.endte.syncmatica;

import ch.endte.syncmatica.network.SyncmaticaPacket;
import ch.endte.syncmatica.network.handler.ClientPlayHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModInit implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(SyncmaticaPacket.Payload.ID, ClientPlayHandler::receiveSyncPayload);
    }
}
