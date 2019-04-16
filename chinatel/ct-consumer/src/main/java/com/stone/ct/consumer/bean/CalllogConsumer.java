package com.stone.ct.consumer.bean;

import com.stone.ct.common.bean.Consumer;
import com.stone.ct.common.constant.Names;
import com.stone.ct.consumer.dao.HBaseDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * 通话日志消费者对象
 */
public class CalllogConsumer implements Consumer {
    /**
     * 消费数据
     */
    @Override
    public void consume() {
        try {
            //采集配置对象
            Properties props = new Properties();
//            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("consumer.properties"));

            //kafka集群
            props.put("bootstrap.servers", "172.30.60.62:9092");
            //消费者组id
            props.put("group.id", "test");
            //设置自动提交offset
            props.put("enable.auto.commit", "true");
            //提交延时
            props.put("auto.commit.interval.ms", "1000");
            //反序列化
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            //获取Flume采集的数据
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

            //指定主题
            consumer.subscribe(Arrays.asList(Names.Topic.getValue()));

            //HBase数据访问对象
            HBaseDao dao = new HBaseDao();
            //初始化
            dao.init();

            //消费数据
            while (true){
                ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    dao.insertData(consumerRecord.value());
                    //Calllog log = new Calllog(consumerRecord.value());
                    //dao.insertData(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源
     * @throws IOException
     */
    @Override
    public void close() throws IOException {

    }
}
