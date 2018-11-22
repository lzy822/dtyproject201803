package com.esri.arcgisruntime.demos.displaymap;

import android.util.Log;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Envelope;

import java.util.ArrayList;
import java.util.List;

public class QueryInfo {
    private String name;
    private Feature feature;
    private Envelope envelope;
    private int status;
    private int fnode;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFnode() {
        return fnode;
    }

    public void setFnode(int fnode) {
        this.fnode = fnode;
    }

    public QueryInfo(String name, Feature feature, Envelope envelope) {
        this.name = name;
        this.feature = feature;
        this.envelope = envelope;
    }

    public QueryInfo(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public QueryInfo(String name, Feature feature, Envelope envelope, int status, int fnode) {
        this.name = name;
        this.feature = feature;
        this.envelope = envelope;
        this.status = status;
        this.fnode = fnode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    static public List<QueryInfo> sort(List<QueryInfo> queryInfos){
        List<QueryInfo> integerList = new ArrayList<>();
        //获取批文年份
         for (int i = 0; i < queryInfos.size(); ++i){
             boolean isOK = true;
             String str = queryInfos.get(i).getName();
             int year = Integer.valueOf(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
             for (int j = 0; j < integerList.size(); ++j) {
                 if (year == Integer.valueOf(integerList.get(j).getName())) {
                     isOK = false;
                     break;
                 }
             }
             if (isOK) integerList.add(new QueryInfo(Integer.toString(year), 1));
         }
        //按批文号码逐年整理批文
        for (int i = 0; i < queryInfos.size() - 1; ++i){
            for (int j = 1; j < queryInfos.size() - i; ++j){
                String name1 = queryInfos.get(j - 1).getName();
                String name2 = queryInfos.get(j).getName();
                int num1 = Integer.valueOf(name1.substring(name1.indexOf("]") + 1, name1.indexOf("号")));
                int num2 = Integer.valueOf(name2.substring(name2.indexOf("]") + 1, name2.indexOf("号")));
                if (num1 > num2){
                    QueryInfo TempQueryInfo = queryInfos.get(j - 1);
                    queryInfos.set(j - 1, queryInfos.get(j));
                    queryInfos.set(j, TempQueryInfo);
                }
            }
        }
        //按年份逐年整理批文
        for (int i = 0; i < queryInfos.size() - 1; ++i){
            for (int j = 1; j < queryInfos.size() - i; ++j){
                String name1 = queryInfos.get(j - 1).getName();
                String name2 = queryInfos.get(j).getName();
                int num1 = Integer.valueOf(name1.substring(name1.indexOf("[") + 1, name1.indexOf("]")));
                int num2 = Integer.valueOf(name2.substring(name2.indexOf("[") + 1, name2.indexOf("]")));
                if (num1 > num2){
                    QueryInfo TempQueryInfo = queryInfos.get(j - 1);
                    queryInfos.set(j - 1, queryInfos.get(j));
                    queryInfos.set(j, TempQueryInfo);
                }
            }
        }
        //按年份逐年整理年份表
        for (int i = 0; i < integerList.size() - 1; ++i){
            for (int j = 1; j < integerList.size() - i; ++j){
                String name1 = integerList.get(j - 1).getName();
                String name2 = integerList.get(j).getName();
                int num1 = Integer.valueOf(name1);
                int num2 = Integer.valueOf(name2);
                if (num1 > num2){
                    QueryInfo TempQueryInfo = integerList.get(j - 1);
                    integerList.set(j - 1, integerList.get(j));
                    integerList.set(j, TempQueryInfo);
                }
            }
        }
        queryInfos.addAll(integerList);
        //按status
        for (int i = 0; i < queryInfos.size() - 1; ++i){
            for (int j = 1; j < queryInfos.size() - i; ++j){
                int num1 = Integer.valueOf(queryInfos.get(j - 1).getStatus());
                int num2 = Integer.valueOf(queryInfos.get(j).getStatus());
                if (num1 > num2){
                    QueryInfo TempQueryInfo = queryInfos.get(j - 1);
                    queryInfos.set(j - 1, queryInfos.get(j));
                    queryInfos.set(j, TempQueryInfo);
                }
            }
        }
        return integerList;
    }

    static public List<QueryInfo> sort1(List<QueryInfo> queryInfos){
        List<QueryInfo> integerList = new ArrayList<>();
        //获取批文年份
        for (int i = 0; i < queryInfos.size(); ++i){
            String str = queryInfos.get(i).getName();
            if (!str.contains("[")) {
                integerList.add(new QueryInfo(str, 1));
                queryInfos.remove(queryInfos.get(i));
                --i;
            }
        }
        //按批文号码逐年整理批文
        for (int i = 0; i < queryInfos.size() - 1; ++i){
            for (int j = 1; j < queryInfos.size() - i; ++j){
                String name1 = queryInfos.get(j - 1).getName();
                String name2 = queryInfos.get(j).getName();
                int num1 = Integer.valueOf(name1.substring(name1.indexOf("]") + 1, name1.indexOf("号")));
                int num2 = Integer.valueOf(name2.substring(name2.indexOf("]") + 1, name2.indexOf("号")));
                if (num1 > num2){
                    QueryInfo TempQueryInfo = queryInfos.get(j - 1);
                    queryInfos.set(j - 1, queryInfos.get(j));
                    queryInfos.set(j, TempQueryInfo);
                }
            }
        }
        //按年份逐年整理批文
        for (int i = 0; i < queryInfos.size() - 1; ++i){
            for (int j = 1; j < queryInfos.size() - i; ++j){
                String name1 = queryInfos.get(j - 1).getName();
                String name2 = queryInfos.get(j).getName();
                int num1 = Integer.valueOf(name1.substring(name1.indexOf("[") + 1, name1.indexOf("]")));
                int num2 = Integer.valueOf(name2.substring(name2.indexOf("[") + 1, name2.indexOf("]")));
                if (num1 > num2){
                    QueryInfo TempQueryInfo = queryInfos.get(j - 1);
                    queryInfos.set(j - 1, queryInfos.get(j));
                    queryInfos.set(j, TempQueryInfo);
                }
            }
        }
        //按年份逐年整理年份表
        for (int i = 0; i < integerList.size() - 1; ++i){
            for (int j = 1; j < integerList.size() - i; ++j){
                String name1 = integerList.get(j - 1).getName();
                String name2 = integerList.get(j).getName();
                int num1 = Integer.valueOf(name1);
                int num2 = Integer.valueOf(name2);
                if (num1 > num2){
                    QueryInfo TempQueryInfo = integerList.get(j - 1);
                    integerList.set(j - 1, integerList.get(j));
                    integerList.set(j, TempQueryInfo);
                }
            }
        }
        //queryInfos.addAll(integerList);

        for (int i = 0, j = 0; i < integerList.size(); ++i){
            if (Integer.valueOf(integerList.get(i).getName()) <= queryInfos.get(i).getFnode()) {
                queryInfos.add(j, integerList.get(i));
                ++j;
            }
            else {
                queryInfos.add(integerList.get(i));
            }
        }

        return integerList;
    }
}
