package com.yhy.version.helper.widget.def;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yhy.version.helper.VersionHelper;
import com.yhy.version.helper.entity.ApkVersionDefault;
import com.yhy.version.helper.listener.ApkDownloadListener;
import com.yhy.version.helper.widget.VersionDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-13 10:58
 * version: 1.0.0
 * desc   :
 */
public abstract class DialogViewProviderDefault implements VersionDialog.DialogViewProvider<ApkVersionDefault> {

    private ApkVersionDownloadListener mListener;
    private VersionDialogViewDefault mView;

    @Override
    public View getView(final Context ctx, final ApkVersionDefault version, final VersionDialog dialog) {
        mView = new VersionDialogViewDefault(ctx);
        mListener = new ApkVersionDownloadListener() {
            @Override
            public void onStart(ApkVersionDefault version) {
                super.onStart(version);
                // 开始下载，禁止返回消失和
                dialog.backDismiss(false).outSideDismiss(false);

                mView.startDownload();
            }

            @Override
            public void onProgress(long total, long current, float faction) {
                mView.progress(faction);
            }

            @Override
            public void onSuccess(File apk) {
                VersionHelper.<ApkVersionDefault>getInstance(mView.getContext()).getGlobalExceptionResolver().exception(new UnsupportedOperationException("安装"));
            }
        };

        mView
                .setTitle("发现新版本" + version.name + "，需要更新吗？")
                .setDesc(version.desc)
                .setTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(new Date(version.time)))
                .setCancel("暂不更新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (version.force) {
                            VersionHelper.<ApkVersionDefault>getInstance(mView.getContext()).getGlobalExceptionResolver().exception(new UnsupportedOperationException("最新版本属于强制更新版本"));
                            return;
                        }
                        dialog.dismiss();
                    }
                })
                .setConfirm("立即更新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (downloadApk(version, mListener)) {
                            mView.setTitle("正在下载最新版本" + version.name);
                        } else {
                            VersionHelper.<ApkVersionDefault>getInstance(mView.getContext()).getGlobalExceptionResolver().exception(new RuntimeException("请先授权内存卡读写权限"));
                        }
                    }
                });
        dialog.backDismiss(!version.force).outSideDismiss(!version.force);
        return mView;
    }

    protected abstract boolean downloadApk(ApkVersionDefault version, ApkVersionDownloadListener listener);

    protected abstract class ApkVersionDownloadListener implements ApkDownloadListener<ApkVersionDefault> {
        @Override
        public void onStart(ApkVersionDefault version) {
        }

        @Override
        public void onProgress(long total, long current, float faction) {
        }

        @Override
        public abstract void onSuccess(File apk);

        @Override
        public void onError(Throwable error) {
            VersionHelper.<ApkVersionDefault>getInstance(mView.getContext()).getGlobalExceptionResolver().exception(error);
        }
    }
}
