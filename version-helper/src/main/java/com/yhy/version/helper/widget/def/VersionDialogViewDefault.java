package com.yhy.version.helper.widget.def;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhy.version.helper.R;
import com.yhy.version.helper.widget.NumberProgressBar;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-13 11:05
 * version: 1.0.0
 * desc   :
 */
public class VersionDialogViewDefault extends LinearLayout {

    private TextView tvTitle;
    private LinearLayout llContent;
    private TextView tvDesc;
    private TextView tvTime;
    private NumberProgressBar npbProgress;
    private LinearLayout llOperation;
    private TextView tvCancel;
    private TextView tvConfirm;

    public VersionDialogViewDefault(Context context) {
        this(context, null);
    }

    public VersionDialogViewDefault(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VersionDialogViewDefault(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_dialog_version_update_default, this);
        tvTitle = view.findViewById(R.id.tv_title);
        llContent = view.findViewById(R.id.ll_content);
        tvDesc = view.findViewById(R.id.tv_desc);
        tvTime = view.findViewById(R.id.tv_time);
        npbProgress = view.findViewById(R.id.npb_progress);
        llOperation = view.findViewById(R.id.ll_operation);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvConfirm = view.findViewById(R.id.tv_confirm);

        npbProgress.setVisibility(GONE);
        llOperation.setVisibility(VISIBLE);
    }

    public VersionDialogViewDefault setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public VersionDialogViewDefault setDesc(String title) {
        tvDesc.setText(title);
        return this;
    }

    public VersionDialogViewDefault setTime(String title) {
        tvTime.setText(title);
        return this;
    }

    public VersionDialogViewDefault setCancel(String title, OnClickListener listener) {
        tvCancel.setText(title);
        tvCancel.setOnClickListener(listener);
        return this;
    }

    public VersionDialogViewDefault setConfirm(String title, OnClickListener listener) {
        tvConfirm.setText(title);
        tvConfirm.setOnClickListener(listener);
        return this;
    }

    public VersionDialogViewDefault startDownload() {
        if (npbProgress.getVisibility() == VISIBLE) {
            return this;
        }

        llOperation.setVisibility(GONE);
        npbProgress.setVisibility(VISIBLE);
        return this;
    }

    public VersionDialogViewDefault progress(float progress) {
        npbProgress.setProgress(progress);
        return this;
    }

    public float getProgress() {
        return npbProgress.getProgress();
    }
}
