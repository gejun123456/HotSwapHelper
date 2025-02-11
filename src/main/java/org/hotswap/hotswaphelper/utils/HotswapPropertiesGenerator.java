package org.hotswap.hotswaphelper.utils;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HotswapPropertiesGenerator {
    
    public static VirtualFile generateHotswapProperties(VirtualFile resourceDir) {
        try {
            InputStream templateStream = HotswapPropertiesGenerator.class
                    .getResourceAsStream("/template/hotswap-agent.properties");
            if (templateStream == null) {
                return null;
            }
            
            String content = IOUtils.toString(templateStream, StandardCharsets.UTF_8);
            
            VirtualFile propertiesFile = resourceDir.findChild("hotswap-agent.properties");
            if (propertiesFile == null) {
                propertiesFile = resourceDir.createChildData(null, "hotswap-agent.properties");
            }
            
            propertiesFile.setBinaryContent(content.getBytes(StandardCharsets.UTF_8));
            
            return propertiesFile;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}