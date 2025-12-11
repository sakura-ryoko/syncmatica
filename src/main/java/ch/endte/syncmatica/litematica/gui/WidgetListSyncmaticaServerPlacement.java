package ch.endte.syncmatica.litematica.gui;


import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.data.ServerPosition;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.litematica.schematic.Schema;
import ch.endte.syncmatica.litematica.schematic.SchematicSchema;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.litematica.gui.Icons;


public class WidgetListSyncmaticaServerPlacement extends WidgetListBase<ServerPlacement, WidgetSyncmaticaServerPlacementEntry>
{

    private final int infoWidth;
    private final int infoHeight;
    private final GuiSyncmaticaServerPlacementList parent;

    public WidgetListSyncmaticaServerPlacement(final int x, final int y, final int width, final int height, final GuiSyncmaticaServerPlacementList parent,
                                               final ISelectionListener<ServerPlacement> selectionListener)
    {
        super(x, y, width, height, selectionListener);
        browserEntryHeight = 22;
        infoWidth = 170;
        infoHeight = 290;
        widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, Icons.FILE_ICON_SEARCH, LeftRight.LEFT);
        browserEntriesOffsetY = widgetSearchBar.getHeight() + 3;
        this.parent = parent;
        setSize(width, height);
        ScreenHelper.ifPresent(s -> s.setCurrentGui(parent));
    }

    @Override
    public void setSize(final int width, final int height)
    {
        super.setSize(width, height);

        browserWidth = getBrowserWidthForTotalWidth(width);
        browserEntryWidth = browserWidth - 14;
    }


    protected int getBrowserWidthForTotalWidth(final int width)
    {
        return width - 6 - infoWidth;
    }

    // source: WidgetFileBrowserBase
    @Override
    public void drawContents(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        // Draw an outline around the entire widget
        RenderUtils.drawOutlinedBox(ctx, posX, posY, browserWidth, browserHeight, 0xB0000000, GuiBase.COLOR_HORIZONTAL_BAR);

        super.drawContents(ctx, mouseX, mouseY, partialTicks);
        this.drawPlacementInfo(ctx, getLastSelectedEntry());
    }

    private void drawPlacementInfo(final GuiContext ctx, final ServerPlacement placement)
    {
        int x = posX + totalWidth - infoWidth;
        int y = posY;
        final int height = Math.min(infoHeight, parent.getMaxInfoHeight());

        RenderUtils.drawOutlinedBox(ctx, x, y, infoWidth, height, 0xA0000000, GuiBase.COLOR_HORIZONTAL_BAR);

        if (placement == null)
        {
            return;
        }

        x += 3;
        y += 3;
        final int textColor = 0xC0C0C0C0;
        final int valueColor = 0xFFFFFFFF;

        String str = StringUtils.translate("syncmatica.gui.label.placement_info.display_name");
        this.drawString(ctx, str, x, y, textColor);
        y += 12;
        this.drawString(ctx, placement.getName(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.file_name");
        this.drawString(ctx, str, x, y, textColor);
        y += 12;
        this.drawString(ctx, placement.getFile().getFileName().toString(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.dimension_id");
        this.drawString(ctx, str, x, y, textColor);
        y += 12;
        this.drawString(ctx, placement.getDimension(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.position");
        this.drawString(ctx, str, x, y, textColor);
        y += 12;
        final BlockPos origin = placement.getPosition();
        final String tmp = String.format("%d %d %d", origin.getX(), origin.getY(), origin.getZ());
        this.drawString(ctx, tmp, x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.owner");
        this.drawString(ctx, str, x, y, textColor);
        y += 12;
        this.drawString(ctx, placement.getOwner().getName(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.last_modified");
        this.drawString(ctx, str, x, y, textColor);
        y += 12;
        this.drawString(ctx, placement.getLastModifiedBy().getName(), x + 4, y, valueColor);
        y += 12;

        // Feature.VERSION
        final int litematic = placement.getLitematicVersion();
        final int dataVersion = placement.getDataVersion();

        if (litematic > -1 && dataVersion > -1)
        {
            final SchematicSchema version = new SchematicSchema(litematic, dataVersion);
            final Schema schema = Schema.getSchemaByDataVersion(dataVersion);

            str = StringUtils.translate("syncmatica.gui.label.placement_info.version", version.litematicVersion());
            this.drawString(ctx, str, x, y, textColor);
            y += 12;

            if (schema != null)
            {
                str = StringUtils.translate("syncmatica.gui.label.placement_info.schema", schema.getString(), version.minecraftDataVersion());
                this.drawString(ctx, str, x, y, textColor);
                //y += 12;
            }
        }
    }

    @Override
    protected List<String> getEntryStringsForFilter(final ServerPlacement entry)
    {
        final String metaName = entry.getName().toLowerCase();
        return ImmutableList.of(metaName);
    }

    @Override
    protected WidgetSyncmaticaServerPlacementEntry createListEntryWidget(final int x, final int y, final int listIndex, final boolean isOdd, final ServerPlacement entry)
    {
        return new WidgetSyncmaticaServerPlacementEntry(x, y, browserEntryWidth, getBrowserEntryHeightFor(entry), entry, listIndex);
    }

    @Override
    protected Collection<ServerPlacement> getAllEntries()
    {
        final ServerPosition playerPosition = LitematicManager.getInstance().getPlayerPosition();
        final Collection<ServerPlacement> serverPlacements = LitematicManager.getInstance().getActiveContext().getSyncmaticManager().getAll();
        return serverPlacements.stream().sorted(new PlayerDistanceComparator(playerPosition)).collect(Collectors.toList());
    }

    public static class PlayerDistanceComparator implements Comparator<ServerPlacement>
    {
        // should have probably turned this into multiple comparators rather than one big thing

        private final String playerDimension;
        private final BlockPos playerPosition;
        private final BlockPos playerPositionOverworld;
        private final BlockPos playerPositionNether;

        PlayerDistanceComparator(final ServerPosition playerPosition)
        {
            this.playerPosition = playerPosition.getBlockPosition();
            playerDimension = playerPosition.getDimensionId();

            if (playerPosition.getDimensionId().equals(ServerPosition.OVERWORLD_DIMENSION_ID))
            {
                playerPositionNether = new BlockPos(
                        this.playerPosition.getX() << 3,
                        this.playerPosition.getY() << 3,
                        this.playerPosition.getZ() << 3
                );
            }
            else
            {
                playerPositionNether = this.playerPosition;
            }
            if (playerPosition.getDimensionId().equals(ServerPosition.NETHER_DIMENSION_ID))
            {
                playerPositionOverworld = new BlockPos(
                        this.playerPosition.getX() >> 3,
                        this.playerPosition.getY() >> 3,
                        this.playerPosition.getZ() >> 3
                );
            }
            else
            {
                playerPositionOverworld = this.playerPosition;
            }
        }

        @Override
        public int compare(final ServerPlacement serverPlacement1, final ServerPlacement serverPlacement2)
        {
            final String dimension1 = serverPlacement1.getDimension();
            final String dimension2 = serverPlacement2.getDimension();

            final boolean equalDimension1 = compareDimensions(dimension1, playerDimension);
            final boolean equalDimension2 = compareDimensions(dimension2, playerDimension);

            if (equalDimension1 ^ equalDimension2)
            {
                return equalDimension1 ? -1 : 1;
            }

            final boolean linkedDimensions1 = areInLinkedDimensions(dimension1, playerDimension);
            final boolean linkedDimensions2 = areInLinkedDimensions(dimension2, playerDimension);

            if (linkedDimensions1 ^ linkedDimensions2)
            {
                return linkedDimensions1 ? -1 : 1;
            }

            return Double.compare(
                    getDimensionDistanceSquared(serverPlacement1.getOrigin()),
                    getDimensionDistanceSquared(serverPlacement2.getOrigin())
            );
        }

        private double getDimensionDistanceSquared(final ServerPosition position)
        {
            if (position.getDimensionId().equals(ServerPosition.OVERWORLD_DIMENSION_ID))
            {
                return SyncmaticaUtil.getBlockDistanceSquared(
                        position.getBlockPosition(),
                        playerPositionOverworld.getX(),
                        playerPositionOverworld.getY(),
                        playerPositionOverworld.getZ()
                );
            }
            if (position.getDimensionId().equals(ServerPosition.NETHER_DIMENSION_ID))
            {
                return SyncmaticaUtil.getBlockDistanceSquared(
                        position.getBlockPosition(),
                        playerPositionNether.getX(),
                        playerPositionNether.getY(),
                        playerPositionNether.getZ()
                );
            }
            return SyncmaticaUtil.getBlockDistanceSquared(
                    position.getBlockPosition(),
                    playerPosition.getX(),
                    playerPosition.getY(),
                    playerPosition.getZ()
            );
        }

        private boolean compareDimensions(final String dimensionId1, final String dimensionId2)
        {
            return dimensionId1.equals(dimensionId2);
        }

        private boolean areInLinkedDimensions(final String dimension1, final String dimension2)
        {
            return (isOverworld(dimension1) && isNether(dimension2))
                    || (isNether(dimension1) && isOverworld(dimension2));
        }

        private boolean isNether(final String dimensionId)
        {
            return dimensionId.equals(ServerPosition.NETHER_DIMENSION_ID);
        }

        private boolean isOverworld(final String dimensionId)
        {
            return dimensionId.equals(ServerPosition.OVERWORLD_DIMENSION_ID);
        }
    }
}
