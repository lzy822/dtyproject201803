package com.esri.arcgisruntime.demos.displaymap;

/**
 * Created by 54286 on 2018/3/20.
 */

public class mTapeobj {
    private String m_POIC;
    private String m_name;
    private String m_time;
    private String m_path;
    private float m_X;
    private float m_Y;

    public mTapeobj(String m_POIC, String m_name, String m_time, String m_path) {
        this.m_POIC = m_POIC;
        this.m_name = m_name;
        this.m_time = m_time;
        this.m_path = m_path;
    }

    public mTapeobj(String m_POIC, String m_name, String m_time, String m_path, float m_X, float m_Y) {
        this.m_POIC = m_POIC;
        this.m_name = m_name;
        this.m_time = m_time;
        this.m_path = m_path;
        this.m_X = m_X;
        this.m_Y = m_Y;
    }

    public String getM_POIC() {
        return m_POIC;
    }

    public void setM_POIC(String m_POIC) {
        this.m_POIC = m_POIC;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getM_time() {
        return m_time;
    }

    public void setM_time(String m_time) {
        this.m_time = m_time;
    }

    public String getM_path() {
        return m_path;
    }

    public void setM_path(String m_path) {
        this.m_path = m_path;
    }

    public float getM_X() {
        return m_X;
    }

    public void setM_X(float m_X) {
        this.m_X = m_X;
    }

    public float getM_Y() {
        return m_Y;
    }

    public void setM_Y(float m_Y) {
        this.m_Y = m_Y;
    }
}
