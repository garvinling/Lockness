package com.garvinling.lockness;

import android.app.Activity;
;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DashboardFragment extends Fragment
{

           @Override
           public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

                view = inflater.inflate(R.layout.dashboard_view,container,false);
                init();
                setTypeFace();

                return view;
           }

            private void init(){

                title = (TextView) view.findViewById(R.id.title);
                lock_button = (Button) view.findViewById(R.id.lock_button);
                title.setText("Lockness Connected");

                if(locked)
                {
                    lock_button.setText("Unlocked");
                }
                else
                {
                    lock_button.setText("Locked");
                }


                lock_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                           if(locked)
                           {
                               ((MainActivity)getActivity()).unlockDoor();
                               locked = false;
                               lock_button.setText("Lock Door");
                           }
                           else
                           {
                               ((MainActivity)getActivity()).lockDoor();
                               locked = true;
                               lock_button.setText("Unlock Door");
                           }


                    }
                });


            }


            public void setTypeFace(){

                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/JosefinSlab-Regular.ttf");
                Typeface button_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/JosefinSlab-SemiBold.ttf");

                title.setTypeface(font);
                lock_button.setTypeface(button_font);
            }

    View view;
    TextView title;
    Button lock_button;
    boolean locked = false;

}
