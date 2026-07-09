package ch.endte.syncmatica.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import com.google.gson.*;

public class SyncmaticManager
{
    public static final String PLACEMENTS_JSON_KEY = "placements";
    private final Map<UUID, ServerPlacement> schematics = new HashMap<>();
    private final Collection<Consumer<ServerPlacement>> consumers = new ArrayList<>();

    Context context;

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

    public void addPlacement(final ServerPlacement placement)
    {
        schematics.put(placement.getId(), placement);
        updateServerPlacement(placement);
    }

    public ServerPlacement getPlacement(final UUID id)
    {
        return schematics.get(id);
    }

    public Collection<ServerPlacement> getAll()
    {
        return schematics.values();
    }

    public boolean hasPlacementHash(UUID hash)
    {
        AtomicBoolean bool = new AtomicBoolean(false);

        this.getAll().forEach(
                (p) ->
                {
                    if (p.getHash().compareTo(hash) == 0)
                    {
                        bool.set(true);
                    }
                });

        return bool.get();
    }

    public void removePlacement(final ServerPlacement placement)
    {
        schematics.remove(placement.getId());
        updateServerPlacement(placement);
    }

    public void addServerPlacementConsumer(final Consumer<ServerPlacement> consumer)
    {
        consumers.add(consumer);
    }

    public void removeServerPlacementConsumer(final Consumer<ServerPlacement> consumer)
    {
        consumers.remove(consumer);
    }

    public void updateServerPlacement(final ServerPlacement updated)
    {
        for (final Consumer<ServerPlacement> consumer : consumers)
        {
            consumer.accept(updated);
        }

        if (context.isServer())
        {
            this.saveServer();
        }
    }

    public void startup()
    {
        if (context.isServer())
        {
            loadServer();
        }
    }

    public void shutdown()
    {
        if (context.isServer())
        {
            this.saveServer();
        }
    }

    private void saveServer()
    {
        final JsonObject obj = new JsonObject();
        final JsonArray arr = new JsonArray();

        for (final ServerPlacement p : this.getAll())
        {
            // Sanitize the FileName
            Pattern pattern = Pattern.compile("[^/\\\\]+$");
            Matcher matcher = pattern.matcher(p.getFileName());

            if (matcher.find())
            {
                String result = matcher.group();
                ServerPlacement px = p.setFileName(result);
                arr.add(px.toJson());
            }
            else
            {
                arr.add(p.toJson());
            }
        }

        obj.add(PLACEMENTS_JSON_KEY, arr);
        final Path backup = context.getConfigFolder().resolve("placements.json.bak");
        final Path incoming = context.getConfigFolder().resolve("placements.json.new");
        final Path current = context.getConfigFolder().resolve("placements.json");

        Syncmatica.debug("saveServer(): placements path: '{}'", current.toAbsolutePath().toString());

        // We still use FileWriter, etc for porting/compatibility -- for now.
        try (final FileWriter writer = new FileWriter(incoming.toFile()))
        {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        }
        catch (final IOException e)
        {
            Syncmatica.LOGGER.error("saveServer(): Exception writing incoming file '{}'; {}",
                                    incoming.getFileName().toString(), e.getLocalizedMessage());
            return;
        }

        SyncmaticaUtil.backupAndReplace(backup, current, incoming);
    }

    private void loadServer()
    {
//        final File f = new File(context.getConfigFolder(), "placements.json");
//        if (f.exists() && f.isFile() && f.canRead()) {
        final Path f = context.getConfigFolder().resolve("placements.json");
        final Path upgrade = Reference.CONFIG_ROOT.resolve(Reference.MOD_ID).normalize().resolve("placements.json");

        if (Files.exists(upgrade))
        {
            try
            {
                if (!Files.exists(f) && Files.isRegularFile(upgrade))
                {
                    Syncmatica.LOGGER.warn("loadServer(): Migrating '{}' to: '{}'", upgrade.toAbsolutePath().toString(),
                                           f.toAbsolutePath().toString());
                    Files.move(upgrade, f);
                }
                else if (Files.isRegularFile(upgrade))
                {
                    Path oldFile = context.getConfigFolder().resolve("placements.json.old");
                    Syncmatica.LOGGER.warn("loadServer(): Backing up stale '{}' to: '{}'",
                                           upgrade.toAbsolutePath().toString(), oldFile.toAbsolutePath().toString());
                    Files.move(upgrade, oldFile);
                }
            }
            catch (Exception e)
            {
                Syncmatica.LOGGER.error("loadServer(): Exception moving upgrade file '{}'; {}",
                                        upgrade.getFileName().toString(), e.getLocalizedMessage());
            }
        }

        Syncmatica.debug("loadServer(): placements path: [{}]", f.toAbsolutePath().toString());

        if (Files.exists(f) && Files.isReadable(f))
        {
            JsonElement element = null;
            try
            {
                final FileReader reader = new FileReader(f.toFile());

                element = JsonParser.parseReader(reader);
                reader.close();

            }
            catch (final Exception e)
            {
                Syncmatica.LOGGER.error("loadServer(): Exception reading file '{}'; {}", f.getFileName().toString(),
                                        e.getLocalizedMessage());
            }
            if (element == null)
            {
                return;
            }
            try
            {
                final JsonObject obj = element.getAsJsonObject();
                if (obj == null || !obj.has(PLACEMENTS_JSON_KEY))
                {
                    return;
                }
                final JsonArray arr = obj.getAsJsonArray(PLACEMENTS_JSON_KEY);
                boolean dirty = false;
                for (final JsonElement elem : arr)
                {
                    final ServerPlacement placement = ServerPlacement.fromJson(elem.getAsJsonObject(), context);

                    if (placement != null)
                    {
                        if (placement.isDirty())
                        {
                            dirty = true;
                        }

                        schematics.put(placement.getId(), placement); // NOSONAR
                    }
                }

                // Dirty flag detected; re-save placements.json
                if (dirty)
                {
                    Syncmatica.LOGGER.warn("loadServer(): Found a dirty placements.json; re-saving with corrections.");
                    this.saveServer();
                }
            }
            catch (final IllegalStateException | NullPointerException e)
            {
                Syncmatica.LOGGER.error("loadServer(): Exception loading server placement; {}",
                                        e.getLocalizedMessage());
            }
        }
    }
}
