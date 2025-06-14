package ch.endte.syncmatica.litematica.gui;

import java.util.List;

import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;

// Represents a type of button
// e.g. the onClickListener / whether it is active or not
// its text and icon

public interface IButtonType
{
    IGuiIcon getIcon();

    // returns the key when it is already translated
    // allows for the most freedom in the implementation
    String getTranslatedKey();

    List<String> getHoverStrings();

    IButtonActionListener getButtonListener();

    // returns whether a button type is active or not
    // for MultiTypeButtons this check essentially checks which type is selected
    boolean isActive();
}
