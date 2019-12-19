package com.xiaopu.customer.data.jsonresult;

import java.util.List;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class AllDetectionData {
    private List<DetectionBody> detectionBody;
    private List<DetectionHeartrate> detectionHeartrate;
    private List<DetectionPregnant> detectionPregnant;
    private List<DetectionWeight> detectionWeight;
    private List<DetectionOvulation> detectionOvulation;
    private List<String> detectionUrine;

    public List<String> getDetectionUrine() {
        return detectionUrine;
    }

    public void setDetectionUrine(List<String> detectionUrine) {
        this.detectionUrine = detectionUrine;
    }

    public List<DetectionBody> getDetectionBody() {
        return detectionBody;
    }

    public void setDetectionBody(List<DetectionBody> detectionBody) {
        this.detectionBody = detectionBody;
    }

    public List<DetectionHeartrate> getDetectionHeartrate() {
        return detectionHeartrate;
    }

    public void setDetectionHeartrate(List<DetectionHeartrate> detectionHeartrate) {
        this.detectionHeartrate = detectionHeartrate;
    }

    public List<DetectionPregnant> getDetectionPregnant() {
        return detectionPregnant;
    }

    public void setDetectionPregnant(List<DetectionPregnant> detectionPregnant) {
        this.detectionPregnant = detectionPregnant;
    }

    public List<DetectionWeight> getDetectionWeight() {
        return detectionWeight;
    }

    public void setDetectionWeight(List<DetectionWeight> detectionWeight) {
        this.detectionWeight = detectionWeight;
    }

    public List<DetectionOvulation> getDetectionOvulation() {
        return detectionOvulation;
    }

    public void setDetectionOvulation(List<DetectionOvulation> detectionOvulation) {
        this.detectionOvulation = detectionOvulation;
    }
}
