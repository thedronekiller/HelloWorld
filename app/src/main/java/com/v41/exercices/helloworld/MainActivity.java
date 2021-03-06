package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ReadInputStreamTask.Callback, ConnectToServerTask.Callback,ServerAcceptConnectionTask.Callback{

    //0 veut dire indéfinie
    public static final int BLUETOOTH_DISCOVERABLE_DURATION = 0;
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 20;
    private BluetoothSocket client;
    private TextView bluetoothStatus;
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private TextView listBondedDevices;
    private View rootView;

    private BluetoothSocket socket;
    private Server server;

    private final int REQUEST_BLUETOOTH_ENABLE = 20;
    private BluetoothAdapter adapter;
    private List<String> devices;
    private TreeMap<String,BluetoothDevice> bluetoothDevices;
    private BroadcastReceiver receiver;
    public static  UUID uuid;
    public static final String MAGIE = "Magie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uuid = new UUID(1024L,128L);

        bluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        spinner = (Spinner) findViewById(R.id.spinner);
        listBondedDevices = (TextView)findViewById(R.id.listBondedDevices);
        rootView = findViewById(R.id.rootView);
        bluetoothDevices = new TreeMap<>();

        devices = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, devices);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        adapter = BluetoothAdapter.getDefaultAdapter();
        server = new Server(adapter);

        Set<BluetoothDevice> devicesss = adapter.getBondedDevices();

        String bondedDevices = "";
        Iterator<BluetoothDevice> it = devicesss.iterator();
        while(it.hasNext()) {
            bondedDevices += it.next().getName() + "\n";
        }
        listBondedDevices.setText(bondedDevices);
    }

    @Override
    protected void onStart() {
        super.onStart();

        enableAccessCoarseLocation();

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

        Set<BluetoothDevice> bondedDevices1 = adapter.getBondedDevices();
        String bondedDevices = "";
        Iterator<BluetoothDevice> it = bondedDevices1.iterator();


        while(it.hasNext()) {
            bondedDevices += it.next().getName() + "\n";
        }

        listBondedDevices.setText(bondedDevices);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_BLUETOOTH_ENABLE){
            if(resultCode==RESULT_OK){
                bluetoothStatus.setText("Bluetooth enabled");
                if(!adapter.isDiscovering()) {
                    enabledDeviceDiscovery();
                }
            } else {
                bluetoothStatus.setText("Bluetooth is disabled");
            }
        }

        if(requestCode==BLUETOOTH_DISCOVERABLE_DURATION){
            if(resultCode!=RESULT_CANCELED){
                bluetoothStatus.setText("Discoverability enabled");
                findBluetoothDevice();
                if(adapter.isDiscovering())
                    adapter.cancelDiscovery();
                adapter.startDiscovery();
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

    private void enableAccessCoarseLocation(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_COARSE_LOCATION_PERMISSIONS){
            if (grantResults.length == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Snackbar.make(rootView,"The app cannot work without the coarse location permission",Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    private void findBluetoothDevice(){

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device != null && !devices.contains(device.getName())){

                        try {
                            bluetoothDevices.put(device.getName(), device);
                            devices.add(device.getName());
                            arrayAdapter.notifyDataSetChanged();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
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
        receiver = null;
    }

    public void onClickConnect(View view) {
        Log.v("Main","Click");
        String name = spinner.getSelectedItem().toString();
        adapter.cancelDiscovery();
        //server.onConnectToOtherServer(bluetoothDevices.get(name));
        try {
            client = bluetoothDevices.get(name).createRfcommSocketToServiceRecord(MainActivity.uuid);
            ConnectToServerTask task = new ConnectToServerTask(this);
            //task.execute(client);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,client);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageReceived(String message) {
        bluetoothStatus.setText(message);
        int i=0;
        //ReadInputStreamTask taskClient = new ReadInputStreamTask(this);
        //taskClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,socket);
    }

    @Override
    public void onConnectionDone(Boolean success) {
        int i=0;
        //server.startRead();
        ServerAcceptConnectionTask connectionTask = new ServerAcceptConnectionTask(this);
        connectionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,adapter);
        bluetoothStatus.setText("Connection request sent");
        /*ReadInputStreamTask taskClient = new ReadInputStreamTask(this);
        String name = spinner.getSelectedItem().toString();
        try {
            client = bluetoothDevices.get(name).createRfcommSocketToServiceRecord(MainActivity.uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,client);*/
    }

    @Override
    public void onConnectionAccepted(BluetoothSocket socket) {
        bluetoothStatus.setText("Connected to the selected device");
        server.startRead(socket);
    }
}
