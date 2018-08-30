package com.esri.arcgisruntime.demos.displaymap;

import java.util.List;

public class DataUtil {
    public static boolean xzqClassify(List<xzq> xzqs){
        try {
            for (int i = 0; i < xzqs.size(); i++){
                xzq xzq = new xzq();
                if (xzqs.get(i).getType().contains("县级")){
                    xzq.setGrade(1);
                }else if (xzqs.get(i).getType().contains("乡级")){
                    xzq.setGrade(2);
                }
                xzq.updateAll("xzqdm = ?", xzqs.get(i).getXzqdm());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static long[] xzqCalGrade(List<xzq> xzqs){
        try {
            int num1 = 0;
            int num2 = 0;
            for (int i = 0; i < xzqs.size(); i++){
                if (xzqs.get(i).getGrade() == 1){
                    num1++;
                }else if (xzqs.get(i).getGrade() == 2){
                    num2++;
                }
            }
            return new long[]{num1, num2};
        }catch (Exception e){
            return new long[]{0, 0};
        }
    }

    public static List<xzq> bubbleSortX(List<xzq> xzqList) {
        int len = xzqList.size();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (Long.valueOf(xzqList.get(j).getXzqdm().substring(0, 6)) > Long.valueOf(xzqList.get(j + 1).getXzqdm().substring(0, 6))) {        // 相邻元素两两对比
                    xzq temp = xzqList.get(j + 1);        // 元素交换
                    xzqList.set(j + 1, xzqList.get(j));
                    xzqList.set(j, temp);
                }
            }
        }
        return xzqList;
    }

    public static List<xzq> bubbleSortXZ(List<xzq> xzqList) {
        int len = xzqList.size();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (xzqList.get(j).getXzqdm().length() == 9 && xzqList.get(j + 1).getXzqdm().length() == 9) {
                    if (Long.valueOf(xzqList.get(j).getXzqdm()) > Long.valueOf(xzqList.get(j + 1).getXzqdm())) {        // 相邻元素两两对比
                        xzq temp = xzqList.get(j + 1);        // 元素交换
                        xzqList.set(j + 1, xzqList.get(j));
                        xzqList.set(j, temp);
                    }
                }
            }
        }
        return xzqList;
    }



    public static List<xzq> bubbleSort(List<xzq> xzqList) {
        return bubbleSortXZ(bubbleSortX(xzqList));
    }
}
