package de.teufelslochschradde.schraddeftp.image_mani;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by dirk on 15.07.2017.
 */

public class ImageAdapter extends BaseAdapter {

    private ArrayList<Uri> imageUriList;

    private ArrayList<Bitmap> imageBmpList;

    private Context mContext;
    private int imageWidth;

    public ImageAdapter(Context c, int width) {
        this.mContext = c;
//        imageUriList = new ArrayList<>();
        imageBmpList =  new ArrayList<>();
        imageWidth = width/3;
    }

    @Override
    public int getCount() {
//        return imageUriList.size();
        return imageBmpList.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(null);
        imageView.setImageBitmap(imageBmpList.get(position));
//        imageView.setImageURI(imageUriList.get(position));

        return imageView;
    }

    public ArrayList<Bitmap> getImageBmpList() {
        return imageBmpList;
    }
}
