package com.garvinling.lockness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Garvin on 12/28/13.
 */
public class LockControlFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.lockcontrol_view,container,false);
        scanButton = (Button)view.findViewById(R.id.scan);
        title = (TextView) view.findViewById(R.id.title);



        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setText("Searching for Lockness");
                ((MainActivity)getActivity()).startDiscovery();
            }
        });

        return view;

    }



    public void setText(String text){

       if(view!=null){

           title.setText(text);

       }
        else{
           System.out.println("View is null.");
       }

    }
    TextView title;
    Button scanButton;
    View view;
}
