package com.wzf.ptr.header;

import android.view.View;

import com.wzf.ptr.PtrLayout;

/**
 * ===============================
 * 描    述：
 * 作    者：wzf
 * 创建日期：2017/9/18 9:40
 * ===============================
 */
public interface PtrSpecialView {

    /**
     * 加载错误页面
     *
     * @param root
     * @return
     */
    View geErrorView(PtrLayout root);

    /**
     * 加载空数据页面
     *
     * @param root
     * @return
     */
    View geEmptyView(PtrLayout root);
}
