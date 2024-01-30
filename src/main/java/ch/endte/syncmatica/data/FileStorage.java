package ch.endte.syncmatica.data;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.util.SyncmaticaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class FileStorage implements IFileStorage {

    private final HashMap<ServerPlacement, Long> buffer = new HashMap<>();
    private Context context = null;

    @Override
    public void setContext(final Context con) {
        if (context == null) {
            context = con;
        } else {
            throw new Context.DuplicateContextAssignmentException("Duplicate Context assignment");
        }
    }

    @Override
    public LocalLitematicState getLocalState(final ServerPlacement placement) {
        final File localFile = getSchematicPath(placement);
        if (localFile.isFile()) {
            if (isDownloading(placement)) {
                return LocalLitematicState.DOWNLOADING_LITEMATIC;
            }
            if ((buffer.containsKey(placement) && buffer.get(placement) == localFile.lastModified()) || hashCompare(localFile, placement)) {
                return LocalLitematicState.LOCAL_LITEMATIC_PRESENT;
            }
            return LocalLitematicState.LOCAL_LITEMATIC_DESYNC;
        }
        return LocalLitematicState.NO_LOCAL_LITEMATIC;
    }

    private boolean isDownloading(final ServerPlacement placement) {
        if (context == null) {
            throw new RuntimeException("No CommunicationManager has been set yet - cannot get litematic state");
        }
        return context.getCommunicationManager().getDownloadState(placement);
    }

    @Override
    public File getLocalLitematic(final ServerPlacement placement) {
        if (getLocalState(placement).isLocalFileReady()) {
            return getSchematicPath(placement);
        } else {
            return null;
        }
    }

    // method for creating an empty file for the litematic data
    @Override
    public File createLocalLitematic(final ServerPlacement placement) {
        if (getLocalState(placement).isLocalFileReady()) {
            throw new IllegalArgumentException("");
        }
        final File file = getSchematicPath(placement);
        if (file.exists()) {
            file.delete(); // NOSONAR
        }
        try {
            file.createNewFile(); // NOSONAR
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean hashCompare(final File localFile, final ServerPlacement placement) {
        UUID hash = null;
        try {
            hash = SyncmaticaUtil.createChecksum(new FileInputStream(localFile));
        } catch (final Exception e) {
            // can be safely ignored since we established that file has been found
            e.printStackTrace();
        }// wtf just exception?

        if (hash == null) {
            return false;
        }
        if (hash.equals(placement.getHash())) {
            buffer.put(placement, localFile.lastModified());
            return true;
        }
        return false;
    }

    private File getSchematicPath(final ServerPlacement placement) {
        final File litematicPath = context.getLitematicFolder();
        if (context.isServer()) {
            return new File(litematicPath, placement.getHash().toString() + ".litematic");
        }
        return new File(litematicPath, placement.getName() + ".litematic");
    }
}
