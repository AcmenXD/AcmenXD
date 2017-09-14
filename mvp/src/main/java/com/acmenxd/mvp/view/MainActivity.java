package com.acmenxd.mvp.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acmenxd.logger.Logger;
import com.acmenxd.mvp.R;
import com.acmenxd.mvp.base.BaseActivity;
import com.acmenxd.mvp.base.EventBusHelper;
import com.acmenxd.mvp.model.response.TestEntity;
import com.acmenxd.mvp.utils.ViewUtils;
import com.acmenxd.mvp.view.test.TestActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/12/16 15:34
 * @detail 主Activity
 */
public class MainActivity extends BaseActivity {
    private ArrayList<DataInfo> datas;
    private ListView lv_main;
    private MyMainAdapter mMyMainAdapter;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.w("App进入MainActivity!");
        setContentView(R.layout.activity_main);
        setTitleView(R.layout.layout_title);
        ViewUtils.initTitleView(getTitleView(), getBundle().getString("title", "个人程序列表"), null);
        initData();

        lv_main = (ListView) findViewById(R.id.lv_main);
        mMyMainAdapter = new MyMainAdapter();
        lv_main.setAdapter(mMyMainAdapter);
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> pAdapterView, View pView, int pI, long pL) {
                Bundle bundle = new Bundle();
                bundle.putString("title", datas.get(pI).name);
                startActivity(datas.get(pI).path, bundle);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusHelper.post(new TestEntity());
    }

    @Subscribe
    public void showTestEntity(TestEntity pTestEntity) {
        //Toaster.show("Debug:" + AppConfig.DEBUG);
    }

    private void initData() {
        datas = new ArrayList<>();
        datas.add(new DataInfo("成语接龙专用", "", TestActivity.class));
    }

    private class DataInfo {
        public DataInfo(String pName, String pDetail, Class pPath) {
            this.name = pName;
            this.detail = pDetail;
            this.path = pPath;
        }

        String name;
        String detail;
        Class path;
    }

    private class MyMainAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int pI) {
            return datas.get(pI);
        }

        @Override
        public long getItemId(int pI) {
            return pI;
        }

        @Override
        public View getView(int pI, View pView, ViewGroup pViewGroup) {
            ViewHolder vh;
            if (pView == null) {
                pView = View.inflate(MainActivity.this, R.layout.activity_main_item, null);
                vh = new ViewHolder();
                vh.tv_name = (TextView) pView.findViewById(R.id.tv_name);
                vh.tv_detail = (TextView) pView.findViewById(R.id.tv_detail);
                pView.setTag(vh);
            } else {
                vh = (ViewHolder) pView.getTag();
            }
            DataInfo bean = datas.get(pI);
            vh.tv_name.setText(bean.name);
            vh.tv_detail.setText(bean.detail);
            return pView;
        }

        class ViewHolder {
            TextView tv_name;
            TextView tv_detail;
        }
    }
}
