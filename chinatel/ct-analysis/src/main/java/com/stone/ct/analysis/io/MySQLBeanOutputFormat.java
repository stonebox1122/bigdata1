package com.stone.ct.analysis.io;

import com.stone.ct.analysis.kv.AnalysisKey;
import com.stone.ct.analysis.kv.AnalysisValue;
import com.stone.ct.common.util.JDBCUtil;
import org.apache.hadoop.fs.Path;
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
public class MySQLBeanOutputFormat extends OutputFormat<AnalysisKey, AnalysisValue> {
    protected static class MySQLRecordWriter extends RecordWriter<AnalysisKey, AnalysisValue> {
        private Connection connection = null;
        private Jedis jedis = null;

        public MySQLRecordWriter() {
            // 获取资源
            connection = JDBCUtil.getConnection();
            jedis = new Jedis("172.30.60.64", 6379);
        }

        @Override
        public void write(AnalysisKey key, AnalysisValue value) throws IOException, InterruptedException {
            // 输出数据
            PreparedStatement ps = null;
            try {
                // 表名不要为call，因为call是MySQL的关键字
                String sql = "insert into calllog(telid,dateid,sumcall,sumduration) values(?,?,?,?)";
                ps = connection.prepareStatement(sql);

                //System.out.println(jedis.hget("user",key.getTel()));
                //System.out.println(jedis.hget("date",key.getDate()));
                ps.setInt(1, Integer.parseInt(jedis.hget("user",key.getTel())));
                ps.setInt(2, Integer.parseInt(jedis.hget("date",key.getDate())));
                ps.setInt(3,Integer.parseInt(value.getSumCall()));
                ps.setInt(4,Integer.parseInt(value.getSumDuration()));
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
    public RecordWriter<AnalysisKey, AnalysisValue> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
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
