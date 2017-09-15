package com.wzf.ptr.listener;

import com.wzf.ptr.PtrLinearLayout;

/**
 * ===============================
 * 描    述：事件回调
 * 作    者：wzf
 * 创建日期：2017/9/6 17:56
 * ===============================
 */
public interface OnPtrListener {

    /**
     * 执行下拉刷新
     *
     * @param view
     */
    void onRefresh(PtrLinearLayout view);
}
