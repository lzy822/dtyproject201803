package com.esri.arcgisruntime.demos.displaymap;

import java.util.ArrayList;
import java.util.List;

public class KeyAndValue {
    private String nickname;
    private String name;
    private String value;

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
        if (name.equals("GHDLMC")) nickname = "规划地类名称";
        else if (name.equals("GHDLBM")) nickname = "规划地类代码";
        else if (name.equals("GHDLMJ")) nickname = "规划地类面积";
        else if (name.equals("PDJB")) nickname = "坡度级别";
        else if (name.equals("XZQMC")) nickname = "行政区名称";
        else if (name.equals("XZQDM")) nickname = "行政区代码";

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

    public static List<KeyAndValue> parseList(List<KeyAndValue> keyAndValues){
        List<KeyAndValue> keyAndValues1 = new ArrayList<>();
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("GHDLMC")) keyAndValues1.add(keyAndValues.get(i));
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("GHDLBM")) keyAndValues1.add(keyAndValues.get(i));
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("GHDLMJ")) keyAndValues1.add(keyAndValues.get(i));
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("PDJB")) keyAndValues1.add(keyAndValues.get(i));
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("XZQMC")) keyAndValues1.add(keyAndValues.get(i));
        }
        for (int i = 0; i < keyAndValues.size(); i++){
            if (keyAndValues.get(i).getName().equals("XZQDM")) keyAndValues1.add(keyAndValues.get(i));
        }
        return keyAndValues1;
    }
}
