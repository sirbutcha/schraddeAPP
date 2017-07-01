package de.teufelslochschradde.schraddeftp;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import de.teufelslochschradde.schraddeftp.ftp_com.FTP_Task;

/**
 * Created by dirk on 12.06.2017.
 */

public class ChooseYearFragment extends Fragment {

    MainActivity mthisActivity;

    FTP_Task mftpTask;
    ListView listV_years;

    ArrayAdapter<String> mArrayAdapter;
    LinearLayout overlayLoading;
    // Create a List from String Array elements
    final ArrayList<String> filesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup mRootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_yearlist, container, false);

        // Create an ArrayAdapter from List Years
        mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, filesList);

        mthisActivity = (MainActivity) getActivity();

        overlayLoading = (LinearLayout) mthisActivity.findViewById(R.id.overlay_loading);

        listV_years = (ListView) mRootView.findViewById(R.id.listV_years);
        listV_years.setAdapter(mArrayAdapter);

        listV_years.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mthisActivity.setSelectedYear(filesList.get(position));
                mthisActivity.mPager.setCurrentItem(0, true);
                mthisActivity.mPager.setCurrentItem(2, true);

            }
        });


        return mRootView;
    }


    final Handler ftphandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case FTP_Task.MSG_ID_CHDIR:
                    listV_years.setVisibility(View.VISIBLE);
                    for(int i=0; i<mftpTask.getFtpConnection().getFolders().size()-2; i++){
                        filesList.add(mftpTask.getFtpConnection().getFolders().get(i));
                    }
                    mArrayAdapter.notifyDataSetChanged();
                    overlayLoading.setVisibility(View.GONE);
                    break;
                case FTP_Task.MSG_ID_UPLOAD:

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onStart(){
        super.onStart();
        overlayLoading.setVisibility(View.VISIBLE);
        mftpTask = new FTP_Task(ftphandler);
        mftpTask.ChangeDirectory("Dirk/Bilder/");
    }
}
