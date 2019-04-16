package com.stone.ct.cache;

import com.stone.ct.common.util.JDBCUtil;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  启动缓存客户端，向redis中增加缓存数据
 */
public class Bootstrap {
    public static void main(String[] args) {
        // 取得MySQL中的数据
        Map<String,Integer> userMap = new HashMap<>();
        Map<String,Integer> dateMap = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtil.getConnection();
            String userSql = "select id,tel from user";
            ps = connection.prepareStatement(userSql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String tel = rs.getString(2);
                userMap.put(tel,id);
            }
            rs.close();

            String dateSql = "select id,year,month,day from date";
            ps = connection.prepareStatement(dateSql);
            rs =  ps.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String year = rs.getString(2);
                String month = rs.getString(3);
                if (month != null && month.length() == 1){
                    month = "0" + month;
                } else if (month == null){
                    month = "";
                }
                String day = rs.getString(4);
                if (day != null && day.length() == 1) {
                    day = "0" + day;
                } else if (day == null){
                    day = "";
                }
                dateMap.put(year+month+day,id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

//        System.out.println(userMap.size());
//        System.out.println(dateMap.size());

        // 向redis中存储数据
        Jedis jedis = new Jedis("172.30.60.64", 6379);

        Iterator<String> keyIterator = userMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Integer value = userMap.get(key);
            jedis.hset("user",key,"" + value);
        }

        keyIterator = dateMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Integer value = dateMap.get(key);
            jedis.hset("date",key,"" + value);
        }
    }
}
