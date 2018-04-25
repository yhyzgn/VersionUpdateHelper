package com.yhy.version.helper.listener;

import java.io.File;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-23 9:43
 * version: 1.0.0
 * desc   :
 */
public interface ApkDownloadListener<T> {
    void onStart(T version);

    void onProgress(long total, long current, float faction);

    void onSuccess(File apk);

    void onError(Throwable error);
}
