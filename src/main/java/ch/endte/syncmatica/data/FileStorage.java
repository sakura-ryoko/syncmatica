package ch.endte.syncmatica.data;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.util.SyncmaticaUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public class FileStorage implements IFileStorage
{

    private final HashMap<ServerPlacement, Long> buffer = new HashMap<>();
    private Context context = null;

    @Override
    public void setContext(final Context con)
    {
        if (context == null)
        {
            context = con;
        }
        else
        {
            throw new Context.DuplicateContextAssignmentException("Duplicate Context assignment");
        }
    }

    @Override
    public LocalLitematicState getLocalState(final ServerPlacement placement)
    {
        final Path localFile = getSchematicPath(placement);
//        if (localFile.isFile()) {
        if (Files.isRegularFile(localFile))
        {
            if (isDownloading(placement))
            {
                return LocalLitematicState.DOWNLOADING_LITEMATIC;
            }
            if ((buffer.containsKey(placement) && buffer.get(placement) == this.getLastModified(localFile)) || hashCompare(localFile, placement))
            {
                return LocalLitematicState.LOCAL_LITEMATIC_PRESENT;
            }
            return LocalLitematicState.LOCAL_LITEMATIC_DESYNC;
        }
        return LocalLitematicState.NO_LOCAL_LITEMATIC;
    }

    private long getLastModified(Path file)
    {
        if (Files.exists(file))
        {
            try
            {
                return Files.getLastModifiedTime(file).toMillis();
            }
            catch (IOException e)
            {
                Syncmatica.LOGGER.warn("Error getting last modified time of file '{}'; Exception: {}", file.getFileName().toString(), e.getLocalizedMessage());
            }
        }

        // use current time
        return System.currentTimeMillis();
    }

    private boolean isDownloading(final ServerPlacement placement)
    {
        if (context == null)
        {
            throw new RuntimeException("No CommunicationManager has been set yet - cannot get litematic state");
        }
        return context.getCommunicationManager().getDownloadState(placement);
    }

    @Override
    public Path getLocalLitematic(final ServerPlacement placement)
    {
        if (getLocalState(placement).isLocalFileReady())
        {
            return getSchematicPath(placement);
        }
        else
        {
            return null;
        }
    }

    // method for creating an empty file for the litematic data
    @Override
    public Path createLocalLitematic(final ServerPlacement placement)
    {
        if (getLocalState(placement).isLocalFileReady())
        {
            throw new IllegalArgumentException("");
        }
//        final File file = getSchematicPath(placement);
//        if (file.exists()) {
//            file.delete(); // NOSONAR
//        }
//        try {
//            file.createNewFile(); // NOSONAR
//        } catch (final IOException e) {
//            e.printStackTrace();
//        }

        final Path file = getSchematicPath(placement);

        try
        {
            if (Files.exists(file))
            {
                Files.deleteIfExists(file);
            }

            Files.createFile(file);
            return file;
        }
        catch (IOException e)
        {
            Syncmatica.LOGGER.error("Exception creating new file: '{}'; Exception: {}", file.getFileName().toString(), e.getLocalizedMessage());
        }

        return file;
    }

    private boolean hashCompare(final Path localFile, final ServerPlacement placement)
    {
        UUID hash = null;
        try
        {
            hash = SyncmaticaUtil.createChecksum(new FileInputStream(localFile.toFile()));
        }
        catch (final Exception e)
        {
            // can be safely ignored since we established that file has been found
            e.printStackTrace();
        }// wtf just exception?

        if (hash == null)
        {
            return false;
        }
        if (hash.equals(placement.getHash()))
        {
            buffer.put(placement, this.getLastModified(localFile));
            return true;
        }
        return false;
    }

    private Path getSchematicPath(final ServerPlacement placement)
    {
        final Path litematicPath = context.getLitematicFolder();

        if (context.isServer())
        {
//            return new File(litematicPath, placement.getHash().toString() + ".litematic");
            return litematicPath.resolve(placement.getHash().toString() + ".litematic");
        }

        String fileName = SyncmaticaUtil.sanitizeUnicodeFileName(placement.getNormalFileName());
        Syncmatica.debug("getSchematicPath(): Placement filename: '{}'", fileName);

        if (fileName.endsWith(".litematic"))
        {
//            return new File(litematicPath, placement.getFileName());
            return litematicPath.resolve(fileName);
        }
        else
        {
//            return new File(litematicPath, placement.getFileName() + ".litematic");
            return litematicPath.resolve(fileName+".litematic");
        }
    }
}
