package ch.endte.syncmatica.litematica.gui;

import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.data.ServerPlacement;
import fi.dy.masa.litematica.gui.GuiMainMenu.ButtonListenerChangeMenu;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiSyncmaticaServerPlacementList extends GuiListBase<ServerPlacement, WidgetSyncmaticaServerPlacementEntry, WidgetListSyncmaticaServerPlacement> {

	public GuiSyncmaticaServerPlacementList() {
		super(12, 30);
		title = StringUtils.translate("syncmatica.gui.title.manage_server_placements", String.format("v%s", Reference.MOD_VERSION));
	}

	@Override
	public void initGui() {
		super.initGui();
		// source GuiSchematicLoadedList
		final ButtonListenerChangeMenu.ButtonType type = ButtonListenerChangeMenu.ButtonType.MAIN_MENU;
		final String label = StringUtils.translate(type.getLabelKey());
		final int buttonWidth = getStringWidth(label) + 20;
		final int x = width - buttonWidth - 10;
		final int y = height - 26;
		final ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, label);
		addButton(button, new ButtonListenerChangeMenu(type, getParent()));
	}

	@Override
	protected WidgetListSyncmaticaServerPlacement createListWidget(final int listX, final int listY) {
		return new WidgetListSyncmaticaServerPlacement(listX, listY, getBrowserWidth(), getBrowserHeight(), this, null);
	}

	@Override
	protected int getBrowserHeight() {
		return height - 68;
	}

	@Override
	protected int getBrowserWidth() {
		return width - 20;
	}

	public int getMaxInfoHeight() {
		return getBrowserHeight();
	}

}
