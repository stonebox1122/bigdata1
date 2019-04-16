package com.stone.ct.common.bean;

import com.stone.ct.common.api.Column;
import com.stone.ct.common.api.RowKey;
import com.stone.ct.common.api.TableRef;
import com.stone.ct.common.constant.Names;
import com.stone.ct.common.constant.ValueConstant;
import com.stone.ct.common.util.DateUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 基础数据访问对象
 */
public abstract class BaseDao {

    private ThreadLocal<Connection> connHolder = new ThreadLocal<Connection>();
    private ThreadLocal<Admin> adminHolder = new ThreadLocal<Admin>();

    protected void start() throws IOException {
        getConnection();
        getAdmin();
    }

    protected void end() throws IOException {
        Admin admin = getAdmin();
        if (admin != null){
            admin.close();
            adminHolder.remove();
        }

        Connection conn = getConnection();
        if (conn != null){
            conn.close();
            connHolder.remove();
        }
    }

    /**
     * 创建命名空间，如果已经存在，则不创建
     * @param namespace
     */
    protected void createNamespaceNX(String namespace) throws IOException {
        Admin admin = getAdmin();
        try {
            admin.getNamespaceDescriptor(namespace);
        } catch (NamespaceNotFoundException e) {
            //e.printStackTrace();
            NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();
            admin.createNamespace(namespaceDescriptor);
        }
    }

    /**
     * 创建表，如果存在，则删除再创建
     * @param name
     * @param families
     */
    protected void createTableXX(String name,String ... families) throws IOException {
        createTableXX(name,null,null,families);
    }

    protected void createTableXX(String name,Integer regionCount,String ... families) throws IOException {
        createTableXX(name,null,regionCount,families);
    }

    protected void createTableXX(String name,String coprocessorClass,Integer regionCount,String ... families) throws IOException {
        Admin admin = getAdmin();
        TableName tableName = TableName.valueOf(name);
        if(admin.tableExists(tableName)){
            deleteTable(name);
        };
        createTable(name,coprocessorClass,regionCount,families);
    }

    private void createTable(String name,String coprocessorClass,Integer regionCount,String ... families) throws IOException {
        Admin admin = getAdmin();
        TableName tableName = TableName.valueOf(name);
        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        if (families == null || families.length == 0){
            families = new String[1];
            families[0] = Names.CF_CALLER.getValue();
        }
        for (String family : families) {
            HColumnDescriptor columnDescriptor = new HColumnDescriptor(family);
            tableDescriptor.addFamily(columnDescriptor);
        }

        if (coprocessorClass != null && "".equals(coprocessorClass)){
            tableDescriptor.addCoprocessor(coprocessorClass);
        }

        //增加预分区
        if (regionCount == null || regionCount <= 1){
            admin.createTable(tableDescriptor);
        } else {
            // 分区键
            byte[][] splitKyes = genSplitKeys(regionCount);
            admin.createTable(tableDescriptor,splitKyes);
        }
    }

    /**
     * 获取查询时startrow，stoprow集合
     * @return
     */
    protected List<String[]> getStartStopRowKey(String tel,String start,String end){
        List<String[]> rowKeyss = new ArrayList<>();
        String startTime = start.substring(0,6);
        String endTime = end.substring(0,6);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(DateUtil.parse(startTime,"yyyyMM"));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(DateUtil.parse(endTime,"yyyyMM"));

        while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
            // 当前时间
            String nowTime = DateUtil.format(startCal.getTime(),"yyyyMM");
            int regionNum = genRegionNum(tel, nowTime);
            // 1_133_201803 ～1_133_201803|
            String startRow = regionNum + "_" + tel + "_" + nowTime;
            String stopRow = startRow + "|";

            String[] rowKeys = {startRow,stopRow};
            rowKeyss.add(rowKeys);

            // 月份+1
            startCal.add(Calendar.MONTH,1);
        }

        return rowKeyss;
    }

    /**
     * 计算分区号
      * @param tel
     * @param date
     * @return
     */
    protected int genRegionNum(String tel,String date){
        //取电话号码后4位
        String usercode = tel.substring(tel.length() - 4);
        //取日期前6位
        String yearMonth = date.substring(0,6);
        int userCodeHash = usercode.hashCode();
        int yearMonthHash = yearMonth.hashCode();
        //crc校验采用异或算法
        int crc = Math.abs(userCodeHash ^ yearMonthHash);
        //取模
        int regionNum = crc % ValueConstant.REGION_COUNT;
        return regionNum;
    }

    /**
     * 生成分区键
     * @param regionCount
     * @return
     */
    private byte[][] genSplitKeys(Integer regionCount) {
        int splitKeyCount = regionCount - 1;
        byte[][] bs = new byte[splitKeyCount][];
        //0|,1|,2|,3|,4|
        List<byte[]> bsList = new ArrayList<byte[]>();
        for (int i = 0; i < splitKeyCount; i++) {
            String splitKey = i + "|";
            bsList.add(Bytes.toBytes(splitKey));
        }
        //Collections.sort(bsList,new Bytes.ByteArrayComparator());
        bsList.toArray(bs);
        return bs;
    }

    /**
     * 增加对象，自动封装数据，将对象数据直接保存到HBase
     * @param obj
     * @throws Exception
     */
    protected void putData(Object obj) throws Exception{
        Class<?> clazz = obj.getClass();
        TableRef tableRef = clazz.getAnnotation(TableRef.class);
        String name = tableRef.value();

        Field[] fields = clazz.getDeclaredFields();
        String sRowKey = "";
        for (Field field : fields) {
            RowKey rowKey = field.getAnnotation(RowKey.class);
            if (rowKey != null){
                field.setAccessible(true);
                sRowKey = (String) field.get(obj);
                break;
            }
        }

        //获取表对象
        Connection connection = getConnection();
        Table table = connection.getTable(TableName.valueOf(name));
        Put put = new Put(Bytes.toBytes(sRowKey));

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null){
                String family = column.family();
                String colName = column.column();
                if (colName == null || "".equals(colName)){
                    colName = field.getName();
                }
                field.setAccessible(true);
                String value = (String) field.get(obj);
                put.addColumn(Bytes.toBytes(family), Bytes.toBytes(colName), Bytes.toBytes(value));
            }
        }

        //增加数据
        table.put(put);

        //关闭表
        table.close();
    }

    /**
     * 增加多条数据
     * @param name
     * @param puts
     */
    protected void putData(String name, List<Put> puts) throws IOException {
        //获取表对象
        Connection connection = getConnection();
        Table table = connection.getTable(TableName.valueOf(name));

        //增加数据
        table.put(puts);

        //关闭表
        table.close();
    }

    /**
     * 增加数据
     * @param name
     * @param put
     */
    protected void putData(String name, Put put) throws IOException {
        //获取表对象
        Connection connection = getConnection();
        Table table = connection.getTable(TableName.valueOf(name));

        //增加数据
        table.put(put);

        //关闭表
        table.close();
    }

    /**
     * 删除表
     * @param name
     * @throws IOException
     */
    protected void deleteTable(String name) throws IOException {
        Admin admin = getAdmin();
        TableName tableName = TableName.valueOf(name);
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
    }

    /**
     * 获取管理对象
     * @return
     * @throws IOException
     */
    protected synchronized Admin getAdmin() throws IOException {
        Admin admin = adminHolder.get();
        if (admin == null){
            admin = getConnection().getAdmin();
            adminHolder.set(admin);
        }
        return admin;
    }

    /**
     * 获取连接对象
     * @return
     * @throws IOException
     */
    protected synchronized Connection getConnection() throws IOException {
        Connection conn = connHolder.get();
        if (conn == null){
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", "172.30.60.62");
            conf.set("hbase.zookeeper.property.clientPort", "2181");
            conn = ConnectionFactory.createConnection(conf);
            connHolder.set(conn);
        }
        return conn;
    }

    public abstract void init() throws IOException;

    public abstract void insertData(String value) throws IOException;
}
