package org.hotswap.hotswaphelper;

import com.intellij.execution.Executor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.TextWithMnemonic;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class HotSwapExecutor extends Executor {
   @NonNls
   public static final String EXECUTOR_ID = "HotSwap Executor";

   public HotSwapExecutor() {
      super();
   }

   @NotNull
   public String getStartActionText() {
      return "HotSwap";
   }

   @NotNull
   public String getStartActionText(@NotNull String configurationName) {
      String configName = StringUtil.isEmpty(configurationName) ? "" : " '" + shortenNameIfNeeded(configurationName) + "'";
      return TextWithMnemonic.parse("Run%s with hotSwap").replaceFirst("%s", configName).toString();
   }

   @NotNull
   public String getToolWindowId() {
      return "Run";
   }

   @NotNull
   public Icon getToolWindowIcon() {
      return IconUtils.hotswapIcon;
   }

   @NotNull
   public Icon getIcon() {
      return IconUtils.hotswapIcon;
   }

   public Icon getDisabledIcon() {
      return null;
   }

   public String getDescription() {
      return "Run With HotSwap";
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
      return "HotSwap Context id";
   }

   public String getHelpId() {
      return null;
   }

   public boolean isSupportedOnTarget() {
      return EXECUTOR_ID.equalsIgnoreCase(this.getId());
   }
}
