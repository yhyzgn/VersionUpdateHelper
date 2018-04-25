package com.yhy.version.helper.listener;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-23 9:54
 * version: 1.0.0
 * desc   :
 */
public interface OnVersionCheckedListener<T> {

    void onChecked(T version, String msg);
}
