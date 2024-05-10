package ch.endte.syncmatica;

import net.fabricmc.api.ModInitializer;

public class EntryPoint implements ModInitializer
{

    @Override
    public void onInitialize()
    {
        Syncmatica.preInit();
    }
}
