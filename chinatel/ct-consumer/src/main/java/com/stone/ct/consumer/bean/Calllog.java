package com.stone.ct.consumer.bean;

import com.stone.ct.common.api.Column;
import com.stone.ct.common.api.RowKey;
import com.stone.ct.common.api.TableRef;

/**
 * 通话日志对象
 */
@TableRef("ct:calllog")
public class Calllog {
    @RowKey
    private String rowKey;
    @Column(family = "caller")
    private String call1;
    @Column(family = "caller")
    private String call2;
    @Column(family = "caller")
    private String callTime;
    @Column(family = "caller")
    private String duration;
    @Column(family = "caller")
    private String flag = "1";

    public Calllog() {
    }

    public Calllog(String value) {
        String[] values = value.split("\t");
        call1 = values[0];
        call2 = values[1];
        callTime = values[2];
        duration = values[3];
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCall1() {
        return call1;
    }

    public void setCall1(String call1) {
        this.call1 = call1;
    }

    public String getCall2() {
        return call2;
    }

    public void setCall2(String call2) {
        this.call2 = call2;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }
}
