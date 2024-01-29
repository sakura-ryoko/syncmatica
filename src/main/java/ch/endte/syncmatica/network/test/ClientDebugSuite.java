package ch.endte.syncmatica.network.test;

import ch.endte.syncmatica.service.DebugService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.Set;

public class ClientDebugSuite {
    public static void checkGlobalChannels() {
        DebugService.printDebug("ClientDebugSuite#checkGlobalChannels(): Start.");
        Set<Identifier> channels = ClientPlayNetworking.getGlobalReceivers();
        Iterator<Identifier> iterator = channels.iterator();
        int i = 0;
        while (iterator.hasNext())
        {
            Identifier id = iterator.next();
            i++;
            DebugService.printDebug("ClientDebugSuite#checkGlobalChannels(): id("+i+") hash: "+id.hashCode()+" //name: "+id.getNamespace()+" path: "+id.getPath());
        }
        DebugService.printDebug("ClientDebugSuite#checkGlobalChannels(): END. Total Channels: "+i);
    }
}
