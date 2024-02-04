package ch.endte.syncmatica;

import net.fabricmc.api.ClientModInitializer;

/**
 * Launches preInit() for Clients (And sets the MOD_ENV / MOD_VERSION, etc.)
 */
public class SyncmaticaClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        if (!Syncmatica.MOD_INIT)
            Syncmatica.preInitClient();
    }
}
