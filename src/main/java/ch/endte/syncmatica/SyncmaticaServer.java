package ch.endte.syncmatica;

import net.fabricmc.api.DedicatedServerModInitializer;

/**
 * Launches preInit() for Dedicated servers (And sets the MOD_ENV / MOD_VERSION, etc.)
 */
public class SyncmaticaServer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        if (!Syncmatica.MOD_INIT)
            Syncmatica.preInitServer();
    }
}
