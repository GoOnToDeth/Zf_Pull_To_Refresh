# Zf_Pull_To_Refresh
继承自ViewGroup, PtrLayout控制Header和Conternt的布局，PtrLinearLayout 继承自PtrLayout，处理手势冲突和滑动，基本支持所有View。

PtrLinearLayout同时也支持自定义Header样式，下面详细介绍。

 
 ## APK下载
 
* [下载地址](https://raw.githubusercontent.com/GoOnToDeth/Zf_Pull_To_Refresh/master/zf_ptr_demo.apk) 
* 二维码下载

![](https://github.com/GoOnToDeth/Zf_Pull_To_Refresh/blob/master/imgaes/qrcode.png)
 
## 效果图
* AbsListView

![](https://github.com/GoOnToDeth/Zf_Pull_To_Refresh/blob/master/imgaes/ptr_normal.gif)

* ViewPager

![](https://github.com/GoOnToDeth/Zf_Pull_To_Refresh/blob/master/imgaes/ptr_viewpager.gif)

# 使用方式

## 自定义属性
有3个参数可配置:
* ptr_resistance(下拉阻尼系数)

  默认: 2.0f，越大，感觉下拉时越吃力。
* ptr_ratio_refuse_requirement（执行刷新的最低高度和当前Header高度的比例）

   默认: 1.0f，比如，当前header高度是200px，但是我们希望下拉300px的时候才刷新，那么改参数值就是300/200=1.5f.

* ptr_duration_to_close_header(刷新完成后Header延迟多长时间在收起)

  默认：600ms，值越大，刷新完成后延迟时间越长。
  
  ## xml中配置示例
 
``` xml
    <com.wzf.ptr.PtrLinearLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:ptr_duration_to_close_header="1000"
        app:ptr_ratio_refuse_requirement="1.5"
        app:ptr_resistance="2.5">

        <ListView
            android:id="@+id/listview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </com.wzf.ptr.PtrLinearLayout>
```

## Java代码设置

``` JAVA
   ptrLinearLayout.setResistance(2.5f);
   ptrLinearLayout.setRatioOfHeaderHeightToRefresh(1.2f);
   ptrLinearLayout.setDurationCloseHeader(1000);
```

## 自定义Header
如果没有设置header，该库有个默认的Header类DefaultHeader，需要自定义Header时候，必须实现PtrHeader接口，并调用setHeader方法。
``` Java
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
```
> 具体示例可以参考DefaultHeader.java文件

## 处理刷新
* 自动刷新
``` Java
  ptrLinearLayout.refuse();
```
* 刷新回调
```Java
      // 触发下拉刷新
      ptrLinearLayout.setOnPtrListener(new OnPtrListener() {
            @Override
            public void onRefresh(final PtrLinearLayout view) {
                // 模拟业务处理延迟
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.completeRefuse();
                    }
                }, 3000);
            }
        });
```

> 注意：刷新完成后必须调用 view.completeRefuse(); 以通知UI视图刷新。

# 常见问题

 * 处理ViewPager手势冲突，
  ``` Java
   ptrLinearLayout.setEnableDisVerification(true);
  ```
 
 # 联系方式和问题建议
 
 E-mail: wangzf0342@qq.com
 
 Blog: http://blog.csdn.net/coder_giser

