## HotSwapHelper 基于HotSwapAgent热更新的Intellij插件，修改代码后无需重启服务器
### 完美兼容国内常见的springboot+mybatis+mybatisplus等项目的热加载,完美兼容若依，ruoyi vue pro,jeecg等项目

[![Jetbrains Plugins](https://img.shields.io/jetbrains/plugin/v/25171-a8translate.svg)][plugin]
[![加入QQ群](https://img.shields.io/badge/chat-QQ群-46BC99.svg?style=flat-square)](https://qm.qq.com/q/JQKyhlt4ke)  

### 使用说明

1. https://github.com/gejun123456/HotSwapHelper/releases 根据自己的jdk版本去下载对应的jdk或者去qq群下载
2. 下载后解压，idea配置为对应的jdk
3. Intellij安装了HotSwapHelper插件后在idea的debug按钮旁边有个图标，使用Debug With HotSwap 即可热更新
4. 查看日志中是否出现org.hotswap.agent.HotswapAgent字样，出现则代表加载成功了
5. 修改代码后,需要编译一下Ctrl F9(build project),多模块的项目只用编译修改了代码的模块就行Build module.

### 支持框架

#### hotSwapAgent支持的插件都支持 包括以下
springboot,springmvc,hibernate,mybatis,mybatis-plus,log4j等等，可以参考https://github.com/HotswapProjects/HotswapAgent
目前已支持国内常见的springboot+mybatis项目


#### HotSwapAgent支持的操作  
1. 修改java方法，增减java方法字段等等，修改xml,增减xml等，唯一不支持的操作是修改类继承关系比如改变父类或者删除接口

#### Ctrl+F9速度很慢  
1. 修改一个文件可以用ctrl shift F9 修改单个module的话在Build菜单 Build Module或者文件夹上build module
2. 不要使用Rebuild module 或者 Rebuild project 会导致去加载所有的类

#### 建议给Build module加一个快捷键 比如ctrl alt shift F9，使用起来更方便  
#### 多模块项目 ctrl F9编译后导致很多类 reload导致程序或者热加载失败  
如果你没有修改resource文件比如xml或者properties,yml等,但是target目录里的文件夹时间更新了，比如依赖的module的target文件夹里面的目录  
这个问题是Intellij第二次ctrl F9去更新了target目录的resource文件导致的。
解决方法: 
1. 在maven或gradle clean后请用ctrl F9编译项目两次，然后再启动项目。
2. 可以使用Build module编译单个module。
3. 如果还有问题，可以查看intellij的版本是否是2023的版本，升级到2024版本。  
4. 禁用掉springboot插件    
5. 其他问题请联系我来看看  
#### 修改了xml后用ctrl shift F9报错了 File Not Found 
可以使用ctrl F9或者在xml的文件夹上用ctrl Shift F9或者build module

#### 支持的框架的版本

参考文章：https://github.com/HotswapProjects/HotswapAgent?tab=readme-ov-file#java-frameworks-plugins  
mybatis最好在3.5.0以上  
mybatisplus需要3.2.0以上  
pageHelper正在兼容中 插件1.0.3版本已兼容  
dynamic-datasource最好在3.6.1以上的版本，低于该版本看启动是否会报错    
dubbo正在兼容中 

#### mybatisplus修改xml后分页sql有问题
1. 升级插件到最新版本1.0.4 qq群文件有

#### dynamic datasource加了注解后不生效
1. 升级插件到最新版本1.0.4 qq群文件有

### 本地测试已通过的一些开源项目
项目名称  | 地址 | 支持的地方 |更多说明
-----   |---| -----| -----
若依 | https://github.com/yangzongzhuan/RuoYi  | 支持mybatis xml热加载 java热加载
RuoYi-Vue-Plus | https://gitee.com/dromara/RuoYi-Vue-Plus | 支持mybatis xml热加载 java热加载
jeecg |https://github.com/jeecgboot/JeecgBoot | 支持mybatis xml热加载 java热加载
mall |https://gitee.com/macrozheng/mall| 支持mybatis xml热加载 java热加载
ruoyi-vue-pro|https://gitee.com/zhijiantianya/ruoyi-vue-pro|支持mybatis xml热加载 java热加载
ruoyi_flowable|https://gitee.com/tony2y/RuoYi-flowable.git|支持mybatis xml热加载 java热加载


### 和其他方案对比

名称 | 是否收费   | 优点                   |  缺点 
-----   |--------|----------------------| -----
Jrebel| 是 收费很贵 | 兼容框架较多 兼容一些老版本的框架    | 测试不兼容mybatis实体类加减字段,不支持部分spring aop
HotSwapAgent| 免费开源   | 开源可自己修改代码兼容一些框架，目前已完美兼容常见的springboot+mybatis+mybatisplus等项目 | 部分框架还未兼容 
Spring dev tool | 免费开源   | 依赖springboot,使用重启技术，兼容性好 | 使用重启技术 对于大点的项目速度较慢  


### 常见问题

#### 使用run修改java代码不生效
需要使用Debug with hotSwap

#### 编译代码后没反应 日志中没有错误也没有新日志

1. 确保intellij配置 `Build, Execution, Deployment/Debugger/HotSwap/Reload class after compilation 为Always.
2. 使用Debug with HotSwapAgent启动

#### maven热加载失败 程序直接退出了

1. 确保intellij配置 `Build, Execution, Deployment/Build tools/Maven/Runner/Delegate IDE build to maven 不要勾选  

#### spring getBean找不到类  
1. 如果这个bean使用了接口，请使用接口来找或者用bean的名字来查找bean  


#### 升级到IDEA 2024.3版本出现NoClassFoundException或其他报错
1. 升级下插件 如果还不行则配置设置"Build, Execution, Deployment" → "Debugger" → "Async Stack Traces" 关闭 "Instrumenting agent"选项

#### 修改类后A fatal error has been detected by the Java Runtime Environment:
1. 一般是jdk8的问题，可以用jdk11看看，有问题也可联系我来看看  



#### Intellij新ui 按钮看不到 怎么拖出来  

1.在IDEA debug 按钮旁边空白的地方鼠标右键 有个 customize Toolbar -> Add Actions -> 搜索Hotswap 然后添加到General Actions中  


#### java17启动报错 java.nio.channels.ReadableByteChannel sun.nio.ch.ChannelInputStream.ch accessible: module java.base does not "opens sun.nio.ch" to unnamed module @8297b3a

使用插件新版本即
#### java.lang.NoClassDefFoundError: org/hotswap/agent/config/PluginManager
Edit Configuration -> Shorten Command Line -> Jar manifest

#### 为什么需要下载一个 jdk？
[HotSwapHelper Releases](https://github.com/gejun123456/HotSwapHelper/releases)这里提供的 jdk 包含了dcevm 和hotswap 文件夹，统一在一个位置，
dcevm用来支持更好的热加载功能，比如类加减字段和方法等。
无需自己再去安装dcevm或者根据jdk版本去查对应的jdk加入agent文件等，方便用户使用。
如果您不喜欢这种方式，您可以根据文档[HotSwapAgent GitHub page](https://github.com/HotswapProjects/HotswapAgent)自行安装

#### 其他问题也可以查看HotSwapAgent 看看是否支持或者去提交issue https://github.com/HotswapProjects/HotswapAgent

### 碰到问题
可以联系我,加入qq群: [![加入QQ群](https://img.shields.io/badge/chat-QQ群-46BC99.svg?style=flat-square)](https://qm.qq.com/q/JQKyhlt4ke)

或者发邮件给:gejun123456@gmail.com


[plugin]: https://plugins.jetbrains.com/plugin/25171



