package ch.endte.syncmatica.litematica_mixin;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(GuiBase.class)
public abstract class MixinGuiBase {

    @Final
    @Shadow(remap = false)
    private List<ButtonBase> buttons;

    @Unique
    public List<ButtonBase> getButtons() {
        return buttons;
    }
}
