package com.jmgzs.qrcode.picture;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 手机图片列表中的详细
 * Created by hupei on 2016/7/7.
 */
public class PickPictureActivity extends BaseActivity {
    private GridView mGridView;
    private List<String> mList;//此相册下所有图片的路径集合
    private PickPictureAdapter mAdapter;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_pick_picture;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = getView(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mGridView = (GridView) findViewById(R.id.child_grid);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setResult(mList.get(position));
            }
        });
        processExtraData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        TextView title = getView(R.id.toolbar_title);
        title.setVisibility(View.VISIBLE);
        title.setText(extras.getString("title"));

        mList = extras.getStringArrayList("data");
        if (mList != null && mList.size() > 1) {
            SortPictureList sortList = new SortPictureList();
            Collections.sort(mList, sortList);
        }
        mAdapter = new PickPictureAdapter(this, mList);
        mGridView.setAdapter(mAdapter);
    }

    private void setResult(String picturePath) {
        Intent intent = new Intent();
        intent.putExtra(PickPictureTotalActivity.EXTRA_PICTURE_PATH, picturePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public static void gotoActivity(Activity activity, ArrayList<String> childList, String title) {
        Intent intent = new Intent(activity, PickPictureActivity.class);
        intent.putStringArrayListExtra("data", childList);
        intent.putExtra("title", title);
        activity.startActivityForResult(intent, PickPictureTotalActivity.REQUEST_CODE_SELECT_ALBUM);
    }
}
