package com.example.practice.ui.main.Second;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.practice.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext); //항목의 모양
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300)); //이미지 크기 조정
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); //이미지 가운데 설정
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            imageView.setPadding(8, 8, 8, 8);   //여백 설정
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);

        return imageView;
    }

    class ViewHolder{
        ImageView imageView;
    }
    // references to our images
    public Integer[] mThumbIds = {
            R.drawable.img01, R.drawable.img02,
            R.drawable.img03, R.drawable.img04,
            R.drawable.img05, R.drawable.img06,
            R.drawable.img07, R.drawable.img08,
            R.drawable.img09, R.drawable.img10,
            R.drawable.img11, R.drawable.img12,
            R.drawable.img13, R.drawable.img14,
            R.drawable.img15, R.drawable.img16,
            R.drawable.img17, R.drawable.img18,
            R.drawable.img19, R.drawable.img20
    };
}
