package org.hotswap.hotswaphelper.utils;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

/** Adds the shared action to one toolbar matching the active UI. */
public final class HotSwapToolbarInstaller {
    private static final Logger LOG = Logger.getInstance(HotSwapToolbarInstaller.class);
    private static final String ACTION_ID = "org.hotswap.hotswaphelper.actions.HotSwapDebugTopAction";
    static final String NEW_TOOLBAR = "MainToolbarRight";
    static final String CLASSIC_TOOLBAR = "ToolbarRunGroup";

    private HotSwapToolbarInstaller() {
    }

    public static void install() {
        if (ApplicationManager.getApplication().isDisposed()
                || ApplicationManager.getApplication().isHeadlessEnvironment()) {
            return;
        }
        ActionManager manager = ActionManager.getInstance();
        AnAction action = manager.getAction(ACTION_ID);
        if (action == null) {
            return;
        }
        String toolbarId = selectToolbar(isNewUi(manager),
                id -> manager.getAction(id) instanceof DefaultActionGroup);
        if (toolbarId == null) {
            LOG.warn("No compatible HotSwap toolbar group found; use the Run menu action.");
            return;
        }
        DefaultActionGroup toolbar = (DefaultActionGroup) manager.getAction(toolbarId);
        if (!Arrays.asList(toolbar.getChildActionsOrStubs()).contains(action)) {
            toolbar.add(action);
        }
    }

    @Nullable
    static String selectToolbar(boolean newUi, Predicate<String> available) {
        String preferred = newUi ? NEW_TOOLBAR : CLASSIC_TOOLBAR;
        String fallback = newUi ? CLASSIC_TOOLBAR : NEW_TOOLBAR;
        return available.test(preferred) ? preferred : available.test(fallback) ? fallback : null;
    }

    private static boolean isNewUi(ActionManager manager) {
        try {
            // The build SDK predates this API. Reflection keeps classic IDEs loadable as well.
            Class<?> experimentalUi = Class.forName("com.intellij.ui.ExperimentalUI");
            return Boolean.TRUE.equals(experimentalUi.getMethod("isNewUI").invoke(null));
        } catch (ReflectiveOperationException | LinkageError e) {
            LOG.debug("UI detection API unavailable; checking registered toolbar groups.", e);
            return manager.getAction(NEW_TOOLBAR) instanceof DefaultActionGroup;
        }
    }
}
