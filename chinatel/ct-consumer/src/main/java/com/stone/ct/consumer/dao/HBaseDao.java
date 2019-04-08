package com.stone.ct.consumer.dao;

import com.stone.ct.common.bean.BaseDao;
import com.stone.ct.common.constant.Names;
import com.stone.ct.common.constant.ValueConstant;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * HBase数据访问对象
 */
public class HBaseDao extends BaseDao {
    /**
     * 初始化
     */
    @Override
    public void init() throws IOException {
        start();
        createNamespaceNX(Names.NAMESPACE.getValue());
        createTableXX(Names.Table.getValue(), ValueConstant.REGION_COUNT,Names.CF_CALLER.getValue());
        end();
    }

    /**
     * 插入数据
     * @param value
     */
    @Override
    public void insertData(String value) throws IOException {
        //将通话日志保存到HBase表中
        //1.获取通话日志数据
        String[] values = value.split("\t");
        String call1 = values[0];
        String call2 = values[1];
        String callTime = values[2];
        String duration = values[3];

        //2.创建数据对象
        // rowKey设计
        // 1)长度原则
        //      最大值64K，推荐长度为10--100byte
        //      最好为8的倍数，能短则短，rowKey如果太长会影响性能
        // 2)唯一原则
        // 3)散列原则
        //      3-1）盐值散列，不能直接使用时间戳作为rowKey，在rowKey前增加随机数
        //      3-2）字符串反转，电话号码
        //      3-3）计算分区号
        // rowKey = regionNum + call1 + callTime + call2 + duration
        String rowKey = "";
        Put put = new Put(Bytes.toBytes(rowKey));

        byte[] family = Bytes.toBytes(Names.CF_CALLER.getValue());
        put.addColumn(family,Bytes.toBytes("call1"),Bytes.toBytes(call1));
        put.addColumn(family,Bytes.toBytes("call2"),Bytes.toBytes(call2));
        put.addColumn(family,Bytes.toBytes("callTime"),Bytes.toBytes(callTime));
        put.addColumn(family,Bytes.toBytes("duration"),Bytes.toBytes(duration));

        //3.保存数据
        putData(Names.Table.getValue(),put);

    }
}
