package com.stone.ct.common.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCUtil {
    public static Connection getConnection(){
        Connection connection = null;
        try {
            //1.读取类路径下的jdbc.properties文件
            Properties properties = new Properties();
            InputStream in = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            properties.load(in);
            String driverClass = properties.getProperty("driverClass");
            String jdbcUrl = properties.getProperty("jdbcUrl");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            //2.加载数据库驱动程序（对应的Driver实现类中有注册驱动的静态代码块）
            Class.forName(driverClass);

            //3.通过DriverManager的getConnection方法获取数据库连接
            connection = DriverManager.getConnection(jdbcUrl, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
