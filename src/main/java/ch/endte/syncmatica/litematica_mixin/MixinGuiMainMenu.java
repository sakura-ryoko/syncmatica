package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.gui.ButtonListenerChangeMenu;
import ch.endte.syncmatica.litematica.gui.MainMenuButtonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.litematica.gui.GuiMainMenu;
import fi.dy.masa.litematica.selection.SelectionMode;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiBase {

    @Inject(method = "initGui", at = @At("RETURN"), remap = false)
    public void initGui(final CallbackInfo ci) {
        final int width = getButtonWidth();
        final int x = 52 + 2 * width;
        int y = 30;
        createChangeMenuButton(x, y, width, MainMenuButtonType.VIEW_SYNCMATICS);
        y += 22;
        createChangeMenuButton(x, y, width, MainMenuButtonType.MATERIAL_GATHERINGS).setEnabled(false);
    }

    @Unique
    private ButtonGeneric createChangeMenuButton(final int x, final int y, final int width, final MainMenuButtonType type) {
        final ButtonGeneric button = new ButtonGeneric(x, y, width, 20, type.getTranslatedKey(), type.getIcon());
        final Context con = LitematicManager.getInstance().getActiveContext();
        button.setEnabled(con != null && con.isStarted());
        addButton(button, new ButtonListenerChangeMenu(type, this));
        return button;
    }

    @Unique
    private int getButtonWidth() {
        int width = 0;

        for (final MainMenuButtonType type : MainMenuButtonType.values()) {
            width = Math.max(width, getStringWidth(type.getTranslatedKey()) + 30);
        }

        for (final SelectionMode mode : SelectionMode.values()) {
            final String label = StringUtils.translate("litematica.gui.button.area_selection_mode", mode.getDisplayName());
            width = Math.max(width, getStringWidth(label) + 10);
        }

        return width;
    }
}
