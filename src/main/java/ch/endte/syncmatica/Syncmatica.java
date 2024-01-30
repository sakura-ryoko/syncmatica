package ch.endte.syncmatica;

import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// could probably turn this into a singleton

public class Syncmatica {

    public static final String VERSION = "0.3.12";
    public static final String MOD_ID = "syncmatica";

    private static final String SERVER_PATH = "." + File.separator + "syncmatics";
    private static final String CLIENT_PATH = "." + File.separator + "schematics" + File.separator + "sync";

    public static final Identifier CLIENT_CONTEXT = new Identifier("syncmatica:client_context");
    public static final Identifier SERVER_CONTEXT = new Identifier("syncmatica:server_context");

    public static final UUID syncmaticaId = UUID.fromString("4c1b738f-56fa-4011-8273-498c972424ea");

    private static Map<Identifier, Context> contexts = null;

    public static Context initServer(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics, final boolean isIntegratedServer, final File worldPath) {
        final Context serverContext = new Context(
                fileStorage,
                comms,
                schematics,
                true,
                new File(SERVER_PATH),
                isIntegratedServer,
                worldPath
        );
        init(serverContext, SERVER_CONTEXT);
        return serverContext;
    }

    public static Context initClient(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics) {
        final Context clientContext = new Context(
                fileStorage,
                comms,
                schematics,
                new File(CLIENT_PATH)
        );
        init(clientContext, CLIENT_CONTEXT);
        return clientContext;
    }

    public static void restartClient() {
        final Context oldClient = getContext(CLIENT_CONTEXT);
        if (oldClient != null) {
            if (oldClient.isStarted()) {
                oldClient.shutdown();
            }

            contexts.remove(CLIENT_CONTEXT);
        }

        // #FIXME
        //ActorClientPlayNetworkHandler.getInstance().startClient();
    }

    public static Context getContext(final Identifier id) {
        return contexts.get(id);
    }

    private static void init(final Context con, final Identifier contextId) {
        if (contexts == null) {
            contexts = new HashMap<>();
        }
        if (!contexts.containsKey(contextId)) {
            contexts.put(contextId, con);
        }
    }

    public static void shutdown() {
        if (contexts != null) {
            for (final Context con : contexts.values()) {
                if (con.isStarted()) {
                    con.shutdown();
                }
            }
        }
        deinit();
    }

    private static void deinit() {
        contexts = null;
    }

    protected Syncmatica() {

    }

}
