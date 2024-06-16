package ch.endte.syncmatica.litematica.gui;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.data.LocalLitematicState;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.network.PacketType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.PacketByteBuf;


public class WidgetSyncmaticaServerPlacementEntry extends WidgetListEntryBase<ServerPlacement> {

    private final ServerPlacement placement;
    private final boolean isOdd;

    public WidgetSyncmaticaServerPlacementEntry(final int x, int y, final int width, final int height, final ServerPlacement entry,
                                                final int listIndex) {
        super(x, y, width, height, entry, listIndex);
        placement = entry;
        isOdd = (listIndex % 2 == 1);
        y += 1;

        int posX = x + width;
        int len;
        ButtonListener listener;
        String text;

        text = StringUtils.translate("syncmatica.gui.button.remove");
        len = getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.REMOVE, this);
        addButton(new ButtonGeneric(posX, y, len, 20, text), listener);

        text = StringUtils.translate("syncmatica.gui.button.material_gathering_placement");
        len = getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.MATERIAL_GATHERING, this);
        final ButtonGeneric matGathering = new ButtonGeneric(posX, y, len, 20, text);
        matGathering.setEnabled(false);
        addButton(matGathering, listener);

        final ArrayList<IButtonType> multi = new ArrayList<>();
        multi.add(new BaseButtonType("syncmatica.gui.button.downloading"
                , () -> LitematicManager.getInstance().getActiveContext().getCommunicationManager().getDownloadState(placement)
                , null));
        multi.add(new BaseButtonType("syncmatica.gui.button.download"
                , () -> {
            final Context con = LitematicManager.getInstance().getActiveContext();
            final LocalLitematicState state = con.getFileStorage().getLocalState(placement);
            return !state.isLocalFileReady() && state.isReadyForDownload();
        }, new ButtonListener(ButtonListener.Type.DOWNLOAD, this)));
        multi.add(new BaseButtonType("syncmatica.gui.button.load",
                () -> !LitematicManager.getInstance().isRendered(placement),
                new ButtonListener(ButtonListener.Type.LOAD, this)));
        multi.add(new BaseButtonType("syncmatica.gui.button.unload",
                () -> LitematicManager.getInstance().isRendered(placement),
                new ButtonListener(ButtonListener.Type.UNLOAD, this)));

        final ButtonGeneric button = new MultiTypeButton(posX, y, true, multi);
        addButton(button, null);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext) {
        // Source: WidgetSchematicEntry
        RenderUtils.color(1f, 1f, 1f, 1f);

        // Draw a lighter background for the hovered and the selected entry
        if (selected || isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(x, y, width, height, 0x70FFFFFF);
        } else if (isOdd) {
            RenderUtils.drawRect(x, y, width, height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else {
            RenderUtils.drawRect(x, y, width, height, 0x50FFFFFF);
        }

        final String schematicName = placement.getName();
        drawString(x + 20, y + 7, 0xFFFFFFFF, schematicName, drawContext);
        drawSubWidgets(mouseX, mouseY, drawContext);
    }

    private static class ButtonListener implements IButtonActionListener {

        Type type;
        WidgetSyncmaticaServerPlacementEntry placement;

        public ButtonListener(final Type type, final WidgetSyncmaticaServerPlacementEntry placement) {
            this.type = type;
            this.placement = placement;
        }

        @Override
        public void actionPerformedWithButton(final ButtonBase button, final int arg1) {
            if (type == null) {
                return;
            }
            button.setEnabled(false);
            type.onAction(placement);
        }

        public enum Type {
            LOAD() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    LitematicManager.getInstance().renderSyncmatic(placement.placement);
                }
            },
            UNLOAD() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    LitematicManager.getInstance().unrenderSyncmatic(placement.placement);
                }
            },
            DOWNLOAD() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    final Context con = LitematicManager.getInstance().getActiveContext();
                    final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
                    if (con.getCommunicationManager().getDownloadState(placement.placement)) {
                        return;
                    }
                    try {
                        con.getCommunicationManager().download(placement.placement, server);
                    } catch (final NoSuchAlgorithmException | IOException e) {
                        e.printStackTrace();
                    }
                }
            },
            REMOVE() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    final Context con = LitematicManager.getInstance().getActiveContext();
                    final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
                    final PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
                    packetBuf.writeUuid(placement.placement.getId());
                    server.sendPacket(PacketType.REMOVE_SYNCMATIC, packetBuf, LitematicManager.getInstance().getActiveContext());
                }
            },
            MATERIAL_GATHERING() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    Syncmatica.LOGGER.info("Opened Material Gatherings GUI - currently unsupported operation");
                }
            };

            abstract void onAction(WidgetSyncmaticaServerPlacementEntry placement);
        }

    }
}
