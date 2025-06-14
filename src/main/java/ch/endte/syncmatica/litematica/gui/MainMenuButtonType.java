package ch.endte.syncmatica.litematica.gui;

import java.util.Collections;
import java.util.List;

import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.StringUtils;

public enum MainMenuButtonType implements IButtonType
{
    VIEW_SYNCMATICS("syncmatica.gui.button.view_syncmatics"),
    MATERIAL_GATHERINGS("syncmatica.gui.button.material_gatherings");

    private final String labelKey;

    MainMenuButtonType(final String labelKey)
    {
        this.labelKey = labelKey;
    }

    @Override
    public IGuiIcon getIcon()
    {
        return null;
    }

    @Override
    public String getTranslatedKey()
    {
        return StringUtils.translate(labelKey);
    }

    @Override
    public List<String> getHoverStrings()
    {
        return Collections.emptyList();
    }

    @Override
    public IButtonActionListener getButtonListener()
    {
        return null;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }
}