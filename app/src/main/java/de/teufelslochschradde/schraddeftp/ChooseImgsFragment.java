package de.teufelslochschradde.schraddeftp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.teufelslochschradde.schraddeftp.ftp_com.Butcha_FTP;
import de.teufelslochschradde.schraddeftp.image_mani.ImageAdapter;

import static android.app.Activity.RESULT_OK;
import static de.teufelslochschradde.schraddeftp.R.id.fab;

/**
 * Created by dirk on 01.07.2017.
 */

public class ChooseImgsFragment extends Fragment {

    private final int INT_RESULT_OK = 0;
    private final int INT_REQ_MULTI_IMG = 0;

    private static final int THUMBNAIL_SIZE = 200;


    private String LOG_INFO = "ChImgsFrag";

    MainActivity mthisActivity;
    LinearLayout overlayLoading;

    Butcha_FTP mFtpClient;
    ArrayList<Uri> imageList = new ArrayList<>();

    private ImageAdapter gridImgAdapter;

    private FloatingActionButton fab;
    private TextView selectedFolderInfo;

    private GridView mImgGrid;

    private int dispWidth;
    private int dispHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup mRootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_images, container, false);

        // Create an ArrayAdapter from List Years


        mthisActivity = (MainActivity) getActivity();
        mFtpClient = mthisActivity.getFtpClient();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        dispHeight = displayMetrics.heightPixels;
        dispWidth = displayMetrics.widthPixels;

        gridImgAdapter = new ImageAdapter(getContext(), dispWidth);

        mImgGrid = (GridView) mRootView.findViewById(R.id.grid_view_img);
        mImgGrid.setAdapter(gridImgAdapter);

        mImgGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridImgAdapter.getImageBmpList().remove(position);
                gridImgAdapter.notifyDataSetChanged();
                return false;
            }
        });

        selectedFolderInfo = (TextView) mRootView.findViewById(R.id.selected_folders);
        overlayLoading = (LinearLayout) mthisActivity.findViewById(R.id.overlay_loading);
        return mRootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        selectedFolderInfo.setText(mthisActivity.getSelectedYear() + "/" + mthisActivity.getSelectedEvent());
        fab = (FloatingActionButton) mthisActivity.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }


    private void openGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "android.intent.action.SEND_MULTIPLE"), INT_REQ_MULTI_IMG);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INT_REQ_MULTI_IMG:
                if (resultCode == RESULT_OK) {
                    int iPicCount = data.getClipData().getItemCount();
                    for (int i = 0; i < iPicCount; i++) {
                        try {
                            gridImgAdapter.getImageBmpList().add(getThumbnail(data.getClipData().getItemAt(i).getUri()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    gridImgAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }




    public void decodeUri(Uri uri, ImageView imageview) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContext().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            imageview.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            // handle errors
        } catch (IOException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContext().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

}
