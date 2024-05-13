package ch.endte.syncmatica;

import net.fabricmc.api.ModInitializer;

/**
 * This helps Syncmatica init the Channel Registrations at game launch.
 */
public class ModInit implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Syncmatica.preInit();
    }
}
