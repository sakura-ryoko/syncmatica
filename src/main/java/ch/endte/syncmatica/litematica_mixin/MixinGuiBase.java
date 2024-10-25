package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.litematica.gui.IGuiBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(GuiBase.class)
public abstract class MixinGuiBase implements IGuiBase {

    @Final
    @Shadow(remap = false)
    private List<ButtonBase> buttons;

    @Override
    public List<ButtonBase> getButtons() {
        return buttons;
    }
}
