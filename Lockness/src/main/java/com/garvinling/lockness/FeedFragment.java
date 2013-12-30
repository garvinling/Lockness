package com.garvinling.lockness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

/**
 * Created by Garvin on 12/27/13.
 */
public class FeedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

            return inflater.inflate(R.layout.feed_view,container,false);


    }


}
