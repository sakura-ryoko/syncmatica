package ch.endte.syncmatica.data;

import java.nio.file.Path;
import ch.endte.syncmatica.Context;

public interface IFileStorage {
    LocalLitematicState getLocalState(ServerPlacement placement);

    Path createLocalLitematic(ServerPlacement placement);

    Path getLocalLitematic(ServerPlacement placement);

    void setContext(Context con);
}
