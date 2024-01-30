package ch.endte.syncmatica.data;

import ch.endte.syncmatica.Context;

import java.io.File;

public interface IFileStorage {
    public LocalLitematicState getLocalState(ServerPlacement placement);

    public File createLocalLitematic(ServerPlacement placement);

    public File getLocalLitematic(ServerPlacement placement);

    public void setContext(Context con);
}
