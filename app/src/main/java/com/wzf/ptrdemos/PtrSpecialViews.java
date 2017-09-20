package com.wzf.ptrdemos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wzf.ptr.PtrLayout;
import com.wzf.ptr.header.PtrSpecialView;

/**
 * ===============================
 * 描    述：
 * 作    者：wzf
 * 创建日期：2017/9/18 10:29
 * ===============================
 */
public class PtrSpecialViews implements PtrSpecialView {

    private Context mContext;

    public PtrSpecialViews(Context context) {
        mContext = context;
    }

    @Override
    public View geErrorView(PtrLayout root) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ptr_error_view, root, false);
        return view;
    }

    @Override
    public View geEmptyView(PtrLayout root) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ptr_empty_view, root, false);
        return view;
    }
}
