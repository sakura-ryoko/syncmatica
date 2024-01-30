package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.litematica.ScreenHelper;
import fi.dy.masa.litematica.gui.GuiSchematicPlacementsList;
import fi.dy.masa.litematica.gui.widgets.WidgetListSchematicPlacements;
import fi.dy.masa.litematica.gui.widgets.WidgetSchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(WidgetListSchematicPlacements.class)
public abstract class MixinWidgetListSchematicPlacement extends WidgetListBase<SchematicPlacement, WidgetSchematicPlacement> {

    @Unique
    Consumer<ServerPlacement> updateListener;

    public MixinWidgetListSchematicPlacement(final int x, final int y, final int width, final int height,
                                             final ISelectionListener<SchematicPlacement> selectionListener) {
        super(x, y, width, height, selectionListener);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void setupListener(final int x, final int y, final int width, final int height, final GuiSchematicPlacementsList parent, final CallbackInfo ci) {
        ScreenHelper.ifPresent((s) -> s.setCurrentGui(parent));
    }

}
