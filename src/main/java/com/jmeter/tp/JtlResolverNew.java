package com.jmeter.tp;

import com.google.common.collect.Ordering;

import java.io.*;
import java.util.*;

/**
 * @author wangshuzheng
 * @date 2020/7/20 6:01 下午
 * @description
 */
public class JtlResolverNew {

    int max = 0;
    int min = Integer.MAX_VALUE;

    private int twoMi;

    private int lessMax;

    private String tpList;

    private int[] lessTop;

    public void setTwoMi(int twoMi) {
        this.twoMi = twoMi;
    }

    private List<Integer> durationEnum;

    private HashMap<Integer, List<Integer>> duration;

    public void setDurationEnum() {
        this.durationEnum = durationEnum();
    }

    public void setDuration() {
        duration = new HashMap<>(1000);
        for (int du : durationEnum) {
            duration.put(du, new LinkedList<>());
        }
    }

    public void setLessMax() {
        this.lessMax = 1 << twoMi;
    }

    public void setLessTop() {
        this.lessTop = new int[lessMax + 1];
        for (int i = 0; i <= lessMax; i++) {
            lessTop[i] = 0;
        }
    }

    public void setTpList(String tpList) {
        this.tpList = tpList;
    }

    public void resloveJtl(File file) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String oneLine;
        //所有的行
        List<Integer> timestampList = new LinkedList<>();
        String firstLine = null, lastLine = "";
        long sumElapsed = 0;
        int lessTopCount = 0;
        while ((oneLine = br.readLine()) != null) {
            if (oneLine.startsWith("timeStamp")) {
                continue;
            }
            if (!oneLine.isEmpty()) {
                if (null == firstLine) {
                    firstLine = oneLine;
                }
                lastLine = oneLine;
                String[] split = oneLine.split(",");
                //响应时间
                String elapsed = split[1];
                if (null != elapsed && !elapsed.isEmpty()) {
                    int elapsedT = Integer.parseInt(elapsed);
                    timestampList.add(elapsedT);
                    if (elapsedT > max) {
                        max = elapsedT;
                    }
                    if (elapsedT < min) {
                        min = elapsedT;
                    }
                    sumElapsed += elapsedT;
                    if (elapsedT <= lessMax) {
                        lessTop[elapsedT] += 1;
                        lessTopCount += 1;
                    } else {
                        int mapIdx = durationEnum.get(0);
                        for (int j = durationEnum.size() - 1; j >= 0; j--) {
                            if (elapsedT > durationEnum.get(j)) {
                                if (j == durationEnum.size() - 1) {
                                    mapIdx = durationEnum.get(j);
                                } else {
                                    mapIdx = durationEnum.get(j + 1);
                                }
                                break;
                            }
                        }
                        List<Integer> multiset = duration.get(mapIdx);
                        multiset.add(elapsedT);
                        duration.put(mapIdx, multiset);
                    }
                }
            }
        }
        int total = timestampList.size();
        System.out.println("totalLines:" + total);
        System.out.println("第一行:" + firstLine);
        System.out.println("最后一行：" + lastLine);
        if (null != firstLine) {
            //压测开始时间
            String allStartTimestampStr = firstLine.split(",")[0];
            Long allStartTime = Long.valueOf(allStartTimestampStr);
            //压测结束时间
            String allEndTimestampStr = lastLine.split(",")[0];
            Long allEndTime = Long.valueOf(allEndTimestampStr);
            getOneRpsResult(allStartTime, allEndTime, sumElapsed, total, lessTopCount);
        }
    }

    public List<Integer> durationEnum() {
        int max = 11;
        List<Integer> set = new ArrayList<>();
        for (int i = twoMi - 1; i <= max; i++) {
            set.add((2 << i));
        }
        set.add((2 << max) + 1);
        return set;
    }

    private void getOneRpsResult(Long startTime, Long endTime, long sumElapsed, int count, int arraysCount) {
        //总的响应时间
        System.out.println("run getOneRpsResult");
        Map<String, Integer> tps = new HashMap<>(10);
        HashMap<String, Integer> tprs = new HashMap<>(10);
        if (null != tpList && !tpList.isEmpty()) {
            int denominator = 1000;
            for (String tp : tpList.split(",")) {
                int tpVal = (int) ((count * Long.parseLong(tp)) / denominator);
                tps.put(tp, tpVal);
                tprs.put(tp, 0);
            }
        }
        for (String h : tps.keySet()) {
            if (tps.get(h) < arraysCount) {
                int countIn = 0;
                for (int i = 0; i < lessTop.length; i++) {
                    if (countIn + lessTop[i] > tps.get(h)) {
                        tprs.put(h, i);
                        break;
                    }
                    countIn += lessTop[i];
                }
            } else {
                int countIn = arraysCount;
                List<Integer> position = null;
                for (Integer aLong : durationEnum) {
                    int ds = duration.get(aLong).size();
                    if (countIn + ds > tps.get(h)) {
                        position = duration.get(aLong);
                        break;
                    }
                    countIn += ds;
                }
                if (null != position) {
                    try {
                        Ordering<Integer> order = Ordering.natural();
                        position = order.immutableSortedCopy(position);
                        int idx = tps.get(h) - countIn;
                        tprs.put(h, position.get(idx));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        SceneResult sceneResult = new SceneResult();
        sceneResult.setStartTimeStr(ResolveProperties.DATE_FORMAT.format(startTime));
        sceneResult.setEndTimeStr(ResolveProperties.DATE_FORMAT.format(endTime));
        sceneResult.setSamples(count + "");
        sceneResult.setAverage(sumElapsed / count + "");
        sceneResult.setTps(tprs);
        sceneResult.setMin(String.valueOf(min));
        sceneResult.setMax(String.valueOf(max));
        System.out.println(sceneResult.toString());

    }

    public static void main(String[] args) throws Exception {
        JtlResolverNew jtlResolverNew = new JtlResolverNew();
        String filePath = ResolveProperties.getPropertyByKey("file.path");
        String fileName = ResolveProperties.getPropertyByKey("file.name");
        if (null == filePath || filePath.equalsIgnoreCase("")) {
            System.err.println("get file.path property fail ");
            System.exit(1);
        }
        if (null == fileName || fileName.equalsIgnoreCase("")) {
            System.err.println("get file.name property fail ");
            System.exit(1);
        }
        System.out.println(filePath + fileName);
        String defaultTpList = "900,990,999";
        String tpList = ResolveProperties.getPropertyByKey("tp.list");
        if (null != tpList && !tpList.isEmpty()) {
            defaultTpList = tpList;
        }
        String tm = ResolveProperties.getPropertyByKey("two.mi");
        if (null != tm && !tm.isEmpty()) {
            try {
                int tmi = Integer.parseInt(tm);
                jtlResolverNew.setTwoMi(tmi);
            } catch (NumberFormatException e) {
                System.err.println("tm to tmi fail, tm is " + tm + " error, " + e);
                System.exit(500);
            }
        }
        jtlResolverNew.setDurationEnum();
        jtlResolverNew.setDuration();
        jtlResolverNew.setLessMax();
        jtlResolverNew.setLessTop();
        jtlResolverNew.setTpList(defaultTpList);
        File file = new File(filePath + fileName);
        jtlResolverNew.resloveJtl(file);
    }

}
