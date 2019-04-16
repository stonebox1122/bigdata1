package com.stone.ct.web.bean;

public class Calllog {
    private Integer id;
    private Integer telid;
    private Integer dateid;
    private Integer sumCall;
    private Integer sumDuration;

    public Calllog() {
    }

    public Calllog(Integer id, Integer telid, Integer dateid, Integer sumCall, Integer sumDuration) {
        this.id = id;
        this.telid = telid;
        this.dateid = dateid;
        this.sumCall = sumCall;
        this.sumDuration = sumDuration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTelid() {
        return telid;
    }

    public void setTelid(Integer telid) {
        this.telid = telid;
    }

    public Integer getDateid() {
        return dateid;
    }

    public void setDateid(Integer dateid) {
        this.dateid = dateid;
    }

    public Integer getSumCall() {
        return sumCall;
    }

    public void setSumCall(Integer sumCall) {
        this.sumCall = sumCall;
    }

    public Integer getSumDuration() {
        return sumDuration;
    }

    public void setSumDuration(Integer sumDuration) {
        this.sumDuration = sumDuration;
    }

    @Override
    public String toString() {
        return "Calllog{" +
                "id=" + id +
                ", telid=" + telid +
                ", dateid=" + dateid +
                ", sumCall=" + sumCall +
                ", sumDuration=" + sumDuration +
                '}';
    }
}
