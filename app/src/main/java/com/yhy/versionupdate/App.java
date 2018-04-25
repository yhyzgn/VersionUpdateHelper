package com.yhy.versionupdate;

import android.app.Application;

import com.lzy.okgo.OkGo;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-23 10:12
 * version: 1.0.0
 * desc   :
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OkGo.getInstance().init(this);
    }
}
