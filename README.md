# streaming-offset-to-zk
#### 项目介绍
该小项目提供了一个在使用spark streaming2.1+kafka0.9.0.0的版本集成时，手动存储偏移量到zookeeper中，因为自带的checkpoint弊端太多，不利于
项目升级发布，例子中的代码已经在我们生产环境运行，所以大家可以参考一下。
