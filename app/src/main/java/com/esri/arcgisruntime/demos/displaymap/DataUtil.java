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
}
