package com.esri.arcgisruntime.demos.displaymap;

import android.graphics.Bitmap;

public class bt {
    private Bitmap m_bm;
    private String m_path;
    private String poic;
    private int rotate;


    /**
     *
     * @param m_bm
     * @param m_path
     * @param rotate
     */
    public bt(Bitmap m_bm, String m_path, int rotate) {
        this.m_bm = m_bm;
        this.m_path = m_path;
        this.rotate = rotate;
    }

    public String getPoic() {
        return poic;
    }

    public void setPoic(String poic) {
        this.poic = poic;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public bt(Bitmap m_bm, String m_path) {
        this.m_bm = m_bm;
        this.m_path = m_path;
    }

    public Bitmap getM_bm() {
        return m_bm;
    }

    public void setM_bm(Bitmap m_bm) {
        this.m_bm = m_bm;
    }

    public String getM_path() {
        return m_path;
    }

    public void setM_path(String m_path) {
        this.m_path = m_path;
    }
}
