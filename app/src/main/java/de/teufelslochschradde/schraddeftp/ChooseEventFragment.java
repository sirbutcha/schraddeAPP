package de.teufelslochschradde.schraddeftp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import de.teufelslochschradde.schraddeftp.ftp_com.Butcha_FTP;

/**
 * Created by dirk on 12.06.2017.
 */

public class ChooseEventFragment extends Fragment {

    private String LOG_INFO = "ChEventFrag";

    MainActivity mthisActivity;
    ListView mEventList;
    FloatingActionButton fab;
    ListView listV_events;
    ArrayAdapter<String> mArrayAdapter;
    LinearLayout overlayLoading;
    Butcha_FTP mFtpClient;
    String mselectedYear = " ";

    // Create a List from String Array elements

    final ArrayList<String> filesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup mRootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_eventlist, container, false);

        // Create an ArrayAdapter from List Years
        mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, filesList);

        mthisActivity = (MainActivity) getActivity();

        mFtpClient = mthisActivity.getFtpClient();

        overlayLoading = (LinearLayout) mthisActivity.findViewById(R.id.overlay_loading);

        listV_events = (ListView) mRootView.findViewById(R.id.listV_events);
        listV_events.setAdapter(mArrayAdapter);

        listV_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fab.setVisibility(View.GONE);
                mthisActivity.setSelectedEvent(filesList.get(position));
                mthisActivity.mPager.setCurrentItem(1, true);
                mthisActivity.mPager.setCurrentItem(3, true);
            }
        });

        return mRootView;
    }


    final Handler mFtpHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case Butcha_FTP.MSG_ID_CHDIR:
                    if(msg.arg1 == Butcha_FTP.MSG_SUCCESS) {
                        listV_events.setVisibility(View.VISIBLE);
                        for (int i = 0; i < mFtpClient.getFolders().size(); i++) {
                            filesList.add(mFtpClient.getFolders().get(i));
                        }
                        mArrayAdapter.notifyDataSetChanged();

                    }else{
                        // TODO
                        mselectedYear = null;
                    }
                    overlayLoading.setVisibility(View.GONE);
                    break;
                case Butcha_FTP.MSG_ID_UPLOAD:

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();

        String selectedYear = mthisActivity.getSelectedYear();
        if(selectedYear == null) {
            mthisActivity.mPager.setCurrentItem(1, true);
        }else {

            if (mselectedYear.equals(selectedYear)) {
               // do nothing
            } else {
                mFtpClient.setHandler(mFtpHandler);
                mFtpClient.doChangeDir(selectedYear);
                overlayLoading.setVisibility(View.VISIBLE);
                mselectedYear = selectedYear;
            }

        }

        fab = (FloatingActionButton) mthisActivity.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

}

