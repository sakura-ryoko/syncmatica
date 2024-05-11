package ch.endte.syncmatica;

import net.fabricmc.api.ModInitializer;

public class ModInit implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Syncmatica.preInit();
    }
}
