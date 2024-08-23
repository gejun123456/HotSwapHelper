package org.hotswap.plugin;

import com.intellij.execution.Executor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.TextWithMnemonic;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author bruce ge 2024/8/20
 */
public class HotSwapDebugExecutor extends Executor {
    @NonNls
    public static final String EXECUTOR_ID = "HotSwap Debug Executor";

    public HotSwapDebugExecutor() {
        super();
    }

    @NotNull
    public String getStartActionText() {
        return "HotSwap Debug";
    }

    @NotNull
    public String getStartActionText(@NotNull String configurationName) {
        String configName = StringUtil.isEmpty(configurationName) ? "" : " '" + shortenNameIfNeeded(configurationName) + "'";
        return TextWithMnemonic.parse("Run%s with hotSwap debug").replaceFirst("%s", configName).toString();
    }

    @NotNull
    public String getToolWindowId() {
        return "Debug";
    }

    @NotNull
    public Icon getToolWindowIcon() {
        return IconUtils.hotSwapDebugIcon;
    }

    @NotNull
    public Icon getIcon() {
        return IconUtils.hotSwapDebugIcon;
    }

    public Icon getDisabledIcon() {
        return null;
    }

    public String getDescription() {
        return "Run With HotSwap Debug";
    }

    @NotNull
    public String getActionName() {
        return EXECUTOR_ID;
    }

    @NotNull
    public String getId() {
        return EXECUTOR_ID;
    }

    public String getContextActionId() {
        return "HotSwap Debug Context id";
    }

    public String getHelpId() {
        return null;
    }

    public boolean isSupportedOnTarget() {
        return EXECUTOR_ID.equalsIgnoreCase(this.getId());
    }
}
