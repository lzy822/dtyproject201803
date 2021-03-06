package com.esri.arcgisruntime.demos.displaymap;

import java.util.ArrayList;
import java.util.List;

public class KeyAndValue {
    private String nickname;
    private String name;
    private String value;
    private int rank;

    public KeyAndValue(String nickname, String name, String value) {
        this.nickname = nickname;
        this.name = name;
        this.value = value;
    }

    public KeyAndValue(String name, String value) {
        this.name = name;
        if (value != null) {
            if (!value.trim().isEmpty()) this.value = value;
            else this.value = "无";
        }else this.value = "无";
        if (name.equals("GHDLMC")) {
            nickname = "规划地类名称";
            rank = 1;
        }
        else if (name.equals("GHDLBM")){
            nickname = "规划地类代码";
            rank = 2;
        }
        else if (name.equals("GHDLMJ")){
            nickname = "规划地类面积";
            rank = 3;
        }
        else if (name.equals("PDJB")) {
            nickname = "坡度级别";
            rank = 4;
        }
        else if (name.equals("XZQMC")) {
            nickname = "行政区名称";
            rank = 5;
        }
        else if (name.equals("XZQDM")) {
            nickname = "行政区代码";
            rank = 6;
        }
        else if (name.equals("BZPZWH")) {
            nickname = "标准批准文号";
        }
        else if (name.equals("NAME")) {
            nickname = "地类类型";
        }
        else nickname = "其他";
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static void parseList(List<KeyAndValue> keyAndValues){
        /*List<KeyAndValue> keyAndValues1 = new ArrayList<>();
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("GHDLMC")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("GHDLBM")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("GHDLMJ")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("PDJB")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("XZQMC")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("XZQDM")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("BZPZWH")) {
                keyAndValues1.add(keyAndValues.get(i));
                keyAndValues.remove(i);
                break;
            }
        }
        return keyAndValues1;*/
        for (int i = 0; i < keyAndValues.size() - 1; ++i){
            for (int j = 0; j < keyAndValues.size() - 1 - i; ++j){
                if (keyAndValues.get(i).rank > keyAndValues.get(i + 1).rank){
                    KeyAndValue temp = keyAndValues.get(i);
                    keyAndValues.set(i, keyAndValues.get(i + 1));
                    keyAndValues.set(i + 1, temp);
                }
            }
        }
    }
}
