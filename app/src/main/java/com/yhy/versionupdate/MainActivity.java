package com.yhy.versionupdate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.yhy.version.helper.VersionHelper;
import com.yhy.version.helper.entity.ApkVersionDefault;
import com.yhy.version.helper.listener.OnVersionCheckedListener;
import com.yhy.version.helper.widget.def.DialogViewProviderDefault;

import java.io.File;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check();

        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check() {
        VersionHelper.<ApkVersionDefault>getInstance(this)
                .url("https://fir.im/zufy")
                .dialogViewProvider(new DialogViewProviderDefault() {
                    @Override
                    protected boolean downloadApk(final ApkVersionDefault version, final ApkVersionDownloadListener listener) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            //验证是否许可权限
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                //申请权限
                                MainActivity.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                // 不下载
                                return false;
                            }
                        }

                        OkGo.<File>get(version.url)
                                .tag(version.url)
                                .execute(new FileCallback() {
                                    @Override
                                    public void onStart(Request<File, ? extends Request> request) {
                                        super.onStart(request);
                                        listener.onStart(version);
                                    }

                                    @Override
                                    public void downloadProgress(Progress progress) {
                                        super.downloadProgress(progress);
                                        listener.onProgress(progress.totalSize, progress.currentSize, progress.fraction);
                                    }

                                    @Override
                                    public void onSuccess(Response<File> response) {
                                        listener.onSuccess(response.body());
                                    }

                                    @Override
                                    public void onError(Response<File> response) {
                                        super.onError(response);
                                        listener.onError(response.getException());
                                    }
                                });
                        // 下载
                        return true;
                    }
                })
                .backDismiss(true)
                .outSideDismiss(true)
                .setGlobalExceptionResolver(new VersionHelper.GlobalExceptionResolver() {
                    @Override
                    public void exception(Throwable exp) {
                        toast(exp.getMessage());
                    }
                })
                .check(new VersionHelper.CheckTask<ApkVersionDefault>() {
                    @Override
                    public void check(String url, OnVersionCheckedListener<ApkVersionDefault> listener) {
                        ApkVersionDefault version = new ApkVersionDefault();
                        version.id = 2340;
                        version.code = 20;
                        version.name = "2.0.12";
                        version.url = "http://a.gdown.baidu.com/data/wisegame/53889e373c1a3337/zuiyou_42000.apk?from=a1101";
                        version.desc = "\t1、修复登录bug；\n\t2、新增会员开通功能，支付方式可以是支付宝或者微信；\n\t3、首页滑动优化，更流畅；\n\t4、杀了几个程序员祭天，哦不。。是杀了所有产品经理祭天！！";
                        version.time = System.currentTimeMillis();
                        version.force = true;
                        listener.onChecked(version, "发现最新版本");
                    }
                });
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
