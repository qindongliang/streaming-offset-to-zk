import kafka.api.OffsetRequest
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import kafka.utils.ZKStringSerializer
import org.I0Itec.zkclient.ZkClient
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by QinDongLiang on 2017/11/28.
  */
object SparkDirectStreaming {


  val log = org.apache.log4j.LogManager.getLogger("SparkDirectStreaming")


  /***
    * 创建StreamingContext
    * @return
    */
  def createStreamingContext():StreamingContext={

    val isLocal=true//是否使用local模式
    val firstReadLastest=true//第一次启动是否从最新的开始消费

    val sparkConf=new SparkConf().setAppName("Direct Kafka Offset to Zookeeper")
    if (isLocal)  sparkConf.setMaster("local[1]") //local模式
    sparkConf.set("spark.streaming.stopGracefullyOnShutdown","true")//优雅的关闭
    sparkConf.set("spark.streaming.backpressure.enabled","true")//激活削峰功能
    sparkConf.set("spark.streaming.backpressure.initialRate","5000")//第一次读取的最大数据值
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition","2000")//每个进程每秒最多从kafka读取的数据条数



    var kafkaParams=Map[String,String]("bootstrap.servers"-> "192.168.10.6:9092,192.168.10.7:9092,192.168.10.8:9092")//创建一个kafkaParams
    if (firstReadLastest)   kafkaParams += ("auto.offset.reset"-> OffsetRequest.LargestTimeString)//从最新的开始消费
    //创建zkClient注意最后一个参数最好是ZKStringSerializer类型的，不然写进去zk里面的偏移量是乱码
    val  zkClient= new ZkClient("192.168.10.6:2181,192.168.10.7:2181,192.168.10.8:2181", 30000, 30000,ZKStringSerializer)
    val zkOffsetPath="/sparkstreaming/20171128"//zk的路径
    val topicsSet="dc_test".split(",").toSet//topic名字

    val ssc=new StreamingContext(sparkConf,Seconds(10))//创建StreamingContext,每隔多少秒一个批次

    val rdds:InputDStream[(String,String)]=createKafkaStream(ssc,kafkaParams,zkClient,zkOffsetPath,topicsSet)

    //开始处理数据
    rdds.foreachRDD( rdd=>{

      if(!rdd.isEmpty()){//只处理有数据的rdd，没有数据的直接跳过

        //迭代分区，里面的代码是运行在executor上面
        rdd.foreachPartition(partitions=>{

          //如果没有使用广播变量，连接资源就在这个地方初始化
          //比如数据库连接，hbase，elasticsearch，solr，等等


          //遍历每一个分区里面的消息
          partitions.foreach(msg=>{

             log.info("读取的数据："+msg)
            //process(msg)  //处理每条数据

          })



        })

        //更新每个批次的偏移量到zk中，注意这段代码是在driver上执行的
        KafkaOffsetManager.saveOffsets(zkClient,zkOffsetPath,rdd)
      }


    })


    ssc//返回StreamContext


  }



  def main(args: Array[String]): Unit = {

    //创建StreamingContext
    val ssc=createStreamingContext()
    //开始执行
    ssc.start()
    //等待任务终止
    ssc.awaitTermination()


  }

  /****
    *
    * @param ssc  StreamingContext
    * @param kafkaParams  配置kafka的参数
    * @param zkClient  zk连接的client
    * @param zkOffsetPath zk里面偏移量的路径
    * @param topics     需要处理的topic
    * @return   InputDStream[(String, String)] 返回输入流
    */
  def createKafkaStream(ssc: StreamingContext,
                        kafkaParams: Map[String, String],
                        zkClient: ZkClient,
                        zkOffsetPath: String,
                        topics: Set[String]): InputDStream[(String, String)]={
    //目前仅支持一个topic的偏移量处理，读取zk里面偏移量字符串
    val zkOffsetData=KafkaOffsetManager.readOffsets(zkClient,zkOffsetPath,topics.last)

    val kafkaStream = zkOffsetData match {
      case None =>  //如果从zk里面没有读到偏移量，就说明是系统第一次启动
        log.info("系统第一次启动，没有读取到偏移量，默认就最新的offset开始消费")
        //使用最新的偏移量创建DirectStream
        KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
      case Some(lastStopOffset) =>
        log.info("从zk中读取到偏移量，从上次的偏移量开始消费数据......")
        val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.key, mmd.message)
        //使用上次停止时候的偏移量创建DirectStream
        KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String, String)](ssc, kafkaParams, lastStopOffset, messageHandler)
    }
    kafkaStream//返回创建的kafkaStream
  }





}
