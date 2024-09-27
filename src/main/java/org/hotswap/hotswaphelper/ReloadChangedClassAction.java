package org.hotswap.hotswaphelper;

import com.intellij.debugger.actions.HotSwapAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge 2024/9/27
 */
public class ReloadChangedClassAction extends HotSwapAction {
    public ReloadChangedClassAction(){
        super();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setIcon(IconUtils.hotSwapDebugIcon);
        e.getPresentation().setVisible(true);
        super.update(e);
    }
}
