package ch.endte.syncmatica.litematica;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.data.ServerPlacement;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;

import java.util.function.Consumer;

public class ScreenHelper {

    private static ScreenHelper instance;

    private Consumer<ServerPlacement> updateListener;
    private GuiBase currentGui = null;
    private Context context;

    public static void ifPresent(final Consumer<ScreenHelper> callable) {
        if (instance != null) {
            callable.accept(instance);
        }
    }

    public static void init() {
        if (instance != null) {
            instance.detach();
        }
        instance = new ScreenHelper();
    }

    public static void close() {
        if (instance != null) {
            instance.detach();
        }
        instance = null;
    }

    private ScreenHelper() {
    }

    public void setActiveContext(final Context con) {
        detach();
        context = con;
        attach();
        updateCurrentScreen();
    }

    public void setCurrentGui(final GuiBase gui) {
        currentGui = gui;
    }

    public void addMessage(final Message.MessageType type, final String messageKey, final Object... args) {
        InfoUtils.showGuiOrInGameMessage(type, messageKey, args);
    }

    private void updateCurrentScreen() {
        if (currentGui != null) {
            currentGui.initGui();
        }
    }

    private void attach() {
        updateListener = p -> updateCurrentScreen();
        context.getSyncmaticManager().addServerPlacementConsumer(updateListener);
    }

    private void detach() {
        if (context != null) {
            context.getSyncmaticManager().removeServerPlacementConsumer(updateListener);
        }
    }
}
