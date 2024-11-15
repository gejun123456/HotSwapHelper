## HotSwapHelper - IntelliJ Plugin Based on HotSwapAgent for Hot Code Swapping

[![Jetbrains Plugins](https://img.shields.io/jetbrains/plugin/v/25171-a8translate.svg)][plugin]  
[中文文档](https://github.com/gejun123456/HotSwapHelper/blob/master/README_CN.md)
### Usage Instructions

1. Visit [HotSwapHelper Releases](https://github.com/gejun123456/HotSwapHelper/releases) and download the JDK corresponding to your version.
2. After downloading, extract the files and configure IntelliJ IDEA to use the corresponding JDK.
3. Once HotSwapHelper is installed, two icons will appear next to the Debug button in IDEA. Click "Debug with HotSwap" to perform hot swap.
4. Check the logs for the appearance of `org.hotswap.agent.HotswapAgent`. If it appears, the agent has been successfully loaded.
5. After change code, need recompile the file or build the project(Ctrl+Shift+F9 or Ctrl+F9), then it will automatically hot swap.

### Supported Frameworks

#### DO not use rebuild module or rebuild project it will reload all classes and slow. only rebuild file or build module or build project.

#### Compile code not work. no error no log

1. make sure intellij setting `Build, Execution, Deployment/Debugger/HotSwap/Reload class after compilation is Always.

#### All plugins supported by HotSwapAgent are also supported by HotSwapHelper, including:
Spring Boot, Spring MVC, Hibernate, MyBatis, MyBatis-Plus, Log4j, etc. For more information, visit the [HotSwapAgent GitHub page](https://github.com/HotswapProjects/HotswapAgent).


#### java.lang.NoClassDefFoundError: org/hotswap/agent/config/PluginManage
Edit Configuration -> Shorten Command Line -> Jar manifest

### Open-Source Projects Tested Locally
Project Name | URL | Supported Features | Additional Notes
-----   |---| -----| -----
RuoYi | [https://github.com/yangzongzhuan/RuoYi](https://github.com/yangzongzhuan/RuoYi)  | Supports MyBatis XML hot-swapping and Java hot-swapping |
Jeecg | [https://github.com/jeecgboot/JeecgBoot](https://github.com/jeecgboot/JeecgBoot) | Supports MyBatis XML hot-swapping and Java hot-swapping |


### Common Question?

#### Why need to download a jdk?
The jdk provided by [HotSwapHelper Releases](https://github.com/gejun123456/HotSwapHelper/releases) has package 
dcevm with hotswap folder into it, so you don't need to install dcevm separately.
If you don't like it, you can always install by yourself from doc:[HotSwapAgent GitHub page](https://github.com/HotswapProjects/HotswapAgent).

#### 2024.3 meet NoClassFound Exception
1. DownGrade to 2024.2 or older version or use java17

### Encountering Issues?
Just issue on github or contact me following:  
You can contact me by joining the QQ group: [HotSwapHelper Plugin User Group](https://qm.qq.com/q/JQKyhlt4ke)  
Or send an email to: gejun123456@gmail.com  

### ScreenShot
![runAndDebug](https://raw.githubusercontent.com/gejun123456/HotSwapHelper/master/screenShot/DebugWithHotSwap.png)
![changeCodeAndWork](https://raw.githubusercontent.com/gejun123456/HotSwapHelper/master/screenShot/HotSwapHelperChangeCodeWork.gif)

[plugin]: https://plugins.jetbrains.com/plugin/25171
