package com.stone.ct.analysis.tool;

import com.stone.ct.analysis.io.MySQLBeanOutputFormat;
import com.stone.ct.analysis.kv.AnalysisKey;
import com.stone.ct.analysis.kv.AnalysisValue;
import com.stone.ct.analysis.mapper.AnalysisBeanMapper;
import com.stone.ct.analysis.reducer.AnalysisBeanReducer;
import com.stone.ct.common.constant.Names;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.util.Tool;

/**
 *  分析数据的工具类
 */
public class AnalysisBeanTool implements Tool {
    private Configuration conf = null;

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(conf);
        job.setJarByClass(AnalysisBeanTool.class);

        // mapper
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(Names.CF_CALLER.getValue()));
        TableMapReduceUtil.initTableMapperJob(
                Names.Table.getValue(),
                scan,
                AnalysisBeanMapper.class,
                AnalysisKey.class,
                Text.class,
                job);

        // reducer
        job.setReducerClass(AnalysisBeanReducer.class);
        job.setOutputKeyClass(AnalysisKey.class);
        job.setOutputValueClass(AnalysisValue.class);

        // outputformat
        job.setOutputFormatClass(MySQLBeanOutputFormat.class);

        boolean flag = job.waitForCompletion(true);
        if (flag){
            return JobStatus.State.SUCCEEDED.getValue();
        } else {
            return JobStatus.State.FAILED.getValue();
        }
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
