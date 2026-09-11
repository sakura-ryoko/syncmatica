package ch.endte.syncmatica;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * This helps Syncmatica init the Channel Registrations at game launch.
 */
public class ModInit implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        if (!this.checkForLitematica())
        {
            final String msg = "Syncmatica requires MaLiLib + Litematica to be installed on your client";
            Syncmatica.LOGGER.fatal(msg);
            System.gc();
            throw new RuntimeException(msg);
        }

        Syncmatica.preInit();
    }

    private boolean checkForLitematica()
    {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            AtomicBoolean check = new AtomicBoolean(false);

            FabricLoader.getInstance().getAllMods().forEach(
                    mod ->
                    {
                        if (Objects.equals(mod.getMetadata().getId(), "litematica"))
                        {
                            check.set(true);
                        }
                    }
            );

            return check.get();
        }

        return true;
    }
}
