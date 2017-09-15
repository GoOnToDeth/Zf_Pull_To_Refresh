package com.wzf.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Toast;

import com.wzf.ptr.header.PtrHeader;
import com.wzf.ptr.listener.OnPtrListener;

/**
 * ===============================
 * 描    述：下拉刷新容器
 * 作    者：wzf
 * 创建日期：2017/8/15 17:53
 * ===============================
 */
public class PtrLinearLayout extends PtrLayout {

    public PtrLinearLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public PtrLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PtrLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private static final int STATUS_PULL_REFUSE = 1;        // 允许下拉刷新的状态
    private static final int STATUS_PREPARE_REFRESH = 2;    // 满足刷新高度状态(还未松开)
    private static final int STATUS_REFRESHING = 3;         // 正在刷新的状态

    private int mCurrentRefuseState = STATUS_PULL_REFUSE;          //默认下拉刷新状态

    private static final String TAG = "info1";

    private PtrHeader iPtrHeader;

    /************* 自定义属性 ****************/
    // 下拉的阻尼系数
    private float resistance;
    // 执行刷新的最低高度和当前Header高度的比例
    private float mRatioOfHeaderHeightToRefresh;
    // 指定刷新完成后Header收起
    private int mDurationCloseHeader;
    private boolean isEnablePtr;


    private View mHeaderView;
    private View mContentView;
    // Header高度
    private int mHeaderHeight;
    // 允许刷新的高度
    private int mRefuseHeight;
    // 子View是否已经滚动到顶部
    private boolean isTop = true;
    // 是否已经取消退出手势
    private boolean mHasSendCancelEvent;
    private Scroller mScroller;
    // 能识别的最小距离
    private static final int minDis = 0;
    // 按下时的y值
    private int yDown;
    // 上一次的y值
    private int lastX, lastY;
    private int extraDealt;
    // 当前MotionEvent是分发到子View还是当前View
    private boolean isDispatchChild;
    // 是否触碰屏幕
    private boolean isTouch;
    // 刷新结束时，是否正在触碰屏幕
    private boolean isTouchAtRefreshOver;
    // 是否需要开启横向和竖向距离判断
    private boolean isEnableDisVerification = false;

    private OnPtrListener mOnPtrListener;

    private void init(AttributeSet attrs) {
        mScroller = new Scroller(getContext());
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrLinearLayout);
        if (arr == null) return;
        resistance = arr.getFloat(R.styleable.PtrLinearLayout_ptr_resistance, 2.0f);
        mRatioOfHeaderHeightToRefresh = arr.getFloat(R.styleable.PtrLinearLayout_ptr_ratio_refuse_requirement, 1.0f);
        mDurationCloseHeader = arr.getInt(R.styleable.PtrLinearLayout_ptr_duration_to_close_header, 600);
        isEnablePtr = arr.getBoolean(R.styleable.PtrLinearLayout_ptr_pull_to_fresh, true);
        arr.recycle();
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() != 1) {
            throw new ArrayIndexOutOfBoundsException("child count only be one.");
        }
        if (iPtrHeader == null) {
            iPtrHeader = new DefaultHeader(getContext());
        }
        this.mHeaderView = iPtrHeader.getHeaderView(this);
        addView(mHeaderView, 0);
        this.mContentView = getChildAt(1);
        setContentViewEvent();
    }

    private void setContentViewEvent() {
        if (mContentView instanceof AbsListView) {
            ((AbsListView) mContentView).setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount == 0) {
                        isTop = true;
                        return;
                    }
                    if (firstVisibleItem == 0) {
                        View firstVisibleItemView = view.getChildAt(0);
                        if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                            isTop = true;
                            return;
                        }
                    }
                    isTop = false;
                }
            });
        } else {
            (mContentView).setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    isTop = v.getScrollY() == 0;
                }
            });
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mRefuseHeight = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int index = event.getActionIndex();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                this.isTouch = true;
                this.isTouchAtRefreshOver = false;
                this.yDown = y;
                extraDealt = this.mCurrentRefuseState == STATUS_REFRESHING ? mScroller.getFinalY() : 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(y - lastY) > Math.abs(x - lastX)) {
                    if (isTop) {
                        if ((y - lastY) > minDis) { // 下拉
                            return pullToBottom(event);
                        } else if ((y - lastY) < -minDis) {    // 上拉
                            if (pullToTop(event))
                                return true;
                            isDispatchChild = true;
                        }
                    } else {
                        isDispatchChild = true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                this.isTouch = false;
                if (this.mCurrentRefuseState != STATUS_REFRESHING
                        && -mScroller.getFinalY() >= mRefuseHeight
                        && !isTouchAtRefreshOver) {
                    startRefuse();
                } else if (this.mCurrentRefuseState == STATUS_REFRESHING) {
                    if (-mScroller.getFinalY() >= mHeaderHeight) {
                        scrollToRefusePos();
                    }
                } else {
                    resetScrollerPos();
                }
                mHasSendCancelEvent = false;
                Log.i(TAG, "up-->" + y);
                break;
        }
        this.lastX = x;
        this.lastY = y;
        return super.dispatchTouchEvent(event);
    }

    /**
     * 下拉操作
     *
     * @param event
     * @return
     */
    private boolean pullToBottom(MotionEvent event) {
        sendCancelEvent(event);
        int y = (int) event.getY();
        if (isDispatchChild && mCurrentRefuseState != STATUS_REFRESHING)
            this.yDown = y;
        float targetPos = -(y - yDown) / resistance + extraDealt;
        targetPos = targetPos > 0 ? 0 : targetPos;
        isDispatchChild = false;
        return scrollToPos((int) targetPos, y);
    }

    /**
     * 上拉操作
     *
     * @param event
     * @return
     */
    private boolean pullToTop(MotionEvent event) {
        int y = (int) event.getY();
        float pos = -(y - yDown) / resistance;
        float targetPos = pos + extraDealt;
        if (this.mCurrentRefuseState == STATUS_REFRESHING) {    // 刷新状态
            if (targetPos < 0) {
                sendCancelEvent(event);
                isDispatchChild = false;
                return scrollToPos((int) targetPos, y);
            } else {
                if (mScroller.getFinalY() != 0) {
                    isDispatchChild = false;
                    return scrollToPos(0, y);
                } else {
                    sendDownEvent(event);
                    isDispatchChild = true;
                }
            }
        } else {    // 未刷新状态
            if (targetPos < 0) {
                isDispatchChild = false;
                return scrollToPos((int) targetPos, y);
            } else {
                if (mScroller.getFinalY() != 0) {
                    isDispatchChild = false;
                    return scrollToPos(0, y);
                } else {
                    sendDownEvent(event);
                    isDispatchChild = true;
                }
            }
        }
        return false;
    }

    /**
     * 滑动到指定位置
     *
     * @param pos
     * @param curY
     * @return
     */
    private boolean scrollToPos(int pos, int curY) {
        smoothScrollTo(pos);
        this.lastY = curY;
        return true;
    }

    /**
     * 创建退出手势
     *
     * @param event
     */
    private void sendCancelEvent(MotionEvent event) {
        if (!mHasSendCancelEvent) {
            MotionEvent last = event;
            MotionEvent e = MotionEvent.obtain(last.getDownTime(),
                    last.getEventTime() + ViewConfiguration.getLongPressTimeout(),
                    MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
            super.dispatchTouchEvent(e);
            mHasSendCancelEvent = true;
        }
    }

    /**
     * 创建按下手势
     *
     * @param event
     */
    private void sendDownEvent(MotionEvent event) {
        if (mHasSendCancelEvent) {
            MotionEvent last = event;
            MotionEvent e = MotionEvent.obtain(last.getDownTime(),
                    last.getEventTime(),
                    MotionEvent.ACTION_DOWN, last.getX(), last.getY() - mHeaderHeight, last.getMetaState());
            super.dispatchTouchEvent(e);
            mHasSendCancelEvent = false;
        }
    }

    private void startRefuse() {
        scrollToRefusePos();
        if (this.mCurrentRefuseState == STATUS_REFRESHING) return;
        this.mCurrentRefuseState = STATUS_REFRESHING;
        if (this.mOnPtrListener != null)
            mOnPtrListener.onRefresh(this);
        iPtrHeader.onBeginRefresh(this);
    }

    private void scrollToRefusePos() {
        smoothScrollTo(-mHeaderHeight);
    }

    private void resetScrollerPos() {
        this.mCurrentRefuseState = STATUS_PULL_REFUSE;
        this.extraDealt = 0;
        if (mScroller.getFinalY() != 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    iPtrHeader.onResetHeader();
                }
            }, 250);
            smoothScrollTo(0);
        }
    }

    private void smoothScrollBy(int dy) {
        mScroller.startScroll(0, mScroller.getFinalY(), 0, dy);
        invalidate();
    }

    private void smoothScrollTo(int fy) {
        int dy = fy - mScroller.getFinalY();
        if (dy == 0) return;
        smoothScrollBy(dy);
    }

    public void completeRefuse() {
        this.iPtrHeader.onCompleteRefuse(this);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isTouchAtRefreshOver = isTouch;
                if (!isTouchAtRefreshOver)
                    resetScrollerPos();
                mCurrentRefuseState = STATUS_PULL_REFUSE;
            }
        }, mDurationCloseHeader);
    }

    public void setOnPtrListener(OnPtrListener onPtrListener) {
        this.mOnPtrListener = onPtrListener;
    }

    public void setHeader(PtrHeader iPtrHeader) {
        this.iPtrHeader = iPtrHeader;
    }

    public void refuse() {
        if (this.mCurrentRefuseState != STATUS_REFRESHING) {
            startRefuse();
        }
    }

    public void setEnableDisVerification(boolean enableDisVerification) {
        isEnableDisVerification = enableDisVerification;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            iPtrHeader.onPositionChange(mHeaderHeight, mRefuseHeight, mScroller.getCurrY(), isTouch);
            if (this.mCurrentRefuseState != STATUS_REFRESHING) {
                if (-mScroller.getFinalY() >= mRefuseHeight
                        && this.mCurrentRefuseState != STATUS_PREPARE_REFRESH
                        && !isTouchAtRefreshOver) {
                    iPtrHeader.onPrepareRefreshToggle(true);
                    this.mCurrentRefuseState = STATUS_PREPARE_REFRESH;
                } else if (-mScroller.getFinalY() < mRefuseHeight
                        && this.mCurrentRefuseState == STATUS_PREPARE_REFRESH
                        && !isTouchAtRefreshOver) {
                    iPtrHeader.onPrepareRefreshToggle(false);
                    this.mCurrentRefuseState = STATUS_PULL_REFUSE;
                }
            }
            postInvalidate();
        }
    }
}