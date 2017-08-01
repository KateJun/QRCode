package com.jmgzs.qrcode.picture;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.GlideApp;
import com.jmgzs.qrcode.base.GlideRequest;

import java.util.List;

import static java.lang.System.load;

/**
 * 照片浏览
 * Created by hupei on 2016/7/7.
 */
class PickPictureAdapter extends BaseAdapter {

    private List<String> pathsList;
    private Context ct;

    private GlideRequest<Drawable> request;

    PickPictureAdapter(Context context, List<String> datas) {
        this.ct = context;
        this.pathsList = datas;
        request = GlideApp.with(ct).asDrawable();
    }


    @Override
    public int getCount() {
        return pathsList == null ? 0 : pathsList.size();
    }

    @Override
    public String getItem(int position) {
        return pathsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = View.inflate(ct, R.layout.activity_pick_picture_grid_item, null);
            h = new Holder(convertView);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }
        request.load(getItem(position)).into(h.image);
        return convertView;
    }

    private class Holder {
        public ImageView image;

        Holder(View v) {
            image = (ImageView) v.findViewById(R.id.activity_pick_picture_grid_item_image);
        }
    }
}
