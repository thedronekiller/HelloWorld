package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Utilisateur on 2016-04-29.
 */
public class Server implements ServerAcceptConnectionTask.Callback, ConnectToServerTask.Callback, ReadInputStreamTask.Callback {

    private BluetoothSocket client;

    public Server(BluetoothAdapter adapter) {
        ServerAcceptConnectionTask task = new ServerAcceptConnectionTask(this);
        //task.execute(adapter);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,adapter);
    }

    public void startRead(BluetoothSocket otherDevice){
        ReadInputStreamTask taskClient = new ReadInputStreamTask(this);
        //taskClient.execute(client);
        taskClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,otherDevice);
    }

    @Override
    public void onConnectionAccepted(BluetoothSocket socket) {
        client = socket;
        try {
            client.getOutputStream().write("Allo".getBytes(),0,4);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void onConnectToOtherServer(BluetoothDevice deviceToConnect){
        try {
            client = deviceToConnect.createRfcommSocketToServiceRecord(MainActivity.uuid);
            ConnectToServerTask task = new ConnectToServerTask(this);
            task.execute(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onConnectionDone(Boolean success) {
        //if(succes.booleanValue()){
            //startRead();
        //}
    }

    @Override
    public void onMessageReceived(String message) {

    }
}
