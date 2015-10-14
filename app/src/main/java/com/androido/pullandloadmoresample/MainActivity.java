package com.androido.pullandloadmoresample;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FootUpdate.LoadMore{

    private static final String[][] names = new String[][]{
            {"加拿大","瑞典","澳大利亚","瑞士","新西兰","挪威","丹麦","芬兰","奥地利","荷兰","德国","日本","比利时","意大利","英国"},
            {"德国","西班牙","爱尔兰","法国","葡萄牙","新加坡","希腊","巴西","美国","阿根廷","波兰","印度","秘鲁","阿联酋","泰国"},
            {"智利","波多黎各","南非","韩国","墨西哥","土耳其","埃及","委内瑞拉","玻利维亚","乌克兰"},
            {"以色列","海地","中国","沙特阿拉伯","俄罗斯","哥伦比亚","尼日利亚","巴基斯坦","伊朗","伊拉克"}
    };
    private Handler handler;
    private ArrayList<String> msgs;
    private int pageId = -1;
    private MyAdapter adapter;
    public FootUpdate mFootUpdate;
    @ViewById
    ListView mListview;
    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @AfterViews
    void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        handler = new Handler();
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_icon_color);
        swipeRefreshLayout.setOnRefreshListener(this);
        mFootUpdate = new FootUpdate();
        mFootUpdate.init(mListview, LayoutInflater.from(this), this);
        msgs = new ArrayList<String>();
        adapter = new MyAdapter(this);
        mListview.setAdapter(adapter);
        refresh(); // 主动下拉刷新
    }


    private void refresh(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(swipeRefreshLayout != null){
                    swipeRefreshLayout.setRefreshing(false);
                }
                int rand = (int) (Math.random() * 2); // 随机数模拟成功失败。这里从有数据开始。
                if (rand == 0 || pageId == -1) {
                    pageId=0;
                    msgs = new ArrayList<String>();
                    for (String name : names[0]) {
                        msgs.add(name);
                    }
                    adapter.notifyDataSetChanged();
                    mFootUpdate.dismiss();//加载成功
                } else {
                    mFootUpdate.showFail();//加载失败
                }
            }
        }, 2 * 1000);
    }

    @Override
    public void onRefresh() {
        pageId = -1;
        refresh();
    }

    @Override
    public void loadMore() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int rand = (int) (Math.random() * 2); // 随机数模拟成功失败。这里从有数据开始。
                if (rand == 0) {
                    pageId++;
                    for (String name : names[0]) {
                        msgs.add(name);
                    }
                    adapter.notifyDataSetChanged();
                    mFootUpdate.dismiss();//加载成功
                } else {
                    mFootUpdate.showFail();//加载失败
                }
            }
        }, 2 * 1000);
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        public MyAdapter(Context context){
            mContext = context;
        }
        @Override
        public int getCount() {
            return msgs==null ? 0 : msgs.size();
        }
        @Override
        public Object getItem(int position) {
            return msgs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if(convertView==null) {
                textView = (TextView) getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            }else{
                textView = (TextView) convertView;
            }
            textView.setText(msgs.get(position));
            if(msgs.size() - position <= 1){
                ((MainActivity)mContext).loadMore();
                ((MainActivity)mContext).mFootUpdate.showLoading();
            }
            return textView;
        }
    }
}
