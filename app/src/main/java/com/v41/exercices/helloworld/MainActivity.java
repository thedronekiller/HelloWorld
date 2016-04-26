package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    public static final int BLUETOOTH_DISCOVERABLE_DURATION = 300;
    private TextView bluetoothStatus;
    private TextView listKnownDevices;
    private final int REQUEST_BLUETOOTH_ENABLE = 20;
    private BluetoothAdapter adapter;
    private TreeMap<String,String> devices;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        listKnownDevices = (TextView)findViewById(R.id.listKnownDevices);
        adapter = BluetoothAdapter.getDefaultAdapter();
        devices = new TreeMap<>();
        Set<BluetoothDevice> devicesss = adapter.getBondedDevices();

        Iterator<BluetoothDevice> it = devicesss.iterator();
        while(it.hasNext()) {
            String name = it.next().getName();
        }
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
                enabledDeviceDiscovery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_BLUETOOTH_ENABLE){
            if(resultCode==RESULT_OK){
                bluetoothStatus.setText("Bluetooth enabled");
                enabledDeviceDiscovery();
            } else {
                bluetoothStatus.setText("Bluetooth is disabled");
            }
        }


        if(requestCode==BLUETOOTH_DISCOVERABLE_DURATION){
            if(resultCode!=RESULT_CANCELED){
                bluetoothStatus.setText("Discoverability enabled");
                findBluetoothDevice();
            } else {
                bluetoothStatus.setText("Discoverability disabled");
            }

        }
    }

    private void enabledDeviceDiscovery(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, BLUETOOTH_DISCOVERABLE_DURATION);
    }

    private void findBluetoothDevice(){

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.put(device.getAddress(),device.getName());
                    updateDeviceList();
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void updateDeviceList(){
        String deviceList = "";
        for(int i=0;i<devices.size();i++){
            deviceList += devices.get(i).toString() + "\n";
        }
        listKnownDevices.setText(deviceList);
    }
}
