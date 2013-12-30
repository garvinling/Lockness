package com.garvinling.lockness;

import android.app.Activity;
;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.garvinling.bluetooth.BTAdapter;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private LockControlFragment lock_fragment = new LockControlFragment();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        instance_state = savedInstanceState;





        //Adding a new fragment
        if(findViewById(R.id.fragment_container)!=null){

            //If being restored from a previous state, dont do anything and return. Avoids overlapping fragments.
            if(savedInstanceState != null){
                return;
            }


        }

        //Create a new fragment

        lock_fragment.setArguments(getIntent().getExtras());            //In case special instructions from an intent were passed
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,lock_fragment,"lock").commit();
    }


    @Override
    public void onStart(){

        super.onStart();


        init();

        if(btAdapter == null)
        {
            Toast.makeText(getApplicationContext(),"Bluetooth is not available.",0).show();
            finish();

        }
        else
        {
            if(!btAdapter.isEnabled()){

                turnOnBlueTooth();

            }

            getPairedDevices();             //Retrieve list of paired devices and store it in an ArrayList::pairedDevices
            startDiscovery();               //Retrieve list of discovered bluetooth devices and store it in an ArrayList::discoveredDevices

            printPairedDevices();
            printDiscoveredDevices();

        }

    }



    public void startDiscovery(){

        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();

    }




    private void turnOnBlueTooth(){

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent,1);
    }


    private void getPairedDevices(){


            devicesArray = btAdapter.getBondedDevices();
            if(devicesArray.size() > 0 )
            {
                   for(BluetoothDevice device:devicesArray){

                            String listString = device.getName() + "  ::  " + device.getAddress();
                            pairedDevices.add(listString);
                            print("Paired Device: " +listString);
                   }


            }

                //TODO: change name of BT device and implement listview for click to connect.
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED){

            Toast.makeText(getApplicationContext(),"Sorry! Lockness needs bluetooth to run!",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void init(){

            btAdapter = BluetoothAdapter.getDefaultAdapter();
            pairedDevices = new ArrayList<String>();
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            discoveredDevices = new ArrayList<String>();
            devices = new ArrayList<BluetoothDevice>();


            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                        String action = intent.getAction();
                        if(BluetoothDevice.ACTION_FOUND.equals(action)){

                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            devices.add(device);
                            String s = "";

                            for(int a = 0; a < pairedDevices.size();a++){

                                if(device.getName().equals(pairedDevices.get(a))){

                                    s = "(PAIRED)";

                                }

                            }

                            String discovered = device.getName() + " " +s+ " " +device.getAddress();
                            discoveredDevices.add(discovered);

                            if(discovered.contains("SGH"))
                            {
                                lock_fragment.setText("Lockness device found!");
                            }


                            print("Discovered Device added: " +device.getName() + " :: " +device.getAddress());
                        }
                        else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                        {



                        }
                        else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                        {



                        }

                        else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
                        {

                            if(btAdapter.getState() == btAdapter.STATE_OFF)
                                {
                                    turnOnBlueTooth();
                                }

                        }


                }
            };

        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        // May need to change fragment container to linearlayout
        //FragmentManager fragmentManager = getFragmentManager();


        if(position == 0 && current_position != position){


            current_position = position;
            Bundle args = new Bundle();
            //args.putInt("test",position);
            //com.lock_fragment.setArguments(args);
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,lock_fragment,"lock").setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("lock").commit();

            print("This is a test of changing to the lock fragment");

        }
       else if(position == 1 && current_position != position){

            current_position = position;
            Bundle args = new Bundle();
            //args.putInt("test",position);
            //feed_fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,feed_fragment,"feed").setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("feed").commit();

            BTAdapter bt = new BTAdapter();
            print("This is a test of changing fragments");

       }
       else if(position == 2 && current_position != position){

            current_position = position;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,settings_fragment,"settings").setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("settings").commit();




        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void print(String line){

        System.out.println(line);
    }

    private void printPairedDevices(){

        if(pairedDevices.size() > 0)
        {

            for(int i = 0 ; i < pairedDevices.size(); i++){

                print("Paired Device["+i+"]: " +pairedDevices.get(i));


            }

        }
        else
        {

            print("No devices paired.");

        }


    }
    private void printDiscoveredDevices(){

        if(discoveredDevices.size() > 0)
        {

            for(int i = 0 ; i < discoveredDevices.size(); i++){

                print("Discovered Device["+i+"]: " +discoveredDevices.get(i));


            }

        }
        else
        {
            print("No bluetooth devices discovered");
            Toast.makeText(getApplicationContext(),"No Bluetooth Devices in range.",Toast.LENGTH_SHORT).show();
            lock_fragment.setText("No Lockness device detected.");

        }


    }





    /** Bluetooth Connect Thread Functions **/







    private FeedFragment feed_fragment = new FeedFragment();
    private SettingsFragment settings_fragment = new SettingsFragment();
    private Bundle instance_state;
    private int current_position = 0;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<BluetoothDevice> devices;
    ArrayList<String> pairedDevices;
    ArrayList<String> discoveredDevices;
    IntentFilter filter;
    BroadcastReceiver receiver;


}

