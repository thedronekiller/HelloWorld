package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private TextView bluetoothStatus;
    private ListView listKnownDevices;
    private final int REQUEST_BLUETOOTH_ENABLE = 20;
    private BluetoothAdapter adapter;
    private TreeMap<String,String> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        listKnownDevices = (ListView)findViewById(R.id.listKnownDevices);
        adapter = BluetoothAdapter.getDefaultAdapter();
        devices = new TreeMap<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(adapter==null){
            bluetoothStatus.setText("Bluetooth not supported on this device");
        }
        else{
            if(!adapter.isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, REQUEST_BLUETOOTH_ENABLE);
            }
            else{
                bluetoothStatus.setText("Bluetooth enabled");
                findBluetoothDevice();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_BLUETOOTH_ENABLE || requestCode==RESULT_OK){
            bluetoothStatus.setText("Bluetooth enabled");
            findBluetoothDevice();
        }
        else{
            bluetoothStatus.setText("Bluetooth is disabled");
        }
    }

    private void findBluetoothDevice(){

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.put(device.getAddress(),device.getName());
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,filter);
    }
}
