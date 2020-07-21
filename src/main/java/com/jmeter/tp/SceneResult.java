package com.jmeter.tp;

import java.util.Map;

/**
 * @author wangshuzheng
 * @date 2020/7/20 4:23 下午
 * @description
 */
public class SceneResult {

    /**开始压测时间*/
    private String startTimeStr;

    /**结束压测时间*/
    private String endTimeStr;

    /**压测请求数*/
    private String samples;

    /**平均响应时间*/
    private String average;

    private Map<String, Integer> tps;

    /**最小请求时间*/
    private String min;

    /**最大请求时间*/
    private String max;

    /**错误率*/
    private String errors;

    /** throughput */
    private String throughput;

    /**吞吐量 KB/sec*/
    private String kbPerSec;

    /**中间时间*/
    private String median;

    public String getMedian() {
        return median;
    }

    public void setMedian(String median) {
        this.median = median;
    }

    public String getSamples() {
        return samples;
    }

    public void setSamples(String samples) {
        this.samples = samples;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }


    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getThroughput() {
        return throughput;
    }

    public void setThroughput(String throughput) {
        this.throughput = throughput;
    }

    public String getKbPerSec() {
        return kbPerSec;
    }

    public void setKbPerSec(String kbPerSec) {
        this.kbPerSec = kbPerSec;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public Map<String, Integer> getTps() {
        return tps;
    }

    public void setTps(Map<String, Integer> tps) {
        this.tps = tps;
    }

    @Override
    public String toString() {
        return "SceneResult{" +
                "startTimeStr='" + startTimeStr + '\'' +
                ", endTimeStr='" + endTimeStr + '\'' +
                ", samples='" + samples + '\'' +
                ", average='" + average + '\'' +
                ", tps=" + tps +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                ", errors='" + errors + '\'' +
                ", throughput='" + throughput + '\'' +
                ", kbPerSec='" + kbPerSec + '\'' +
                ", median='" + median + '\'' +
                '}';
    }
}
