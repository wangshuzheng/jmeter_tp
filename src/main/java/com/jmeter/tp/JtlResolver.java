package com.jmeter.tp;

import com.google.common.collect.Ordering;

import java.io.*;
import java.util.*;

/**
 * @author wangshuzheng
 * @date 2020/7/20 4:23 下午
 * @description
 */
public class JtlResolver {

    public String resloveJtl(File file, List<Integer> durationList) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String oneLine;
        //所有的行
        List<String> totalLines = new LinkedList<>();
        List<Long> timestampList = new LinkedList<>();
        while ((oneLine = br.readLine()) != null) {
            if (oneLine.startsWith("timeStamp")) {
                continue;
            }
            totalLines.add(oneLine);
            String[] split = oneLine.split(",");
            String timestampStr = split[0];
            Long timestamp = Long.valueOf(timestampStr);
            timestampList.add(timestamp);
        }
        System.out.println("totalLines:" + totalLines.size());
        System.out.println("第一行:" + totalLines.get(0));
        System.out.println("最后一行：" + totalLines.get(totalLines.size() - 1));
        System.out.println();
        //压测开始时间
        String allStartTimestampStr = totalLines.get(0).split(",")[0];
        Long allStartTime = Long.valueOf(allStartTimestampStr);
        //压测结束时间
        String allEndTimestampStr = totalLines.get(totalLines.size() - 1).split(",")[0];
        Long allEndTime = Long.valueOf(allEndTimestampStr);
        Long startTime = allStartTime;
        Long endTime = allEndTime;
        Integer startIndex = 0;
        for (int i = 0; i < durationList.size(); i++) {
            Integer duration = durationList.get(i);
            if (durationList.size() != 1) {
                endTime = startTime + duration * 1000;
            }
            if (i == (durationList.size() - 1)) {
                endTime = allEndTime;
            }
            //计算开始和结束
            Integer endIndex = getEndIndex(endTime, timestampList);
            getOneRpsResult(startIndex, endIndex, startTime, endTime, timestampList, totalLines);
            startTime = endTime;
            startIndex = endIndex;
        }
        return null;
    }

    private void getOneRpsResult(Integer startIndex, Integer endIndex, Long startTime, Long endTime, List<Long> timestampList,
                                 List<String> totalLines) throws Exception {
        int index = 0;
        //总的响应时间
        int sumElapsed = 0;
        Integer failSize = 0;
        Integer totalBytes = 0;
        List<Integer> elapsedList = new LinkedList<Integer>();
        for (Integer i = startIndex; i <= endIndex; i++) {
            String row = totalLines.get(i);
            String[] split = row.split(",");
            //响应时间
            String elapsed = split[1];
            sumElapsed += Integer.valueOf(elapsed);
            elapsedList.add(Integer.valueOf(elapsed));
            //成功与否
            String success = split[7];
            if (!"true".equals(success)) {
                failSize++;
            }
            //字节
            String bytes = split[8];
            if (!bytes.isEmpty()) {
                totalBytes += Integer.valueOf(bytes);
                index++;
            }
        }

        Ordering<Integer> order = Ordering.natural();
        elapsedList = order.immutableSortedCopy(elapsedList);

        Integer tp90 = elapsedList.size() * 9 / 10;
        Integer tp95 = elapsedList.size() * 95 / 100;
        Integer tp99 = elapsedList.size() * 99 / 100;
        Map<String, Integer> tprs = new HashMap<>();
        tprs.put("90", tp90);
        tprs.put("95", tp95);
        tprs.put("99", tp99);
        Long l = timestampList.get(index - 1) - timestampList.get(0);
        SceneResult sceneResult = new SceneResult();
        sceneResult.setStartTimeStr(ResolveProperties.DATE_FORMAT.format(startTime));
        sceneResult.setEndTimeStr(ResolveProperties.DATE_FORMAT.format(endTime));
        sceneResult.setSamples(index + "");
        sceneResult.setAverage(sumElapsed / index + "");
        sceneResult.setTps(tprs);
        sceneResult.setMin(elapsedList.get(0) + "");
        sceneResult.setMax(elapsedList.get(index - 1) + "");
        sceneResult.setMedian(elapsedList.get(index / 2) + "");
        sceneResult.setErrors(String.format("%.2f", failSize * 100.0 / index) + "%");
        sceneResult.setThroughput(String.format("%.2f", index * 1.0 / (l * 1.0 / 1000)));
        sceneResult.setKbPerSec(String.format("%.2f", totalBytes * 1.0 / 1024 / (l * 1.0 / 1000)));
        System.out.println(sceneResult.toString());
        System.out.println();

    }

    private Integer getStartIndex(Long startTime, List<Long> timestampList) {
        int index = 0;
        for (int i = 0; i < timestampList.size() - 1; i++) {
            if (startTime.longValue() >= timestampList.get(i).longValue() && startTime.longValue() <= timestampList.get(i + 1).longValue()) {
                return index;
            }
            index++;
        }
        return index;
    }

    private Integer getEndIndex(Long endTime, List<Long> timestampList) {
        for (int i = timestampList.size() - 1; i >= 0; i--) {

            if (endTime.longValue() >= timestampList.get(i).longValue()) {
                return i;
            }
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        List<Integer> durationList = new ArrayList<Integer>();
        durationList.add(10);
        /*durationList.add(10);
        durationList.add(10);
        durationList.add(10);
        durationList.add(10);
        durationList.add(10);*/
        Properties properties;
        properties = new Properties();
        JtlResolver jtlResolver = new JtlResolver();
        try {
            InputStream inputStream = jtlResolver.getClass().getClassLoader().getResourceAsStream("filename.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath = properties.getProperty("file.path");
        String fileName = properties.getProperty("file.name");
        System.out.println(filePath + fileName);
        File file = new File(filePath + fileName);
        jtlResolver.resloveJtl(file, durationList);
    }

}
