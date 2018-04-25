package com.yhy.version.helper;

import android.content.Context;

import com.yhy.version.helper.listener.OnVersionCheckedListener;
import com.yhy.version.helper.widget.VersionDialog;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-10 15:42
 * version: 1.0.0
 * desc   :
 */
public class VersionHelper<T> {

    private static volatile VersionHelper instance;

    private Context mCtx;
    private String mUrl;
    private boolean mBackDismiss;
    private boolean mOutsideDismiss;
    private CheckTask<T> mCheckTask;
    private OnVersionCheckedListener<T> mListener;
    private T mVersion;
    private VersionDialog.DialogViewProvider<T> mDialogViewProvider;
    private VersionDialog<T> mDialog;
    private GlobalExceptionResolver mResolver;

    private VersionHelper(Context ctx) {
        if (null != instance) {
            throw new UnsupportedOperationException("Can not instantiate singleton class.");
        }
        mCtx = ctx;
        mBackDismiss = true;
        mOutsideDismiss = true;
        mListener = new OnVersionCheckedListenerDefault();
    }

    public static <T> VersionHelper<T> getInstance(Context ctx) {
        if (null == instance) {
            synchronized (VersionHelper.class) {
                if (null == instance) {
                    instance = new VersionHelper<T>(ctx);
                }
            }
        }
        return instance;
    }

    public VersionHelper<T> url(String url) {
        mUrl = url;
        return this;
    }

    public VersionHelper<T> backDismiss(boolean enable) {
        mBackDismiss = enable;
        return this;
    }

    public VersionHelper<T> outSideDismiss(boolean enable) {
        mOutsideDismiss = enable;
        return this;
    }

    public VersionHelper<T> dialogViewProvider(VersionDialog.DialogViewProvider<T> provider) {
        mDialogViewProvider = provider;
        return this;
    }

    public VersionHelper<T> setGlobalExceptionResolver(GlobalExceptionResolver resolver) {
        mResolver = resolver;
        return this;
    }

    public void check(CheckTask<T> task) {
        if (null == task) {
            throw new IllegalArgumentException("The argument 'CheckTask<T>' can not be null.");
        }

        mCheckTask = task;
        mCheckTask.check(mUrl, mListener);
    }

    private void tipVersionUpdate() {
        if (null == mDialogViewProvider) {
            throw new IllegalStateException("Must set DialogViewProvider by call 'dialogViewProvider(VersionDialog.DialogViewProvider provider)' at first.");
        }

        mDialog = new VersionDialog<>(mCtx, mDialogViewProvider);
        mDialog.backDismiss(true).outSideDismiss(true).version(mVersion);
        mDialog.show();
    }

    public GlobalExceptionResolver getGlobalExceptionResolver() {
        return mResolver;
    }

    public interface CheckTask<T> {
        void check(String url, OnVersionCheckedListener<T> listener);
    }

    private class OnVersionCheckedListenerDefault implements OnVersionCheckedListener<T> {
        @Override
        public void onChecked(T version, String msg) {
            mVersion = version;
            if (null != mVersion) {
                tipVersionUpdate();
            }
        }
    }

    public interface GlobalExceptionResolver {
        void exception(Throwable exp);
    }
}
