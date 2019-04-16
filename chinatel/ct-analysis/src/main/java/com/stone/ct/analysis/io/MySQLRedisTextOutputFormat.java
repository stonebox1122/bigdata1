package com.stone.ct.analysis.io;

import com.stone.ct.common.util.JDBCUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *  MySQL数据格式化
 */
public class MySQLRedisTextOutputFormat extends OutputFormat<Text, Text> {
    protected static class MySQLRecordWriter extends RecordWriter<Text, Text> {
        private Connection connection = null;
        private Jedis jedis = null;

        public MySQLRecordWriter() {
            // 获取资源
            connection = JDBCUtil.getConnection();
            jedis = new Jedis("172.30.60.64", 6379);
        }

        @Override
        public void write(Text key, Text value) throws IOException, InterruptedException {
            // 输出数据
            String[] values = value.toString().split("_");
            String sumCall = values[0];
            String sumDuration = values[1];
            PreparedStatement ps = null;

            String[] keys = key.toString().split("_");
            String tel = keys[0];
            String date = keys[1];
            try {
                // 表名不要为call，因为call是MySQL的关键字
                String sql = "insert into calllog(telid,dateid,sumcall,sumduration) values(?,?,?,?)";
                ps = connection.prepareStatement(sql);

                ps.setInt(1, Integer.parseInt(jedis.hget("user",tel)));
                ps.setInt(2, Integer.parseInt(jedis.hget("date",date)));
                ps.setInt(3,Integer.parseInt(sumCall));
                ps.setInt(4,Integer.parseInt(sumDuration));
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null){
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
            // 释放资源
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (jedis != null){
                jedis.close();
            }
        }
    }

    @Override
    public RecordWriter<Text, Text> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new MySQLRecordWriter();
    }

    @Override
    public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {

    }

    private FileOutputCommitter committer = null;
    public static Path getOutputPath(JobContext job) {
        String name = job.getConfiguration().get("mapred.output.dir");
        return name == null ? null : new Path(name);
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        if (this.committer == null) {
            Path output = getOutputPath(context);
            this.committer = new FileOutputCommitter(output, context);
        }

        return this.committer;
    }
}
