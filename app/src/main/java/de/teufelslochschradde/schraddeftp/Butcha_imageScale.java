package de.teufelslochschradde.schraddeftp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dirk on 08.05.2017.
 */

public class Butcha_imageScale {

    String LOG_TAG = "Butcha_ImageScale";

    int resX;
    int resY;

    File tmpfile;
    FileOutputStream fOut;

    public Butcha_imageScale(int resX, int resY){
        this.resX = resX;
        this.resY = resY;
    }

    private FileOutputStream getBitmapFromUri(Uri uri, Context ConTe) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                ConTe.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calculateInSampleSize(options, this.resX, this.resY);
        options.inJustDecodeBounds = false;

        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        image = Bitmap.createScaledBitmap(image, resX, resY, true);

        try {
            tmpfile = File.createTempFile("tmpImgFile", null, ConTe.getCacheDir());
            fOut = new FileOutputStream(tmpfile);
            image.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
            image.recycle();
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "Failed to save/resize image due to: " + e.toString());
        }

        parcelFileDescriptor.close();
        return fOut;
    }



    public FileOutputStream resize(Uri src_uri, Context ConTe) throws IOException {

        return getBitmapFromUri(src_uri, ConTe);

    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
