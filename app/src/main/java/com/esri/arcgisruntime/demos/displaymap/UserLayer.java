package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

public class UserLayer extends LitePalSupport {
    public static final int TIF_FILE = 1;
    public static final int SHP_FILE = 2;
    public static final int GDB_FILE = 3;

    private String name;
    private String path;
    private boolean loadstatus;
    private int type;
    private String queriedKey;

    public UserLayer() {
    }

    public UserLayer(String name, String path, int type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public UserLayer(String name, String path, boolean loadstatus, int type) {
        this.name = name;
        this.path = path;
        this.loadstatus = loadstatus;
        this.type = type;
    }

    public String getQueriedKey() {
        return queriedKey;
    }

    public void setQueriedKey(String queriedKey) {
        this.queriedKey = queriedKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLoadstatus() {
        return loadstatus;
    }

    public void setLoadstatus(boolean loadstatus) {
        this.loadstatus = loadstatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
