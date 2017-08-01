package com.jmgzs.qrcode.picture;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.GlideApp;
import com.jmgzs.qrcode.base.GlideRequest;

import java.util.List;

/**
 * Created by hupei on 2016/7/14.
 */
class PickPictureTotalAdapter extends BaseAdapter {

    private List<Picture> pathsList;
    private Context ct;
    private GlideRequest<Drawable> request;


    PickPictureTotalAdapter(Context context, List<Picture> datas) {
        this.ct = context;
        this.pathsList = datas;
        request = GlideApp.with(ct).asDrawable();

    }

    @Override
    public int getCount() {
        return pathsList == null ? 0 : pathsList.size();
    }

    @Override
    public Picture getItem(int position) {
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
            convertView = View.inflate(ct, R.layout.activity_pick_picture_total_list_item, null);
            h = new Holder(convertView);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }
        Picture p = getItem(position);
        h.title.setText(p.getFolderName());
        h.count.setText("(" + Integer.toString(p.getPictureCount()) + ")");
        request.load(p.getTopPicturePath()).into(h.image);
        return convertView;
    }

    private class Holder {
        TextView title;
        TextView count;
        ImageView image;

        Holder(View v) {
            title = (TextView) v.findViewById(R.id.pick_picture_total_list_item_group_title);
            count = (TextView) v.findViewById(R.id.pick_picture_total_list_item_group_count);
            image = (ImageView) v.findViewById(R.id.pick_picture_total_list_item_group_image);
        }
    }
}
