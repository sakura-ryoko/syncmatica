package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.gui.ButtonListenerShare;
import fi.dy.masa.litematica.gui.widgets.WidgetListSchematicPlacements;
import fi.dy.masa.litematica.gui.widgets.WidgetSchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetSchematicPlacement.class)
public abstract class MixinWidgetSchematicPlacement extends WidgetListEntryBase<SchematicPlacement> {
    @Shadow(remap = false)
    public int buttonsStartX;
    @Final
    @Shadow(remap = false)
    public SchematicPlacement placement;

    protected MixinWidgetSchematicPlacement(final int x, final int y, final int width, final int height, final SchematicPlacement entry, final int listIndex) {
        super(x, y, width, height, entry, listIndex);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void addUploadButton(final int x, final int y, final int width, final int height, final boolean isOdd,
                                final SchematicPlacement placement, final int listIndex, final WidgetListSchematicPlacements parent, final CallbackInfo ci) {
        int i = 0;
        if (LitematicManager.getInstance().isSyncmatic(placement)) {
            for (final WidgetBase base : subWidgets) {
                if (base instanceof ButtonBase) {
                    final ButtonBase button = (ButtonBase) base;
                    if (++i == 1) {
                        final IButtonActionListener oldAction = ((MixinButtonBase) button).getActionListener();
                        button.setActionListener((b, k) -> {
                            if (GuiBase.isShiftDown()) {
                                LitematicManager.getInstance().unrenderSchematicPlacement(placement);
                                return;
                            }
                            oldAction.actionPerformedWithButton(b, k);
                        });

                    }
                }
            }
        }

        final ButtonGeneric shareButton = new ButtonGeneric(buttonsStartX, y + 1, -1, true, "syncmatica.gui.button.share");
        final Context con = LitematicManager.getInstance().getActiveContext();
        final boolean buttonEnabled = con != null && con.isStarted() && !LitematicManager.getInstance().isSyncmatic(placement);
        shareButton.setEnabled(buttonEnabled);
        addButton(shareButton, new ButtonListenerShare(placement, parent.parent));
        buttonsStartX = shareButton.getX() - 1;
    }

    @Unique
    public SchematicPlacement getPlacement() {
        return placement;
    }

}
