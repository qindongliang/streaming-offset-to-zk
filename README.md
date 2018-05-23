# streaming-offset-to-zk
#### 项目背景
公司有一个比较核心的实时业务用的是spark streaming2.1.0+kafka0.9.0.0的流式技术来开发的，存储用的hbase+elasticsearch+redis，这中间趟过很多坑，解决了一些bug和问题，在这里我把它做成了一个骨架项目并开源出来，希望后来的朋友可以借阅和参考，尽量少走些弯路，当然如果中间遇到或者发现什么问题都可以给我提issue。

下面是使用过程中记录的一些心得和博客，感兴趣的朋友可以了解下：

（1）[spark streaming自带的checkpoint容错使用）](http://qindongliang.iteye.com/blog/2350846) 

（2）[spark streaming自带的checkpoint的弊端）](http://qindongliang.iteye.com/blog/2356634) 

（3）[如何管理Spark Streaming消费Kafka的偏移量（一））](http://qindongliang.iteye.com/blog/2399736) 

（4）[如何管理Spark Streaming消费Kafka的偏移量（二））](http://qindongliang.iteye.com/blog/2400003) 

（5）[如何管理Spark Streaming消费Kafka的偏移量（三））](http://qindongliang.iteye.com/blog/2401194) 

（6）[spark streaming程序如何优雅的停止服务（一））](http://qindongliang.iteye.com/blog/2364713) 

（7）[spark streaming程序如何优雅的停止服务（二））](http://qindongliang.iteye.com/blog/2401501) 

（8）[spark streaming程序如何优雅的停止服务（三））](http://qindongliang.iteye.com/blog/2404100) 






#### 项目简介
该项目提供了一个在使用spark streaming2.1+kafka0.9.0.0的版本集成时，手动存储偏移量到zookeeper中，因为自带的checkpoint弊端太多，不利于
项目升级发布，例子中的代码已经在我们生产环境运行，所以大家可以参考一下。



## 博客相关

（1）[微信公众号（woshigcs）：同步更新](https://github.com/qindongliang/answer_sheet_scan/blob/master/imgs/gcs.jpg)

（2）[个人站点(2018之后，同步更新）](http://8090nixi.com/) 

（3）[腾讯云社区，自动同步公众号文章](<http://qindongliang.iteye.com/>)

（4）[csdn ： (暂时同步更新)](https://blog.csdn.net/u010454030)

（5）[iteye（2018.05月之前所有的文章，之后弃用）](<http://qindongliang.iteye.com/>)  






## 我的公众号(woshigcs)

有问题可关注我的公众号留言咨询

![image](https://github.com/qindongliang/answer_sheet_scan/blob/master/imgs/gcs.jpg)
