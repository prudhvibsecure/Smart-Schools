package com.bsecure.scsm_mobile.models;

import java.io.Serializable;

public class GalleryModel implements Serializable {

    String ename, gid;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}
