package com.stone.ct.consumer.coprocessor;

import com.stone.ct.common.bean.BaseDao;
import com.stone.ct.common.constant.Names;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 *  使用协处理器保存被叫用户的数据
 *   1.创建类
 *   2.让表知道协处理器类（和表有关联）
 *   3.生成Jar包，包括关联的Jar包，放到集群中所有HBase的lib目录下，CDH的目录为：/opt/cloudera/parcels/CDH-5.15.1-1.cdh5.15.1.p0.4/lib/hbase/lib
 *   4.修改配置文件属性：hbase.coprocessor.region.classes 增加：com.stone.ct.consumer.coprocessor.InsertCalleeCoprocessor
 *   4.重启HBase
 */
public class InsertCalleeCoprocessor extends BaseRegionObserver {

    /**
     *  保存主叫用户数据之后，由HBase自动保存被叫用户数据
     * @param e
     * @param put
     * @param edit
     * @param durability
     * @throws IOException
     */
    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        // 获取表
        Table table = e.getEnvironment().getTable(TableName.valueOf(Names.Table.getValue()));

        // 主叫用户的rowKey
        String rowKey = Bytes.toString(put.getRow());
        // 5_1841754692_20180510145528_1839130195_02213_1
        String[] values = rowKey.split("_");
        String flag = values[5];

        if ("1".equals(flag)){
            // 只有主叫用户保存后才需要触发被叫用户的保存
            String call1 = values[1];
            String call2 = values[3];
            String callTime = values[2];
            String duration = values[4];
            CoprocessorDao dao = new CoprocessorDao();
            String calleeRowKey = dao.getRegionNum(call2,callTime) + "_" + call2 + "_" + callTime + "_" + call1 + "_" + duration + "_0";;

            // 保存数据
            Put calleePut = new Put(Bytes.toBytes(calleeRowKey));
            byte[] calleeFamily = Bytes.toBytes(Names.CF_CALLEE.getValue());
            calleePut.addColumn(calleeFamily,Bytes.toBytes("call1"),Bytes.toBytes(call2));
            calleePut.addColumn(calleeFamily,Bytes.toBytes("call2"),Bytes.toBytes(call1));
            calleePut.addColumn(calleeFamily,Bytes.toBytes("callTime"),Bytes.toBytes(callTime));
            calleePut.addColumn(calleeFamily,Bytes.toBytes("duration"),Bytes.toBytes(duration));
            calleePut.addColumn(calleeFamily,Bytes.toBytes("flag"),Bytes.toBytes("0"));
            table.put(calleePut);

            // 关闭表
            table.close();
        }
    }

    private class CoprocessorDao extends BaseDao{
        public int getRegionNum(String tel,String date){
            return genRegionNum(tel,date);
        }

        @Override
        public void init() throws IOException {

        }

        @Override
        public void insertData(String value) throws IOException {

        }
    }
}
