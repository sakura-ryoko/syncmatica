package ch.endte.syncmatica.litematica.gui;

import java.util.List;
import java.util.function.Supplier;

import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseButtonType implements IButtonType
{
    private final String translatedKey;
    private final IButtonActionListener listener;
    private final Supplier<Boolean> activeFunction;
    private final List<String> hoverString;

    public BaseButtonType(final String untranslatedKey, final Supplier<Boolean> activeFunction, final IButtonActionListener listener, final List<String> hoverString)
    {
        translatedKey = StringUtils.translate(untranslatedKey);
        this.listener = listener;
        this.activeFunction = activeFunction;
        this.hoverString = hoverString;
    }

    public BaseButtonType(final String untranslatedKey, final Supplier<Boolean> activeFunction, final IButtonActionListener listener)
    {
        this(untranslatedKey, activeFunction, listener, null);
    }

    @Override
    public String getTranslatedKey()
    {
        return translatedKey;
    }

    @Override
    public IButtonActionListener getButtonListener()
    {
        return listener;
    }

    @Override
    public boolean isActive()
    {
        return activeFunction.get();
    }

    @Override
    public IGuiIcon getIcon()
    {
        return null;
    }

    @Override
    public List<String> getHoverStrings()
    {
        return hoverString;
    }
}
