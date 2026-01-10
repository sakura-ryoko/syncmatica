package ch.endte.syncmatica.litematica;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.data.RedirectFileStorage;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.data.ServerPosition;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.extended_core.SubRegionData;
import ch.endte.syncmatica.extended_core.SubRegionPlacementModification;
import ch.endte.syncmatica.litematica.schematic.FileType;
import ch.endte.syncmatica.litematica.schematic.SchematicMetadata;
import ch.endte.syncmatica.litematica.schematic.SchematicSchema;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.*;

// responsible for loading and keeping track of rendered syncmatic placements
// responsible for keeping track redirected litematic files (e.g. if the syncmatic was
// shared from this client)

public class LitematicManager
{
    private static LitematicManager instance = null;
    protected static PlacementEventHandler eventHandler = null;

    // links syncmatic to schematic if it is rendered on the client
    // specific client
    private final Map<ServerPlacement, SchematicPlacement> rendering;
    private Collection<SchematicPlacement> preLoadList = new ArrayList<>();
    private Context context;

    public static LitematicManager getInstance()
    {
        if (instance == null)
        {
            instance = new LitematicManager();

            if (eventHandler == null)
            {
                eventHandler = new PlacementEventHandler();
                SchematicPlacementEventHandler.getInstance()
                                              .registerSchematicPlacementEventListener(eventHandler,
                                                                                       List.of(SchematicPlacementEventFlag.ALL_EVENTS)
                                              );
            }
        }

        return instance;
    }

    public static void clear()
    {
        // fixme -- this get's cleared too often -- just leave it alone.
//        if (eventHandler != null)
//        {
//            eventHandler.clear();
//        }

        instance = null;
    }

    private LitematicManager()
    {
        rendering = new HashMap<>();
    }

    // sets the active context for the gui side of things
    public void setActiveContext(final Context con)
    {
        if (con.isServer())
        {
            throw new Context.ContextMismatchException("Applied server context where client context was expected");
        }
        context = con;
        ScreenHelper.ifPresent(s -> s.setActiveContext(con));
        context.getSyncmaticManager().addServerPlacementConsumer(this::checkSyncMatches);
        for (final ServerPlacement p : context.getSyncmaticManager().getAll())
        {
            checkSyncMatches(p);
        }
    }

    private void checkSyncMatches(final ServerPlacement p)
    {
        for (final SchematicPlacement sm : DataManager.getSchematicPlacementManager().getAllSchematicsPlacements())
        {
            final UUID smId = eventHandler.getServerId(sm);
            if (smId != null && smId.equals(p.getId()))
            {
                if (!rendering.containsKey(p))
                {
                    rendering.put(p, sm);
                    ((RedirectFileStorage) context.getFileStorage()).addRedirect(sm.getSchematicFile());
                    readVersionInfo(p, sm);
                }
                break;
            }
        }
    }

    public Context getActiveContext()
    {
        return context;
    }

    // 1st case syncmatic placement is present and is now enabled from GUI
    // or another source
    public void renderSyncmatic(final ServerPlacement placement)
    {
        final String dimension = Minecraft.getInstance().getCameraEntity().level().dimension().identifier().toString();
        if (!dimension.equals(placement.getDimension()))
        {
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.player_dimension_mismatch"));
            context.getSyncmaticManager().updateServerPlacement(placement);
            return;
        }
        if (rendering.containsKey(placement))
        {
            return;
        }
        final Path file = context.getFileStorage().getLocalLitematic(placement);

        final LitematicaSchematic schematic = SchematicHolder.getInstance().getOrLoad(file);

        if (schematic == null)
        {
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.failed_to_load", file.toAbsolutePath().toString()));
            return;
        }

        final BlockPos origin = placement.getPosition();

        // Feature.DISPLAY_NAME
        String displayName = placement.getName();

        if (displayName.equals(placement.getFileName()))
        {
            displayName = schematic.getMetadata().getName();
        }
        else if (displayName.isEmpty())
        {
            displayName = ServerPlacement.removeExtension(file.toString());
        }

        Syncmatica.debug("renderSyncmatic() - displayName [{}] (schem: [{}])", displayName, schematic.getMetadata().getName());

//        final SchematicPlacement litematicaPlacement = SchematicPlacement.createFor(schematic, origin, ServerPlacement.removeExtension(file.getName()), true, true);
//        final SchematicPlacement litematicaPlacement = SchematicPlacement.createFor(schematic, origin, ServerPlacement.removeExtension(file.toString()), true, true);
        final SchematicPlacement litematicaPlacement = SchematicPlacement.createFor(schematic, origin, displayName, true, true);
        // Feature.VERSION
        final ServerPlacement adjusted = readVersionInfo(placement, litematicaPlacement);

//        ((IIDContainer) litematicaPlacement).syncmatica$setServerId(placement.getId());
        eventHandler.setServerId(litematicaPlacement, placement.getId());
        if (litematicaPlacement.isLocked())
        {
            litematicaPlacement.toggleLocked();
        }
        litematicaPlacement.setRotation(placement.getRotation(), null);
        litematicaPlacement.setMirror(placement.getMirror(), null);
        transferSubregionDataToClientPlacement(placement, litematicaPlacement);
        litematicaPlacement.toggleLocked();

        rendering.put(Objects.requireNonNullElse(adjusted, placement), litematicaPlacement);
        DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, true);

        // Mark as selected if none
        if (DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement() == null)
        {
            DataManager.getSchematicPlacementManager().setSelectedSchematicPlacement(litematicaPlacement);
        }
        context.getSyncmaticManager().updateServerPlacement(placement);
    }

    // 2nd case litematic placement is present but gets turned into ServerPlacement
    // removed side effects
    public ServerPlacement syncmaticFromSchematic(final SchematicPlacement schem)
    {
        if (rendering.containsValue(schem))
        {
            // TODO: use the new ID for faster retrieval
            for (final ServerPlacement checkPlacement : rendering.keySet())
            {
                if (rendering.get(checkPlacement) == schem)
                {
                    return checkPlacement;
                }
            }
            // theoretically not a possible branch that will be taken

            return null;
        }
        try
        {
            final Path placementFile = schem.getSchematicFile();
            if (placementFile == null)
            {
                return null;
            }
            final FileType fileType = FileType.fromFile(placementFile);
            if (fileType == FileType.VANILLA_STRUCTURE || fileType == FileType.SCHEMATICA_SCHEMATIC)
            {
                ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.share_incompatible_schematic"));
                return null;
            }
            else if (fileType != FileType.LITEMATICA_SCHEMATIC)
            {
                ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.invalid_file"));
                return null;
            }

            final PlayerIdentifier owner = context.getPlayerIdentifierProvider().createOrGet(
                    Minecraft.getInstance().getUser().getProfileId(),
                    Minecraft.getInstance().getUser().getName()
            );

            final ServerPlacement placement = new ServerPlacement(UUID.randomUUID(), placementFile, schem.getName(), owner);
            // thanks miniHUD
            final String dimension = Minecraft.getInstance().getCameraEntity().level().dimension().identifier().toString();
            placement.move(dimension, schem.getOrigin(), schem.getRotation(), schem.getMirror());
            transferSubregionDataToServerPlacement(schem, placement);

            // Feature.VERSION
            final ServerPlacement adjusted = readVersionInfo(placement, schem);
            return Objects.requireNonNullElse(adjusted, placement);
        }
        catch (final Exception e)
        {
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.ERROR, "syncmatica.error.create_from_schematic", e.getMessage()));
        }
        return null;
    }

    private void transferSubregionDataToServerPlacement(final SchematicPlacement schem, final ServerPlacement placement)
    {
        final Collection<SubRegionPlacement> subLitematica = schem.getAllSubRegionsPlacements();
        final Map<String, BlockPos> defaultPositionMap = schem.getSchematic().getAreaPositions();
        final SubRegionData subRegionData = placement.getSubRegionData();
        subRegionData.reset();
        for (final SubRegionPlacement subRegionPlacement : subLitematica)
        {
            final BlockPos defaultPos = defaultPositionMap.get(subRegionPlacement.getName());
            if (isSubregionModified(subRegionPlacement, defaultPos))
            {
                subRegionData.modify(
                        subRegionPlacement.getName(),
                        subRegionPlacement.getPos(),
                        subRegionPlacement.getRotation(),
                        subRegionPlacement.getMirror()
                );
            }
        }
    }

    private void transferSubregionDataToClientPlacement(final ServerPlacement placement, final SchematicPlacement schem)
    {
        final Collection<SubRegionPlacement> subLitematica = schem.getAllSubRegionsPlacements();
        final Map<String, BlockPos> defaultPositionMap = schem.getSchematic().getAreaPositions();
        final Map<String, SubRegionPlacementModification> modificationData = placement.getSubRegionData().getModificationData();
        for (final SubRegionPlacement subRegionPlacement : subLitematica)
        {
            final SubRegionPlacementModification modification = modificationData != null ?
                                                                modificationData.get(subRegionPlacement.getName()) :
                                                                null;
            final BlockPos defaultPos = defaultPositionMap.get(subRegionPlacement.getName());
//            final SchematicPlacementManager manager = DataManager.getSchematicPlacementManager();
//            final MixinSchematicPlacementManager mixinManager = (MixinSchematicPlacementManager) manager;
//            final MixinSubregionPlacement mutable = (MixinSubregionPlacement) subRegionPlacement;
            if (modification != null)
            {
                SchematicPlacementEventHandler.getInstance().invokePrePlacementChange(eventHandler, schem);
                SchematicPlacementEventHandler.getInstance().invokeSetSubRegionOrigin(eventHandler, subRegionPlacement, modification.position);
                SchematicPlacementEventHandler.getInstance().invokeSetSubRegionRotation(eventHandler, subRegionPlacement, modification.rotation);
                SchematicPlacementEventHandler.getInstance().invokeSetSubRegionMirror(eventHandler, subRegionPlacement, modification.mirror);
//                mixinManager.preSubregionChange(schem);
//                mutable.setBlockPosition(modification.position);
//                mutable.setBlockRotation(modification.rotation);
//                mutable.setBlockMirror(modification.mirror);
//                ((MovingFinisher) schem).onFinishedMoving(subRegionPlacement.getName(), manager);
                SchematicPlacementEventHandler.getInstance().invokeSubRegionModified(eventHandler, schem, subRegionPlacement.getName());
            }
            else
            {
                if (isSubregionModified(subRegionPlacement, defaultPos))
                {
//                    mixinManager.preSubregionChange(schem);
                    SchematicPlacementEventHandler.getInstance().invokePrePlacementChange(eventHandler, schem);
                    resetSubRegion(subRegionPlacement, defaultPos);
//                    ((MovingFinisher) schem).onFinishedMoving(subRegionPlacement.getName(), manager);
                    SchematicPlacementEventHandler.getInstance().invokeSubRegionModified(eventHandler, schem, subRegionPlacement.getName());
                }
            }
        }
    }

    public SchematicPlacement schematicFromSyncmatic(final ServerPlacement p)
    {
        return rendering.get(p);
    }

    // 3rd case litematic placement is loaded from file at startup or because the syncmatic got created from
    // it on this client
    // and the server gives confirmation that the schematic exists
    public void renderSyncmatic(final ServerPlacement placement, final SchematicPlacement litematicaPlacement, final boolean addToRendering)
    {
        if (rendering.containsKey(placement))
        {
            return;
        }
//        final IIDContainer modPlacement = (IIDContainer) litematicaPlacement;
//        if (modPlacement.syncmatica$getServerId() != null && !modPlacement.syncmatica$getServerId().equals(placement.getId()))

        final UUID serverId = eventHandler.getServerId(litematicaPlacement);

        if (serverId != null && !serverId.equals(placement.getId()))
        {
            return;
        }
        // Feature.VERSION
        final ServerPlacement adjusted = readVersionInfo(placement, litematicaPlacement);
//        modPlacement.syncmatica$setServerId(placement.getId());

        eventHandler.setServerId(litematicaPlacement, placement.getId());

        if (litematicaPlacement.isLocked())
        {
            litematicaPlacement.toggleLocked();
        }
        litematicaPlacement.setOrigin(placement.getPosition(), null);
        litematicaPlacement.setRotation(placement.getRotation(), null);
        litematicaPlacement.setMirror(placement.getMirror(), null);
        transferSubregionDataToClientPlacement(placement, litematicaPlacement);
        litematicaPlacement.toggleLocked();

        rendering.put(Objects.requireNonNullElse(adjusted, placement), litematicaPlacement);

        context.getSyncmaticManager().updateServerPlacement(placement);

        if (addToRendering)
        {
            DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, false);

            // Set as selected if none are
            if (DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement() == null)
            {
                DataManager.getSchematicPlacementManager().setSelectedSchematicPlacement(litematicaPlacement);
            }
        }
    }

    public void unrenderSyncmatic(final ServerPlacement placement)
    {
        if (!isRendered(placement))
        {
            return;
        }
        DataManager.getSchematicPlacementManager().removeSchematicPlacement(rendering.get(placement));
        rendering.remove(placement);
        context.getSyncmaticManager().updateServerPlacement(placement);
    }

    public void updateRendered(final ServerPlacement placement)
    {
        if (!isRendered(placement))
        {
            return;
        }
        final SchematicPlacement litematicaPlacement = rendering.get(placement);
        final boolean wasLocked = litematicaPlacement.isLocked();
        if (wasLocked)
        {
            litematicaPlacement.toggleLocked();
        }
        litematicaPlacement.setOrigin(placement.getPosition(), null);
        litematicaPlacement.setRotation(placement.getRotation(), null);
        litematicaPlacement.setMirror(placement.getMirror(), null);
        transferSubregionDataToClientPlacement(placement, litematicaPlacement);
        if (wasLocked)
        {
            litematicaPlacement.toggleLocked();
        }
    }

    public void updateServerPlacement(final SchematicPlacement placement, final ServerPlacement serverPlacement)
    {
        serverPlacement.move(
                serverPlacement.getDimension(), // dimension never changes
                placement.getOrigin(),
                placement.getRotation(),
                placement.getMirror()
        );
        transferSubregionDataToServerPlacement(placement, serverPlacement);
    }

    public boolean isRendered(final ServerPlacement placement)
    {
        return rendering.containsKey(placement);
    }

    public boolean isSyncmatic(final SchematicPlacement schem)
    {
        return rendering.containsValue(schem);
    }

    public @Nullable ServerPlacement getRenderingSyncmatic(final SchematicPlacement schem)
    {
        AtomicReference<ServerPlacement> temp = new AtomicReference<>();

        this.rendering.forEach(
                (placement, schematicPlacement) ->
                {
                    if (schematicPlacement.equals(schem))
                    {
                        temp.set(placement);
                    }
                }
        );

        return temp.get();
    }

    public boolean isSubregionModified(final SubRegionPlacement subRegionPlacement, final BlockPos defaultPos)
    {
        return subRegionPlacement.getMirror() != Mirror.NONE || subRegionPlacement.getRotation() != Rotation.NONE ||
                !subRegionPlacement.getPos().equals(defaultPos);
    }

    public void resetSubRegion(final SubRegionPlacement subRegionPlacement, final BlockPos defaultPos)
    {
//        final MixinSubregionPlacement mutable = (MixinSubregionPlacement) subRegionPlacement;
//        mutable.setBlockMirror(BlockMirror.NONE);
//        mutable.setBlockRotation(BlockRotation.NONE);
//        mutable.setBlockPosition(defaultPos);
        SchematicPlacementEventHandler.getInstance().invokeSetSubRegionOrigin(eventHandler, subRegionPlacement, defaultPos);
        SchematicPlacementEventHandler.getInstance().invokeSetSubRegionRotation(eventHandler, subRegionPlacement, Rotation.NONE);
        SchematicPlacementEventHandler.getInstance().invokeSetSubRegionMirror(eventHandler, subRegionPlacement, Mirror.NONE);
    }

    // gets called by code mixed into litematicas loading stage
    // its responsible for keeping the litematics that got loaded in such a way
    // until a time where the server has told the client which syncmatics actually are still loaded
    public void preLoad(final SchematicPlacement schem)
    {
        if (context != null && context.isStarted())
        {
//            final UUID id = ((IIDContainer) schem).syncmatica$getServerId();
            final UUID id = eventHandler.getServerId(schem);
            final ServerPlacement p = context.getSyncmaticManager().getPlacement(id);
            if (p == null)
            {
                return;
            }
            if (!isRendered(p))
            {
                final ServerPlacement adjusted = readVersionInfo(p, schem);
                rendering.put(Objects.requireNonNullElse(adjusted, p), schem);
                if (context.getFileStorage().getLocalLitematic(p) != schem.getSchematicFile())
                {
                    ((RedirectFileStorage) context.getFileStorage()).addRedirect(schem.getSchematicFile());
                }
                DataManager.getSchematicPlacementManager().addSchematicPlacement(schem, false);
            }
        }
        else if (preLoadList != null)
        {
            preLoadList.add(schem);
        }
    }

    @Nullable
    private ServerPlacement readVersionInfo(ServerPlacement p, SchematicPlacement s)
    {
        try
        {
            final Path file = s.getSchematicFile();
            if (file != null)
            {
//                final File dir = new File(file.getParent());
//                final Path dir = file.getParent();

//                if (file.getName().endsWith(LitematicaSchematic.FILE_EXTENSION)) {
                if (file.toString().endsWith(LitematicaSchematic.FILE_EXTENSION))
                {
//                    final Pair<SchematicSchema, SchematicMetadata> pair = LitematicaSchematic.readMetadataAndVersionFromFile(dir, file.getName());
//                    final Pair<SchematicSchema, SchematicMetadata> pair = LitematicaSchematic.readMetadataAndVersionFromFile(dir, file.toString());
                    final Pair<SchematicMetadata, SchematicSchema> pair = SyncmaticaUtil.litematicPeek(file);

                    if (pair != null)
                    {
                        final SchematicSchema schema = pair.getRight();
                        return p.setVersion(schema.litematicVersion(), schema.minecraftDataVersion());
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }

        return null;
    }

    public void commitLoad()
    {
        final SyncmaticManager man = context.getSyncmaticManager();
        if (preLoadList != null)
        {
            for (final SchematicPlacement schem : preLoadList)
            {
//            final UUID id = ((IIDContainer) schem).syncmatica$getServerId();
                final UUID id = eventHandler.getServerId(schem);
                final ServerPlacement p = man.getPlacement(id);
                if (p != null)
                {
                    if (context.getFileStorage().getLocalLitematic(p) != schem.getSchematicFile())
                    {
                        ((RedirectFileStorage) context.getFileStorage()).addRedirect(schem.getSchematicFile());
                    }
                    renderSyncmatic(p, schem, true);
                }
            }
        }
        preLoadList = null;
    }

    public void unrenderSchematic(final LitematicaSchematic l)
    {
        rendering.entrySet().removeIf(e ->
                                      {
                                          if (e.getValue().getSchematic() == l)
                                          {
                                              context.getSyncmaticManager().updateServerPlacement(e.getKey());
                                              return true;
                                          }
                                          return false;
                                      });
    }

    public void unrenderSchematicPlacement(final SchematicPlacement placement)
    {
//        final UUID id = ((IIDContainer) placement).syncmatica$getServerId();
        final UUID id = eventHandler.getServerId(placement);
        final ServerPlacement p = context.getSyncmaticManager().getPlacement(id);
        if (p != null)
        {
            unrenderSyncmatic(p);
        }
    }

    public ServerPosition getPlayerPosition()
    {
        if (Minecraft.getInstance().getCameraEntity() != null)
        {
            final BlockPos blockPos = Minecraft.getInstance().getCameraEntity().blockPosition();
            final String dimensionId = getPlayerDimension();
            return new ServerPosition(blockPos, dimensionId);
        }
        return new ServerPosition(new BlockPos(0, 0, 0), ServerPosition.OVERWORLD_DIMENSION_ID);
    }

    public String getPlayerDimension()
    {
        if (Minecraft.getInstance().getCameraEntity() != null)
        {
            return Minecraft.getInstance().getCameraEntity().level().dimension().identifier().toString();
        }
        else
        {
            return ServerPosition.OVERWORLD_DIMENSION_ID;
        }
    }
}