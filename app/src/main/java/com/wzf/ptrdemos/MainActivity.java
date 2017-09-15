package com.wzf.ptrdemos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String[] datas = {"ListView", "GridView", "ScrollView",
            "TextView", "WebView", "外部嵌套ViewPager"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.lv_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PtrActivity.class);
        switch (position) {
            case 0:
                intent.putExtra("layout", R.layout.ptr_listview);
                break;
            case 1:
                intent.putExtra("layout", R.layout.ptr_gridview);
                break;
            case 2:
                intent.putExtra("layout", R.layout.ptr_scrollerview);
                break;
            case 3:
                intent.putExtra("layout", R.layout.ptr_textview);
                break;
            case 4:
                intent.putExtra("layout", R.layout.ptr_webview);
                break;
            case 5:
                intent.putExtra("layout", R.layout.ptr_viewpager);
                break;
        }
        startActivity(intent);
    }
}
