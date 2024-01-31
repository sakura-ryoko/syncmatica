package ch.endte.syncmatica;

import net.fabricmc.api.DedicatedServerModInitializer;

/**
 * Launches preInit() for Dedicated servers
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
