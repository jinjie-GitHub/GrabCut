package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/12/28 11:39
 * 邮箱：499629556@qq.com
 */

public class VersionBean extends BaseBean {
    private String version;
    private String whatsnew;
    private String url;
    private boolean force;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWhatsnew() {
        return whatsnew;
    }

    public void setWhatsnew(String whatsnew) {
        this.whatsnew = whatsnew;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
