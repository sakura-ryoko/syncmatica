package ch.endte.syncmatica.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.util.SyncmaticaUtil;

// pretty sure this is some kind of pattern 
// dont remember the name though
public class RedirectFileStorage implements IFileStorage
{

    private final IFileStorage fs;


    // stores the redirects
    // will associate a hash with a specific file on the disk
    // not sure how to solve storage and make everything convenient for the user
    private final Map<UUID, RedirectData> redirect = new HashMap<>();

    public RedirectFileStorage()
    {
        fs = new FileStorage();
    }

    public void addRedirect(final Path file)
    {
        final RedirectData red = new RedirectData(file);
        redirect.put(red.getHash(), red);
    }

    @Override
    public LocalLitematicState getLocalState(final ServerPlacement placement)
    {
        final UUID hashId = placement.getHash();
        if (redirect.containsKey(hashId) && hashId.equals(redirect.get(hashId).getHash()))
        {
            return LocalLitematicState.LOCAL_LITEMATIC_PRESENT;
        }
        else
        {
            return fs.getLocalState(placement);
        }
    }

    // todo
    @Override
    public Path createLocalLitematic(final ServerPlacement placement)
    {
        return fs.createLocalLitematic(placement);
    }

    @Override
    public Path getLocalLitematic(final ServerPlacement placement)
    {
        final UUID hashId = placement.getHash();
        if (redirect.containsKey(hashId))
        {
            final RedirectData red = redirect.get(hashId);
            if (red.exists() && hashId.equals(red.getHash()))
            {
                return red.redirect;
            }
            else
            {
                redirect.remove(hashId);
            }
        }
        return fs.getLocalLitematic(placement);
    }

    @Override
    public void setContext(final Context con)
    {
        fs.setContext(con);
    }

    private class RedirectData
    {
        Path redirect = null;
        UUID hash = null;
        long hashTimeStamp;

        RedirectData(Path file)
        {
            redirect = file;
            getHash();
            if (hash == null)
            {
                file = null;
            }
        }

        UUID getHash()
        {
//            if (hashTimeStamp == redirect.lastModified()) {
            if (hashTimeStamp == this.getModifiedTime())
            {
                return hash;
            }
            try
            {
                hash = SyncmaticaUtil.createChecksum(new FileInputStream(redirect.toFile()));
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                return null;
            }
//            hashTimeStamp = redirect.lastModified();
            hashTimeStamp = this.getModifiedTime();
            return hash;
        }

        long getModifiedTime()
        {
            try
            {
                return Files.getLastModifiedTime(redirect).toMillis();
            }
            catch (IOException e)
            {
                Syncmatica.LOGGER.warn("Exception getting last modified time of file [{}]",
                                       redirect.getFileName().toString());
            }

            // default to now
            return System.currentTimeMillis();
        }

        boolean exists()
        {
//            return redirect.exists() && redirect.canRead();
            return Files.exists(redirect) && Files.isReadable(redirect);
        }
    }

}
