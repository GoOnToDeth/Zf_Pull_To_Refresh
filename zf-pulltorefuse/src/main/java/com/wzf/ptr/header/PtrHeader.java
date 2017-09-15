package com.wzf.ptr.header;

import android.view.View;
import com.wzf.ptr.PtrLayout;
import com.wzf.ptr.PtrLinearLayout;

/**
 * ===============================
 * 描    述：下拉刷新Header必须实现接口
 * 作    者：wzf
 * 创建日期：2017/9/12 10:48
 * ===============================
 */
public interface PtrHeader {

    View getHeaderView(PtrLayout root);

    /**
     * 开始刷新
     *
     * @param view
     */
    void onBeginRefresh(PtrLinearLayout view);

    /**
     * 刷新完成
     *
     * @param view
     */
    void onCompleteRefuse(PtrLinearLayout view);

    void onResetHeader();

    /**
     * 改变高度时，满足刷新状态
     *
     * @param isRequireRefresh 是否满足刷新要求
     */
    void onPrepareRefreshToggle(boolean isRequireRefresh);

    /**
     * Header位置变化回调
     *
     * @param mHeaderHeight Header高度
     * @param mRefuseHeight 允许刷新的下拉高度
     * @param isTouch       是否触碰屏幕
     */
    void onPositionChange(int mHeaderHeight, int mRefuseHeight, int currentPosition, boolean isTouch);
}
