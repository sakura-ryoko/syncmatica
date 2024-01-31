package ch.endte.syncmatica;

import net.fabricmc.api.ClientModInitializer;

/**
 * Launches preInit for Clients
 */
public class SyncmaticaClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        if (!Syncmatica.MOD_INIT)
            Syncmatica.preInitClient();
    }
}
