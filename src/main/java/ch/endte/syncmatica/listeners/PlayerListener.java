package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.interfaces.IPlayerListener;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener implements IPlayerListener
{
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerJoin(): invoked.");
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerLeave(): invoked.");
    }
}
