package com.acmenxd.mvp.view.test;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.acmenxd.frame.utils.Utils;
import com.acmenxd.logger.Logger;
import com.acmenxd.mvp.R;
import com.acmenxd.mvp.base.BaseActivity;
import com.acmenxd.recyclerview.adapter.AdapterUtils;
import com.acmenxd.recyclerview.adapter.SimpleAdapter;
import com.acmenxd.recyclerview.decoration.LinearLayoutDecoration;
import com.acmenxd.recyclerview.delegate.ViewHolder;
import com.acmenxd.recyclerview.listener.AddItemListener;
import com.acmenxd.recyclerview.listener.ItemCallback;
import com.acmenxd.toaster.Toaster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/9/13 18:02
 * @detail something
 */
public class TestActivity extends BaseActivity {
    public static class TestBean implements Serializable {

        /**
         * status : 0
         * msg : ok
         * result : [{"name":"昂藏七尺"},{"name":"不管三七二十一"},{"name":"才高七步"},{"name":"打蛇打七寸"},{"name":"横七竖八"},{"name":"夹七夹八"},{"name":"九宗七祖"},{"name":"救人一命，胜造七级浮屠"},{"name":"开门七件事"},{"name":"零七八碎"},{"name":"乱七八遭"},{"name":"乱七八糟"},{"name":"七病八倒"},{"name":"七病八痛"},{"name":"七步八叉"}]
         */
        public String status;
        public String msg;
        public List<Result> result;

        public static class Result {
            /**
             * name : 昂藏七尺
             */
            public String name;
        }
    }

    public interface Test {
        @FormUrlEncoded
        @POST()
        Call<TestBean> post(@Url String url, @Field("appkey") String appkey, @Field("keyword") String keyword);
    }

    private RecyclerView rv;
    private SimpleAdapter mAdapter;
    private ClipboardManager clipboardManager;
    private String url = "http://api.jisuapi.com/chengyu/search";
    private String appkey = "af46e44624f13052";
    private String str;
    private List<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        rv = getView(R.id.activity_test_rv);
        //设置布局管理器
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(OrientationHelper.VERTICAL);
        rv.setLayoutManager(manager1);
        //设置分隔线
        rv.addItemDecoration(new LinearLayoutDecoration(this));
        new AddItemListener(rv, new ItemCallback() {
            @Override
            public void onClick(@NonNull RecyclerView.ViewHolder viewHolder, @IntRange(from = 0) int dataPosition) {
                clipboardManager.setText(datas.get(dataPosition));
            }
        });
        // 设置Adapter
        mAdapter = new SimpleAdapter<String>(this, rv, R.layout.activity_test_item, datas) {
            @Override
            public void convert(ViewHolder viewHolder, String item, int dataPosition) {
                TextView tv = viewHolder.getView(R.id.activity_test_item_tv);
                tv.setText(item);
            }
        };
        // 设置Adapter
        rv.setAdapter(mAdapter);
    }

    /**
     * 获取剪切板内容
     */
    public void getClipBoardContent() {
        str = "";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager != null) {
                    if (clipboardManager.getText() != null) {
                        str = clipboardManager.getText().toString();
                    }
                }
            }
        });
    }

    /**
     * 更新Adapter
     */
    public void refreshAdapter() {
        /**
         * 在Adapter.onBindViewHolder()中调用notifyDataSetChanged()会使程序崩溃
         * mEmptyWarpper.notifyDataSetChanged();
         */
        mAdapter.setDatas(datas);
        AdapterUtils.notifyDataSetChanged(rv, mAdapter);
    }

    /**
     * 查询-根据剪切板
     */
    public void queryClick(View view) {
        datas.clear();
        getClipBoardContent();
        str = str.trim();
        Logger.e(str);
        if (!Utils.isEmpty(str)) {
            final String tempStr = String.valueOf(str.charAt(str.length() - 1));
            request(TestActivity.Test.class).post(url, appkey, tempStr).enqueue(new BindCallback<TestBean>() {
                @Override
                public void succeed(@NonNull TestBean pData) {
                    if (pData != null && pData.result != null && pData.result.size() > 0) {
                        for (int i = 0; i < pData.result.size(); i++) {
                            if (pData.result.get(i).name.startsWith(tempStr)) {
                                datas.add(pData.result.get(i).name);
                            }
                        }
                    }
                    refreshAdapter();
                    Toaster.show("请求完成");
                }
            });
        }
    }
}
