package com.garvinling.lockness;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

/**
 * Created by Garvin on 12/27/13.
 */
public class SettingsFragment extends Fragment{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

           //Inflate the layout for this fragment.

            return inflater.inflate(R.layout.settings_view,container,false);

    }




}
