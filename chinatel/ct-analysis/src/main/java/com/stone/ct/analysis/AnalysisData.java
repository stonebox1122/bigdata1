package com.stone.ct.analysis;

import com.stone.ct.analysis.tool.AnalysisBeanTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.util.ToolRunner;

/**
 * 分析数据
 */
public class AnalysisData {
    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "172.30.60.62");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        //ToolRunner.run(conf,new AnalysisTextTool(), args);
        ToolRunner.run(conf,new AnalysisBeanTool(), args);
    }
}
