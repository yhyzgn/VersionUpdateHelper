package com.yhy.versionupdate;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-11 17:24
 * version: 1.0.0
 * desc   :
 */
public class ApkVersion {
    public int code;
    public String name;
    public String desc;
    public String url;

    public ApkVersion(int code, String name, String desc, String url) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.url = url;
    }
}
