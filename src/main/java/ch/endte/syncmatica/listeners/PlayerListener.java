package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.interfaces.IPlayerListener;
import ch.endte.syncmatica.service.DebugService;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener implements IPlayerListener
{
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        DebugService.printDebug("PlayerManagerEvents#onPlayerJoin(): invoked.");
        //StructureDataProvider.INSTANCE.register(player);
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        DebugService.printDebug("PlayerManagerEvents#onPlayerLeave(): invoked.");
        //StructureDataProvider.INSTANCE.unregister(player);
    }
}
