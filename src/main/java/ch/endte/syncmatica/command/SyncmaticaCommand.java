package ch.endte.syncmatica.command;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.data.ServerPosition;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.litematica.schematic.SchematicMetadata;
import ch.endte.syncmatica.litematica.schematic.SchematicSchema;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class SyncmaticaCommand implements IServerCommand
{
    public static final SyncmaticaCommand INSTANCE = new SyncmaticaCommand();
    private Context context = null;
    private final HashMap<Path, Pair<SchematicMetadata, SchematicSchema>> files = new HashMap<>();
    private final PermissionLevel DEFAULT_PERMISSIONS = PermissionLevel.ALL;

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment)
    {
        dispatcher.register(
                Commands
                        .literal(Reference.MOD_ID)
                        .requires(Permissions.require(Reference.MOD_ID + ".command", DEFAULT_PERMISSIONS))
                        .then(Commands.literal("load")
                                            .requires(Permissions.require(Reference.MOD_ID + ".command.load", DEFAULT_PERMISSIONS))
                                            .executes(this::doLoadAll)
                                            .then(Commands.argument("file", StringArgumentType.string())
                                                                .suggests(
                                                                        (ctx, builder) ->
                                                                                SharedSuggestionProvider.suggest(this.files.keySet(), builder,
                                                                                                              ent -> ServerPlacement.removeExtension(ent.getFileName().toString()),
                                                                                                              ent2 -> this.formatTooltip(this.files.get(ent2).getLeft()
                                                                                                              )
                                                                                )
                                                                )
                                                                .requires(Permissions.require(Reference.MOD_ID + ".command.load_each", DEFAULT_PERMISSIONS))
                                                                .executes((ctx) ->
                                                                          {
                                                                              String result = StringArgumentType.getString(ctx, "file");
                                                                              return this.doLoadEach(ctx, result);
                                                                          })
                                            )
                        ));
    }

    public void updateSyncmaticDir(Context context)
    {
        List<Path> list = new ArrayList<>();
        this.files.clear();

        if (context == null)
        {
            return;
        }

        if (this.context == null)
        {
            this.context = context;
        }

        // Fetch all directory files.
        if (context.isServer())
        {
            Path dir = context.getLitematicFolder();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir))
            {
                for (Path file : stream)
                {
                    if (!Files.isDirectory(file))
                    {
                        list.add(file);
//                        Syncmatica.LOGGER.warn("updateSyncmaticDir(): list::add '{}'", file.toAbsolutePath().toString());
                    }
                }
            }
            catch (Exception e)
            {
                Syncmatica.LOGGER.error("updateSyncmaticDir(): Exception reading directory '{}'; {}", dir.toAbsolutePath().toString(), e.getLocalizedMessage());
            }
        }

        // List only files that are not loaded.
        list.forEach(
                (p) ->
                {
                    String name = ServerPlacement.removeExtension(p.getFileName());
                    UUID hash = UUID.fromString(name);

                    if (!context.getSyncmaticManager().hasPlacementHash(hash))
                    {
                        Pair<SchematicMetadata, SchematicSchema> pair = SyncmaticaUtil.litematicPeek(p);

                        if (pair.getLeft() != null)
                        {
                            this.files.put(p, pair);
//                            Syncmatica.LOGGER.warn("updateSyncmaticDir(): files::add '{}'", p.toAbsolutePath().toString());
                        }
                    }
                });
    }

    private Component formatTooltip(SchematicMetadata meta)
    {
        return Component.nullToEmpty(meta.getName());
    }

    private int doLoadAll(CommandContext<CommandSourceStack> ctx)
    {
        if (this.context == null)
        {
            return 0;
        }

        if (this.files.isEmpty())
        {
            this.updateSyncmaticDir(this.context);
        }

        if (this.files.isEmpty())
        {
            ctx.getSource().sendSuccess(() -> Component.nullToEmpty("No Syncmatic file(s) found that needs to be loaded."), false);
            return 0;
        }

        ServerPlayer player = ctx.getSource().getPlayer();
        PlayerIdentifier owner;
        GlobalPos globalPos;

        if (player != null)
        {
            owner = this.context.getPlayerIdentifierProvider().createOrGet(player.getGameProfile());
            globalPos = new GlobalPos(player.level().dimension(), player.blockPosition());
        }
        else
        {
            owner = this.context.getPlayerIdentifierProvider().createOrGet(UUID.randomUUID(), "Unknown");
            globalPos = new GlobalPos(Level.OVERWORLD, BlockPos.ZERO);
        }

        AtomicInteger count = new AtomicInteger();
        PlayerIdentifier finalOwner = owner;
        GlobalPos finalGlobalPos = globalPos;

        this.files.forEach(
                (p, pair) ->
                {
                    if (this.loadEach(ctx.getSource(), p, pair.getLeft(), pair.getRight(), finalOwner, finalGlobalPos))
                    {
                        count.getAndIncrement();
                    }
                });

        ctx.getSource().sendSuccess(() -> Component.nullToEmpty("§b" + String.format("%02d", count.get()) + "§r Syncmatic file(s) found / loaded."), true);
        this.updateSyncmaticDir(this.context);
        return 1;
    }

    private boolean loadEach(CommandSourceStack src, Path p, SchematicMetadata meta, SchematicSchema schema, PlayerIdentifier owner, GlobalPos pos)
    {
        ServerPlacement placement = new ServerPlacement(UUID.randomUUID(), p.normalize(), p.getFileName().toString(), owner);
        placement = placement.move(ServerPosition.fromGlobalPos(pos), Rotation.NONE, Mirror.NONE);
        placement = placement.setMetadata(meta);
        placement = placement.setSchema(schema);

        // Update placement to all clients
        ServerCommunicationManager comms = (ServerCommunicationManager) this.context.getCommunicationManager();
        comms.addPlacement(comms.fromExistingPlayer(src.getPlayer()), placement);
        final String name = placement.getName();
        src.sendSuccess(() -> Component.nullToEmpty("Loaded Server Placement '§d" + name + "§r'"), true);
        return true;
    }

    private int doLoadEach(CommandContext<CommandSourceStack> ctx, String result)
    {
        if (this.files.isEmpty())
        {
            this.updateSyncmaticDir(this.context);
        }

        if (this.files.isEmpty())
        {
            ctx.getSource().sendSuccess(() -> Component.nullToEmpty("No Syncmatic file(s) found that needs to be loaded."), false);
            return 0;
        }

        Path dir = this.context.getLitematicFolder();
        Path file = dir.resolve(result + ".litematic");
        Pair<SchematicMetadata, SchematicSchema> pair = SyncmaticaUtil.litematicPeek(file);
        ServerPlayer player = ctx.getSource().getPlayer();
        PlayerIdentifier owner;
        GlobalPos globalPos;

        if (player != null)
        {
            owner = this.context.getPlayerIdentifierProvider().createOrGet(player.getGameProfile());
            globalPos = new GlobalPos(player.level().dimension(), player.blockPosition());
        }
        else
        {
            owner = this.context.getPlayerIdentifierProvider().createOrGet(UUID.randomUUID(), "Unknown");
            globalPos = new GlobalPos(Level.OVERWORLD, BlockPos.ZERO);
        }

        if (this.loadEach(ctx.getSource(), file, pair.getLeft(), pair.getRight(), owner, globalPos))
        {
            ctx.getSource().sendSuccess(() -> Component.nullToEmpty("§b01§r Syncmatic file(s) found / loaded."), true);
        }

        this.updateSyncmaticDir(this.context);
        return 1;
    }
}
