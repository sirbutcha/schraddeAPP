package de.teufelslochschradde.schraddeftp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by dirk on 15.06.2017.
 */

public class StartFragment extends Fragment {

    MainActivity mthisActivity;
    Button connectButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup mRootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_start, container, false);

        mthisActivity = (MainActivity) getActivity();

        connectButton = (Button) mRootView.findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mthisActivity.mPager.setCurrentItem(1, true);

            }
        });

        return mRootView;

    }

}
