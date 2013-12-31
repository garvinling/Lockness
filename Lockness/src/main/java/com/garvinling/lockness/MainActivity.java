package com.garvinling.lockness;

;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.garvinling.bluetooth.BTAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private LocknessFinderFragment lock_fragment = new LocknessFinderFragment();

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

        lock_fragment.setTypeFace();
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


            if(LOCKNESS_DEVICE_EXISTS == 0)
            {
                   startDiscovery();        //Retrieve list of discovered bluetooth devices and store it in an ArrayList::discoveredDevices
            }
            else
            {                               //Connect to the paired device.
                   connectToPairedLocknessDevice();

            }

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

            //Check the list of paired devices and see if one of them is a lockness device.


            devicesArray = btAdapter.getBondedDevices();
            if(devicesArray.size() > 0 )
            {
                   for(BluetoothDevice device:devicesArray){

                            String listString = device.getName() + "  ::  " + device.getAddress();
                            pairedDevices.add(listString);
                            checkForPairedLocknessDevice(listString);

                            if(LOCKNESS_DEVICE_EXISTS == 1){                //Connect to the first device that is a lockness device
                                locknessDevice = device;                    //Set the bluetoothDevice to the matched lockness device
                                break;
                            }

                   }


            }
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

                            print("Discovered Device.  Adding to Devices ArrayList.");
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            boolean valid_device = checkValidLocknessDevice(device.getName());
                            String discovered = device.getName() +" : " +device.getAddress();

                            devices.add(device);
                            String s = "";

                            for(int a = 0; a < pairedDevices.size(); a++){

                                if(LOCKNESS_DEVICE_EXISTS == 1){              //Checks if lockness is one of the paired devices on the phone/tablet

                                    //connectToExistingLockness(device);        //Connect to the paired device
                                }

                            }

                            discoveredDevices.add(discovered);

                            if(discovered.contains("RN42-0576"))
                            {
                                lock_fragment.setText("Please Pair your Lockness!");
                                LOCKNESS_DEVICE_ONLINE = 1;
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

            //Update UI font elements
            lock_fragment.setTypeFace();

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



    private void checkForPairedLocknessDevice(String deviceString){

            if(deviceString.contains("RN42")){

                print("Lockness Device exists on this phone");
                LOCKNESS_DEVICE_EXISTS = 1;

            }


    }




    private boolean checkValidLocknessDevice(String deviceString){

            if(deviceString.contains("RN42")){

                return true;
            }

        return false;
    }

    private void connectToPairedLocknessDevice(){

        print("Connecting to Lockness");
        ConnectThread bt_connect = new ConnectThread(locknessDevice);           //Connect to lockness device
        bt_connect.run();


    }



    /** Bluetooth Connect Thread Functions **/

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { System.out.println("IOEXCEPTION"); }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();


            } catch (IOException connectException) {

                print("Unable to connect");
                lock_fragment.setText("Unable to Connect to Lockness.)");

                try {

                    mmSocket.close();

                } catch (IOException closeException) { }



                return;
            }

            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    /** Bluetooth Connected Thread Functions **/


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity

                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what){
                case SUCCESS_CONNECT:
                    // DO something
                    print("Success_Connect");
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(), "CONNECT", 0).show();
                    String s = "l";
                    connectedThread.write(s.getBytes());
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[])msg.obj;
                    String string = new String(readBuf);
                    Toast.makeText(getApplicationContext(), string, 0).show();
                    break;
            }
        }
    };



    private FeedFragment feed_fragment = new FeedFragment();
    private SettingsFragment settings_fragment = new SettingsFragment();
    private Bundle instance_state;
    private int current_position = 0;
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    private int LOCKNESS_DEVICE_EXISTS = 0;
    private int LOCKNESS_DEVICE_ONLINE = 0;
    private String device_identifier;      //need to pull device string from sql.


    BluetoothDevice locknessDevice;
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    Set<BluetoothDevice> devicesArray;
    ArrayList<BluetoothDevice> devices;
    ArrayList<String> pairedDevices;
    ArrayList<String> discoveredDevices;
    IntentFilter filter;
    BroadcastReceiver receiver;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


}

