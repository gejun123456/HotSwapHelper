package icons;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;

public interface PluginIcons {
   Icon ICON = IconLoader.getIcon("/icons/icon.png", PluginIcons.class);
   Icon ICON_16 = IconLoader.getIcon("/icons/icon_16.png", PluginIcons.class);
   Icon ICON_32 = IconLoader.getIcon("/icons/icon_32.png", PluginIcons.class);
   Icon ICON_64 = IconLoader.getIcon("/icons/icon_64.png", PluginIcons.class);
   Icon ICONDBUG_16 = IconLoader.getIcon("/icons/rotate.png", PluginIcons.class);
}
