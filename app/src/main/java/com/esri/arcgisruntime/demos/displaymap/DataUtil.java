package com.esri.arcgisruntime.demos.displaymap;

import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * @auther lizhengyang
 *
 * @version 1.0
 */
public class DataUtil {

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     * Ref List<xzq>
     *
     * @return boolean
     * Ref 如果出现异常就返回false
     */
    public static boolean xzqClassify(List<xzq> xzqList){
        try {
            for (int i = 0; i < xzqList.size(); i++){
                xzq xzq = new xzq();
                if (xzqList.get(i).getType().contains("县级")){
                    xzq.setGrade(1);
                }else if (xzqList.get(i).getType().contains("乡级")){
                    xzq.setGrade(2);
                }
                xzq.updateAll("xzqdm = ?", xzqList.get(i).getXzqdm());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     * Ref List<xzq>
     *
     * @return long[]
     *
     * Ref {number of grade1, number of grade2}
     */
    public static long[] xzqCalGrade(List<xzq> xzqList){
        try {
            int num1 = 0;
            int num2 = 0;
            for (int i = 0; i < xzqList.size(); i++){
                if (xzqList.get(i).getGrade() == 1){
                    num1++;
                }else if (xzqList.get(i).getGrade() == 2){
                    num2++;
                }
            }
            return new long[]{num1, num2};
        }catch (Exception e){
            return new long[]{0, 0};
        }
    }

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     *
     * @return List<xzq>
     */
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

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     *
     * @return List<xzq>
     */
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

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     *
     * @return List<xzq>
     */
    public static List<xzq> bubbleSort(List<xzq> xzqList) {
        return bubbleSortXZ(bubbleSortX(xzqList));
    }
}
