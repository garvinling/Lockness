package com.garvinling.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.Toast;
import com.garvinling.lockness.MainActivity;
/**
 * Created by Garvin on 12/28/13.
 */
public class BTAdapter extends Activity {

    public BTAdapter(){

        //1. Get the Adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){

                //Bluetooth not available

        }

        int REQUEST_ENABLE_BT = 1;

        //2. Enable Bluetooth
        if(!mBluetoothAdapter.isEnabled()){

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);

        }



    }


}
