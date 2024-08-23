## HotSwapHelper - IntelliJ Plugin Based on HotSwapAgent for Hot Code Swapping

[![Jetbrains Plugins](https://img.shields.io/jetbrains/plugin/v/25171-a8translate.svg)][plugin]  
[中文文档](https://github.com/gejun123456/HotSwapHelper/blob/master/README_CN.md)
### Usage Instructions

1. Visit [HotSwapHelper Releases](https://github.com/gejun123456/HotSwapHelper/releases) and download the JDK corresponding to your version.
2. After downloading, extract the files and configure IntelliJ IDEA to use the corresponding JDK.
3. Once HotSwapHelper is installed, two icons will appear next to the Debug button in IDEA. Click them to perform hot code swapping.
4. Check the logs for the appearance of `org.hotswap.agent.HotswapAgent`. If it appears, the agent has been successfully loaded.
5. After change code, need recompile the file or project(Ctrl+Shift+F9 or Ctrl+F9), then it will automatically hot swap.

### Supported Frameworks

#### All plugins supported by HotSwapAgent are also supported by HotSwapHelper, including:
Spring Boot, Spring MVC, Hibernate, MyBatis, MyBatis-Plus, Log4j, etc. For more information, visit the [HotSwapAgent GitHub page](https://github.com/HotswapProjects/HotswapAgent).

Currently, the plugin supports common Spring Boot + MyBatis projects in China.

### Open-Source Projects Tested Locally
Project Name | URL | Supported Features | Additional Notes
-----   |---| -----| -----
RuoYi | [https://github.com/yangzongzhuan/RuoYi](https://github.com/yangzongzhuan/RuoYi)  | Supports MyBatis XML hot-swapping and Java hot-swapping |
Jeecg | [https://github.com/jeecgboot/JeecgBoot](https://github.com/jeecgboot/JeecgBoot) | Supports MyBatis XML hot-swapping and Java hot-swapping |

### Encountering Issues?
Just issue on github or contact me following:
You can contact me by joining the QQ group: [HotSwapHelper Plugin User Group](https://qm.qq.com/q/JQKyhlt4ke)
Or send an email to: gejun123456@gmail.com

### ScreenShot
![runAndDebug](https://raw.githubusercontent.com/gejun123456/HotSwapHelper/master/screenShot/RunAndDebugIcon.png)
![changeCodeAndWork](https://raw.githubusercontent.com/gejun123456/HotSwapHelper/master/screenShot/HotSwapHelperChangeCodeWork.gif)

[plugin]: https://plugins.jetbrains.com/plugin/25171
