## HotSwapHelper 基于HotSwapAgent热更新的Intellij插件

[![Jetbrains Plugins](https://img.shields.io/jetbrains/plugin/v/25171-a8translate.svg)][plugin]
[![加入QQ群](https://img.shields.io/badge/chat-QQ群-46BC99.svg?style=flat-square)](https://qm.qq.com/q/JQKyhlt4ke)  

### 使用说明

1. https://github.com/gejun123456/HotSwapHelper/releases 根据自己的jdk版本去下载对应的jdk或者去qq群下载
2. 下载后解压，idea配置为对应的jdk
3. 安装了HotSwapHelper后在idea的debug按钮旁边有两个图标，使用Debug With HotSwap 即可热更新
4. 查看日志中是否出现org.hotswap.agent.HotswapAgent字样，出现则代表加载成功了
5. 修改代码后,需要编译一下Ctrl shift f9或者Ctrl f9 build project即可 注意修改xml要用ctrl+F9 多模块的项目只用编译修改了代码的模块就行 Build module.

### 支持框架

#### hotSwapAgent支持的插件都支持 包括以下
springboot,springmvc,hibernate,mybatis,mybatis-plus,log4j等等，可以参考https://github.com/HotswapProjects/HotswapAgent
目前已支持国内常见的springboot+mybatis项目

#### Ctrl+F9速度很慢  
1. 对修改过的java文件ctrl shift F9即可或者选中文件夹或者module ctrl shift F9 rebuild


#### 修改了xml后用ctrl shift F9报错了 File Not Found 
可以使用ctrl F9(速度慢)或者在xml的文件夹上用ctrl Shift F9(推荐)


#### 支持的框架的版本

参考文章：https://github.com/HotswapProjects/HotswapAgent?tab=readme-ov-file#java-frameworks-plugins  
mybatis最好在3.5.0以上  
mybatisplus需要3.2.0以上  
pageHelper正在兼容中 插件1.0.3版本已兼容  
dynamic-datasource需要4.2.0以上的版本  

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


### 常见问题

#### 使用run修改java代码不生效
需要使用Debug with hotSwap

#### 编译代码后没反应 日志中没有错误也没有新日志

1. 确保intellij配置 `Build, Execution, Deployment/Debugger/HotSwap/Reload class after compilation 为Always.
2. 使用Debug with HotSwapAgent启动

#### java17启动报错 java.nio.channels.ReadableByteChannel sun.nio.ch.ChannelInputStream.ch accessible: module java.base does not "opens sun.nio.ch" to unnamed module @8297b3a

使用插件新版本即

#### java.lang.NoClassDefFoundError: org/hotswap/agent/config/PluginManage
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



