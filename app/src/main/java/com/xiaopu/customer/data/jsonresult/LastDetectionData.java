package com.xiaopu.customer.data.jsonresult;

import java.util.List;

/**
 * Created by Administrator on 2016/8/26 0026.
 */
public class LastDetectionData {
    public LastDetectionData() {

    }

    private List<DetectionHeartrate> heartrate;
    private List<DetectionOvulation> ovulation;
    private List<DetectionPregnant> pregnant;
    private List<DetectionUrine> urine;
    private List<DetectionWeight> weight;

    private int code;

    private String msg;

    private String data;

    public List<DetectionHeartrate> getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(List<DetectionHeartrate> heartrate) {
        this.heartrate = heartrate;
    }

    public List<DetectionOvulation> getOvulation() {
        return ovulation;
    }

    public void setOvulation(List<DetectionOvulation> ovulation) {
        this.ovulation = ovulation;
    }

    public List<DetectionPregnant> getPregnant() {
        return pregnant;
    }

    public void setPregnant(List<DetectionPregnant> pregnant) {
        this.pregnant = pregnant;
    }

    public List<DetectionUrine> getUrine() {
        return urine;
    }

    public void setUrine(List<DetectionUrine> urine) {
        this.urine = urine;
    }

    public List<DetectionWeight> getWeight() {
        return weight;
    }

    public void setWeight(List<DetectionWeight> weight) {
        this.weight = weight;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
