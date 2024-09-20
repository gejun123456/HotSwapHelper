package org.hotswap.hotswaphelper;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.ApplicationManager;
import org.hotswap.hotswaphelper.utils.MyUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class StartUpListener implements AppLifecycleListener {
   public StartUpListener() {
      super();
   }

   public void appFrameCreated(@NotNull List<String> commandLineArgs) {
      ApplicationManager.getApplication().executeOnPooledThread(() -> {
         try {
            File folder = MyUtils.getHotSwapFolder();
            if (!folder.exists()) {
               folder.mkdirs();
            }

            File agentFile = MyUtils.getHotSwapJarPath();
            if (agentFile.exists()) {
               agentFile.delete();
            }
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("hotswap-agent.jar");
            //copy resource to the file
            FileUtils.copyInputStreamToFile(resourceAsStream, agentFile);
            resourceAsStream.close();
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      });
   }
}
