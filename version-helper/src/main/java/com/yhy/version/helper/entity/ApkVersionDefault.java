package com.yhy.version.helper.entity;

import java.io.Serializable;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-13 10:42
 * version: 1.0.0
 * desc   : 默认实体
 */
public class ApkVersionDefault implements Serializable {
    public int id;
    public int code;
    public String name;
    public String desc;
    public String url;
    public long time;
    public boolean force;
}
