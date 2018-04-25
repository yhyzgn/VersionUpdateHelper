package com.yhy.version.helper.widget;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yhy.version.helper.R;
import com.yhy.version.helper.utils.VHUtils;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-10 15:41
 * version: 1.0.0
 * desc   :
 */
public class VersionDialog<T> {

    private Context mCtx;
    private DialogViewProvider<T> mProvider;
    private WindowManager mWm;
    private WindowManager.LayoutParams mParams;
    private RelativeLayout mScreen;
    private View mView;
    private T mVersion;

    private boolean mIsShowing;
    private boolean mBackDismiss;
    private boolean mHomeDismiss;
    private boolean mOutsideDismiss;

    private HomeKeyReceiver mHomeReceiver;
    private IntentFilter mFilter;
    private CardView mContainer;

    public VersionDialog(Context ctx) {
        this(ctx, null);
    }

    public VersionDialog(Context ctx, DialogViewProvider<T> provider) {
        mCtx = ctx;
        mProvider = provider;
        init();
    }

    private void init() {
        mWm = (WindowManager) mCtx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.CENTER;

        setupScreen();

        mBackDismiss = mHomeDismiss = mOutsideDismiss = true;
        mHomeReceiver = new HomeKeyReceiver();
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void show() {
        if (mIsShowing || null == mScreen) {
            return;
        }
        setupView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mCtx)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mCtx.getPackageName()));
                mCtx.startActivity(intent);
            } else {
                mWm.addView(mScreen, mParams);
            }
        } else {
            mWm.addView(mScreen, mParams);
        }
        mCtx.registerReceiver(mHomeReceiver, mFilter);
        mIsShowing = true;
    }

    public void dismiss() {
        if (!mIsShowing || null == mScreen) {
            return;
        }
        mWm.removeView(mScreen);
        mCtx.unregisterReceiver(mHomeReceiver);
        mIsShowing = false;
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public VersionDialog<T> backDismiss(boolean enable) {
        mBackDismiss = enable;
        return this;
    }

    public VersionDialog<T> homeDismiss(boolean enable) {
        mHomeDismiss = enable;
        return this;
    }

    public VersionDialog<T> outSideDismiss(boolean enable) {
        mOutsideDismiss = enable;
        return this;
    }

    public VersionDialog<T> version(T version) {
        mVersion = version;
        return this;
    }

    private void dismissOutSide() {
        if (!mOutsideDismiss) {
            return;
        }
        dismiss();
    }

    private void dismissBack() {
        if (!mBackDismiss) {
            return;
        }
        dismiss();
    }

    private void dismissHome() {
        if (!mHomeDismiss) {
            return;
        }
        dismiss();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupScreen() {
        // 全屏半透明黑色背景
        mScreen = new RelativeLayout(mCtx);
        mScreen.setFocusableInTouchMode(true);
        mScreen.setBackgroundResource(R.drawable.bg_window_container_alpha_shape);

        mScreen.setOnTouchListener(new View.OnTouchListener() {
            boolean moved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moved = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!moved && !VHUtils.isViewEventRange(mContainer, event)) {
                            // 点击的是弹窗以外的界面
                            dismissOutSide();
                        }
                        moved = false;
                        break;
                }
                return true;
            }
        });

        mScreen.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {
                    dismissBack();
                    return false;
                }
                return true;
            }
        });

        // Dialog容器
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.leftMargin = VHUtils.dp2px(mCtx, 48);
        params.rightMargin = VHUtils.dp2px(mCtx, 48);
        mContainer = new CardView(mCtx);
        mContainer.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContainer.setElevation(VHUtils.dp2px(mCtx, 8));
            mContainer.setTranslationZ(VHUtils.dp2px(mCtx, 8));
        }

        mScreen.removeAllViews();
        mScreen.addView(mContainer);
    }

    private void setupView() {
        if (null == mProvider) {
            return;
        }

        mView = mProvider.getView(mCtx, mVersion, this);
        if (null == mView) {
            return;
        }
        VHUtils.removeParent(mView);

        mContainer.removeAllViews();
        mContainer.addView(mView);
        mContainer.postInvalidate();
    }

    public VersionDialog<T> setDialogViewProvider(DialogViewProvider<T> provider) {
        mProvider = provider;
        return this;
    }

    public interface DialogViewProvider<T> {
        View getView(Context ctx, T version, VersionDialog dialog);
    }

    private class HomeKeyReceiver extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                if (reason != null && SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // Home键
                    dismissHome();
                } else if (reason != null && SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 多任务按键
                    dismissHome();
                }
            }
        }
    }
}
