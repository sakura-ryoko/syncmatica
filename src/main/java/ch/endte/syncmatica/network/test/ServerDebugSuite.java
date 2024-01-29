package ch.endte.syncmatica.network.test;

import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.Set;

public class ServerDebugSuite {
    public static void checkGlobalChannels() {
        DebugService.printDebug("ServerDebugSuite#checkGlobalChannels(): Start.");
        Set<Identifier> channels = ServerPlayNetworking.getGlobalReceivers();
        Iterator<Identifier> iterator = channels.iterator();
        int i = 0;
        while (iterator.hasNext())
        {
            Identifier id = iterator.next();
            i++;
            DebugService.printDebug("ServerDebugSuite#checkGlobalChannels(): id("+i+") hash: "+id.hashCode()+" //name: "+id.getNamespace()+" path: "+id.getPath());
        }
        DebugService.printDebug("ServerDebugSuite#checkGlobalChannels(): END. Total Channels: "+i);
    }
}
