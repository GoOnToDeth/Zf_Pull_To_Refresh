package com.wzf.ptrdemos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wzf.ptr.PtrLinearLayout;
import com.wzf.ptr.listener.OnPtrListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：wzf
 * 创建日期：2017/9/13 16:04
 * ===============================
 */
public class PtrActivity extends AppCompatActivity {

    PtrLinearLayout ptrLinearLayout;
    List<String> mStringList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) this.finish();
        int layoutId = intent.getIntExtra("layout", 0);
        setContentView(layoutId);

        ptrLinearLayout = (PtrLinearLayout) findViewById(R.id.container);
        switch (layoutId) {
            case R.layout.ptr_listview:
                setListView();
                break;
            case R.layout.ptr_gridview:
                setGridView();
                break;
            case R.layout.ptr_scrollerview:
                setScrollView();
                break;
            case R.layout.ptr_textview:
                setTextView();
                break;
            case R.layout.ptr_webview:
                setWebView();
                break;
            case R.layout.ptr_viewpager:
                setOutSideViewPager();
                break;
            case R.layout.ptr_error_empty:
                setErrorEmnpt();
                break;
        }
    }

    private void setListView() {
        final ListView listView = (ListView) findViewById(R.id.listview);
        ptrLinearLayout.setPtrSpecialView(new PtrSpecialViews(this));
        ptrLinearLayout.setOnPtrListener(new OnPtrListener() {
            @Override
            public void onRefresh(final PtrLinearLayout view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.completeRefuse();
                        mStringList.clear();
                        bindAdapter(listView);
                    }
                }, 3000);
            }
        });
        ptrLinearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLinearLayout.refuse();
            }
        }, 200);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PtrActivity.this, "onItemClick---->>" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGridView() {
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        ptrLinearLayout.setOnPtrListener(new OnPtrListener() {
            @Override
            public void onRefresh(final PtrLinearLayout view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.completeRefuse();
                        mStringList.clear();
                        bindAdapter(gridView);
                    }
                }, 3000);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PtrActivity.this, "onItemClick---->>" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setScrollView() {
        setRefuseListener();
        ptrLinearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLinearLayout.refuse();
            }
        }, 300);
    }

    private void setTextView() {
        setRefuseListener();
    }

    private void setWebView() {
        final WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        setRefuseListener();
        webView.loadUrl("http://qt.qq.com/php_cgi/news/php/varcache_article.php?id=37076&areaid=1");
    }

    private void setOutSideViewPager() {
        setRefuseListener();
        ptrLinearLayout.setEnableDisVerification(true);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_outside);

        final List<View> viewList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            viewList.add(getChilds(i));
        }
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = viewList.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView(viewList.get(position));
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }

    private int loadViewCode;

    private void setErrorEmnpt() {
        final ListView listView = (ListView) findViewById(R.id.listview);
        ptrLinearLayout.setPtrSpecialView(new PtrSpecialViews(this));
        ptrLinearLayout.setOnPtrListener(new OnPtrListener() {
            @Override
            public void onRefresh(final PtrLinearLayout view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.completeRefuse();
                        switch (loadViewCode) {
                            case 0:
                                ptrLinearLayout.showContentView();
                                mStringList.clear();
                                bindAdapter(listView);
                                break;
                            case 1:
                                ptrLinearLayout.showErrorView();
                                break;
                            case 2:
                                ptrLinearLayout.showEmptyView();
                                break;
                        }
                    }
                }, 1000);
            }
        });
        ptrLinearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLinearLayout.refuse();
            }
        }, 200);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PtrActivity.this, "onItemClick---->>" + position, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadViewCode = 1;
                ptrLinearLayout.refuse();
            }
        });
        findViewById(R.id.btn_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadViewCode = 2;
                ptrLinearLayout.refuse();
            }
        });
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadViewCode = 0;
                ptrLinearLayout.refuse();
            }
        });
    }

    private void setRefuseListener() {
        ptrLinearLayout.setOnPtrListener(new OnPtrListener() {
            @Override
            public void onRefresh(final PtrLinearLayout view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.completeRefuse();
                        Toast.makeText(PtrActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });
    }

    private View getChilds(int position) {
        int bgColor = Color.parseColor("#FF7F00");
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView textView = new TextView(PtrActivity.this);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(bgColor);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(24);
        textView.setText("这是第" + position + "个页面");
        textView.setClickable(true);
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    private void bindAdapter(AbsListView absListView) {
        mStringList.addAll(DataSource.getInstance().getSource());
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStringList);
            absListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
