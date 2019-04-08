package com.stone.ct.consumer;

import com.stone.ct.consumer.bean.CalllogConsumer;

import java.io.IOException;

/**
 * 启动消费者
 * 使用Kafka消费者获取Flume采集的数据
 * 将数据存储到HBase中去
 */
public class Bootstrap {

    public static void main(String[] args) throws IOException {
        //采集消费者
        CalllogConsumer consumer = new CalllogConsumer();

        //消费数据
        consumer.consume();

        //关闭资源
        consumer.close();
    }
}
