package com.esri.arcgisruntime.demos.displaymap;

import android.graphics.PointF;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;
import java.util.List;

public class lineUtil {
    private static final String TAG = "lineUtil";
    private static final long time = 100;

    public static double[] getLineNormalEquation(double x1, double y1, double x2, double y2){
        if (x1 == x2 & y1 != y2){
            double[] parameters = {1, 0, -1 * x1};
            return parameters;
        }else if (x1 != x2 & y1 == y2){
            double[] parameters = {0, 1, -1 * y1};
            return parameters;
        }else if (x1 == x2 & y1 == y2){
            return null;
        }else {
            double[] parameters = {y2 - y1, x1 - x2, x2 * y1 - x1 * y2};
            return parameters;
        }
        /*double[] parameters = {y2 - y1, x1 - x2, x2 * y1 - x1 * y2};
        return parameters;*/
    }

    public static PointF getPoint1(double[] line1, double[] line2){
        if (line1 != null & line2 != null) {
            if ((line1[0] * line2[1] - line1[1] * line2[0]) != 0)
                return new PointF((float) (line2[2] * line1[1] - line1[2] * line2[1]) / (float) (line1[0] * line2[1] - line1[1] * line2[0]), (float) (line2[0] * line1[2] - line1[0] * line2[2]) / (float) (line1[0] * line2[1] - line1[1] * line2[0]));
            else {
                Log.w(TAG, "getPoint1: " + "两直线没有交点！");
                return null;
            }
        }else {
            Log.w(TAG, "getPoint1: " + "有待求直线没有意义！");
            return null;
        }
    }

    public static List<double[]> getParallelLineEquation(double[] line, double delta){
        if (line != null) {
            double[] parameters0 = {line[0], line[1], (Math.sqrt(line[0] * line[0] + line[1] * line[1])) * delta + line[2]};
            double[] parameters1 = {line[0], line[1], line[2] - Math.sqrt(line[0] * line[0] + line[1] * line[1]) * delta};
            if ( (line[0] != 0 & line[1] == 0)) {
                List<double[]> parameterList = new ArrayList<>();
                parameterList.add(parameters1);
                parameterList.add(parameters0);
                return parameterList;
            } else if ((line[0] == 0 & line[1] != 0) || (line[0] > 0 & line[1] > 0) || (line[0] < 0 & line[1] < 0)) {
                List<double[]> parameterList = new ArrayList<>();
                parameterList.add(parameters0);
                parameterList.add(parameters1);
                return parameterList;
            } else if (line[0] == 0 & line[1] == 0) {
                return null;
            } else {
                List<double[]> parameterList = new ArrayList<>();
                parameterList.add(parameters1);
                parameterList.add(parameters0);
                return parameterList;
            }
        }else return null;
    }

    public static double[] getVerticalLineEquation(double[] line, PointF pt){
        if (line[0] == 0 & line[1] != 0){
            double[] parameters = {1, 0, -1 * pt.x};
            return parameters;
        }else if (line[0] != 0 & line[1] == 0){
            double[] parameters = {0, 1, -1 * pt.y};
            return parameters;
        }else if (line[0] == 0 & line[1] == 0){
            return null;
        }else {
            double[] parameters = {line[1] / line[0], -1, pt.y - line[1] / line[0] * pt.x};
            return parameters;
        }
    }

    public static double[] getExactVerticalLine(List<double[]> lines, double[] mline, PointF p1, PointF p2){
        if (lines != null & lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                PointF pt = getPoint1(lines.get(i), mline);
                Log.w(TAG, "getExactVerticalLine: " + pt.toString());
                if (pt.x >= (p1.x >= p2.x ? p2.x : p1.x) & pt.x <= (p1.x >= p2.x ? p1.x : p2.x) & pt.y >= (p1.y >= p2.y ? p2.y : p1.y) & pt.y <= (p1.y >= p2.y ? p1.y : p2.y))
                    continue;
                else return lines.get(i);
            }
        }
        return null;
    }

    public static String getExternalPolygon(String line, double delta){
        String[] lines = line.trim().split(" ");
        int size = lines.length;
        List<PointF> pointFS1 = new ArrayList<>();
        for (int i = 0; i < size; i++){
            String[] pt = lines[i].split(",");
            pointFS1.add(new PointF(Float.valueOf(pt[1]) * time, Float.valueOf(pt[0]) * time));
        }
        List<PointF> pointFS = new ArrayList<>();
        double[] mline1 = new double[2];
        for (int i = 0; i < pointFS1.size(); i++){
            if (i == 0){
                Log.w(TAG, "getExternalPolygon: " + pointFS1.get(i).x+ "; " +pointFS1.get(i).y+ "; " +pointFS1.get(i + 1).x+ "; " +pointFS1.get(i + 1).y);
                double[] mline = getLineNormalEquation(pointFS1.get(i).x, pointFS1.get(i).y, pointFS1.get(i + 1).x, pointFS1.get(i + 1).y);
                Log.w(TAG, "getExternalPolygon: " + mline[0] + "; " + mline[1] + "; " + mline[2]);
                List<double[]> floats = getParallelLineEquation(mline, delta);
                double[] mvline = getExactVerticalLine(getParallelLineEquation(getVerticalLineEquation(mline, pointFS1.get(i)), delta), mline, pointFS1.get(i), pointFS1.get(i + 1));
                if (mvline == null){
                    mvline = getExactVerticalLine(getParallelLineEquation(getVerticalLineEquation(mline, pointFS1.get(i)), delta), getLineNormalEquation(pointFS1.get(i + 1).x, pointFS1.get(i + 1).y, pointFS1.get(i + 2).x, pointFS1.get(i + 2).y), pointFS1.get(i), pointFS1.get(i + 1));
                }
                Log.w(TAG, "getExternalPolygon: " +  + mvline[0] + "; " + mvline[1] + "; " + mvline[2]);
                for (int j = 0; j < floats.size(); j++){
                    pointFS.add(getPoint1(floats.get(j), mvline));
                    Log.w(TAG, "getExternalPolygon: " + getPoint1(floats.get(j), mvline));
                }
                mline1 = mline;
            }else if (i == pointFS1.size() - 1){
                double[] mline = getLineNormalEquation(pointFS1.get(i - 1).x, pointFS1.get(i - 1).y, pointFS1.get(i).x, pointFS1.get(i).y);
                if (mline == null){
                    mline = getLineNormalEquation(pointFS1.get(i - 2).x, pointFS1.get(i - 2).y, pointFS1.get(i - 1).x, pointFS1.get(i - 1).y);
                    if (mline != null) pointFS = updateList(pointFS1, delta, pointFS, i - 1);
                    else {
                        mline = getLineNormalEquation(pointFS1.get(i - 3).x, pointFS1.get(i - 3).y, pointFS1.get(i - 2).x, pointFS1.get(i - 2).y);
                        if (mline != null) pointFS = updateList(pointFS1, delta, pointFS, i - 2);
                        else {

                        }
                    }
                }else {
                    List<double[]> floats = getParallelLineEquation(mline, delta);
                    double[] mvline = getExactVerticalLine(getParallelLineEquation(getVerticalLineEquation(mline, pointFS1.get(i)), delta), mline, pointFS1.get(i - 1), pointFS1.get(i));
                    //Log.w(TAG, "getExternalPolygon: " + +mvline[0] + "; " + mvline[1] + "; " + mvline[2]);
                    if (mvline != null) {
                        for (int j = 0; j < floats.size(); j++) {
                            pointFS.add(getPoint1(floats.get(j), mvline));
                            Log.w(TAG, "getExternalPolygon: " + getPoint1(floats.get(j), mvline));
                        }
                    }
                }
            } else {
                double[] mline = getLineNormalEquation(pointFS1.get(i).x, pointFS1.get(i).y, pointFS1.get(i + 1).x, pointFS1.get(i + 1).y);
                if (mline == null) continue;
                List<double[]> floats = getParallelLineEquation(mline, delta);
                List<double[]> floats1 = getParallelLineEquation(mline1, delta);
                if (floats != null & floats1 != null) {
                    pointFS.add(getPoint1(floats.get(0), floats1.get(0)));
                    pointFS.add(getPoint1(floats.get(1), floats1.get(1)));
                }
                mline1 = mline;
            }
        }
        line = "";
        Log.w(TAG, "getExternalPolygon: " + pointFS.size());
        /*for (int i = 0; i < pointFS.size(); i++){
            line = line + pointFS.get(i).y + "," + pointFS.get(i).x + " ";
        }*/
        /*for (int i = 0; i < pointFS.size(); i = i + 2){
            line = line + pointFS.get(i).y + "," + pointFS.get(i).x + " ";
        }
        for (int i = 1; i < pointFS.size(); i = i + 2){
            line = line + pointFS.get(i).y + "," + pointFS.get(i).x + " ";
        }*/
        for (int i = 1; i < pointFS.size(); i = i + 2){
            if (pointFS.get(i) != null)
                line = line + pointFS.get(i).y / time + "," + pointFS.get(i).x / time + " ";
        }
        for (int i = pointFS.size() - 2; i >= 0; i = i - 2){
            if (pointFS.get(i) != null)
                line = line + pointFS.get(i).y / time + "," + pointFS.get(i).x / time + " ";
        }
        Log.w(TAG, "lzyyyy: " + line);
        return line;
    }

    public static List<PointF> updateList(List<PointF> pointFS1, double delta, List<PointF> pointFS, int i){
        double[] mline = getLineNormalEquation(pointFS1.get(i - 1).x, pointFS1.get(i - 1).y, pointFS1.get(i).x, pointFS1.get(i).y);
        List<double[]> floats = getParallelLineEquation(mline, delta);
        Log.w(TAG, "getExternalPolygon: " + floats.toString());
        double[] mvline = getExactVerticalLine(getParallelLineEquation(getVerticalLineEquation(mline, pointFS1.get(i)), delta), mline, pointFS1.get(i - 1), pointFS1.get(i));
        Log.w(TAG, "getExternalPolygon: " +  + mvline[0] + "; " + mvline[1] + "; " + mvline[2]);
        for (int j = 0; j < floats.size(); j++){
            pointFS.add(getPoint1(floats.get(j), mvline));
            Log.w(TAG, "getExternalPolygon: " + getPoint1(floats.get(j), mvline));
        }
        return pointFS;
    }

    public static double getDistance(double[] line, PointF pt){
        if (line != null)
            return Math.abs(line[0] * pt.x + line[1] * pt.y + line[2]) / Math.sqrt(line[0] * line[0] + line[1] * line[1]);
        else return 50;

    }

    public static double getDistance1(com.esri.arcgisruntime.demos.displaymap.Point pt0, PointF pt){
        return Math.abs(Math.pow((pt0.getX() - pt.x), 2) + Math.pow((pt0.getY() - pt.y), 2));
    }
}
