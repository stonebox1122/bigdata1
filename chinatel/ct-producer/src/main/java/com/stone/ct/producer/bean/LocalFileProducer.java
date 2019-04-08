package com.stone.ct.producer.bean;

import com.stone.ct.common.bean.DataIn;
import com.stone.ct.common.bean.DataOut;
import com.stone.ct.common.bean.Producer;
import com.stone.ct.common.util.DateUtil;
import com.stone.ct.common.util.NumberUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 本地数据文件生产者
 */
public class LocalFileProducer implements Producer {

    private DataIn in;
    private DataOut out;
    private volatile boolean flag = true;

    @Override
    public void setIn(DataIn in) {
        this.in = in;
    }

    @Override
    public void setOut(DataOut out) {
        this.out = out;
    }

    /**
     * 生产数据
     */
    @Override
    public void produce() {

        //读取通讯录数据
        try {
            List<Contact> contacts = in.read(Contact.class);

            while (flag){
                //从通讯录中随机查找2个电话号码
                int call1Index = new Random().nextInt(contacts.size());
                int call2Index;
                while (true){
                    call2Index = new Random().nextInt(contacts.size());
                    if (call1Index != call2Index){
                        break;
                    }
                }

                Contact call1 = contacts.get(call1Index);
                Contact call2 = contacts.get(call2Index);

                //生成随机的通话时间
                String startDate = "20180101000000";
                String endDate = "20190101000000";
                long startTime = DateUtil.parse(startDate, "yyyyMMddHHmmss").getTime();
                long endTime = DateUtil.parse(endDate, "yyyyMMddHHmmss").getTime();
                long callTime = (long) (startTime + (endTime - startTime) * Math.random());
                String callTimeString = DateUtil.format(new Date(callTime), "yyyyMMddHHmmss");

                //生成随机的通话时长，四位长度，不足补0
                String duration = NumberUtil.format(new Random().nextInt(3000), 4);

                //生成通话记录
                Calllog log = new Calllog(call1.getTel(), call2.getTel(), callTimeString, duration);

                //将通话记录写入数据文件中
                out.wirte(log);

                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭生产者
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (in != null){
            in.close();
        }

        if (out != null){
            out.close();
        }
    }
}
