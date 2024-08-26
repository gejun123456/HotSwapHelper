## HotSwapHelper 基于HotSwapAgent热更新的Intellij插件

### 使用说明
#### 安装jdk

1. https://github.com/gejun123456/HotSwapHelper/releases 根据自己的jdk版本去下载对应的jdk或者去qq群下载
2. 下载后解压，idea配置为对应的jdk
3. 安装了HotSwapHelper后在idea的debug按钮旁边有两个图标，使用Debug With HotSwap 即可热更新
4. 查看日志中是否出现org.hotswap.agent.HotswapAgent字样，出现则代表加载成功了
5. 修改代码后,需要编译一下ctrl shift f9或者ctrl f9 build project即可

### 支持框架

#### hotSwapAgent支持的插件都支持 包括以下
springboot,springmvc,hibernate,mybatis,mybatis-plus,log4j等等，可以参考https://github.com/HotswapProjects/HotswapAgent
目前已支持国内常见的springboot+mybatis项目

### 本地测试已通过的一些开源项目
项目名称  | 地址 | 支持的地方 |更多说明
-----   |---| -----| -----
若依 | https://github.com/yangzongzhuan/RuoYi  | 支持mybatis xml热加载 java热加载
jeecg |https://github.com/jeecgboot/JeecgBoot | 支持mybatis xml热加载 java热加载


### 常见问题

#### java17使用run修改代码不生效
可以使用debug with hotSwap

#### java17启动报错 java.nio.channels.ReadableByteChannel sun.nio.ch.ChannelInputStream.ch accessible: module java.base does not "opens sun.nio.ch" to unnamed module @8297b3a

加入vm参数 
--add-opens java.base/sun.nio.ch=ALL-UNNAMED   
--add-opens=java.base/java.lang=ALL-UNNAMED  
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED  
--add-opens=java.base/java.io=ALL-UNNAMED  
--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED  

#### 为什么需要下载一个 jdk？
[HotSwapHelper Releases](https://github.com/gejun123456/HotSwapHelper/releases)这里提供的 jdk 包含了dcevm 和hotswap 文件夹，统一在一个位置，
dcevm用来支持更好的热加载功能，比如类加减字段和方法等。
无需自己再去安装dcevm或者根据jdk版本去查对应的jdk加入agent文件等，方便用户使用。
如果您不喜欢这种方式，您可以根据文档[HotSwapAgent GitHub page](https://github.com/HotswapProjects/HotswapAgent)自行安装

#### 其他问题也可以查看HotSwapAgent 看看是否支持或者去提交issue https://github.com/HotswapProjects/HotswapAgent

### 碰到问题
可以联系我,加入qq群: [HotSwapHelper插件交流群](https://qm.qq.com/q/JQKyhlt4ke)
或者发邮件给:gejun123456@gmail.com



