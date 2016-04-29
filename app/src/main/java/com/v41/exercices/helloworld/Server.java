package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

/**
 * Created by Utilisateur on 2016-04-29.
 */
public class Server implements ServerAcceptConnectionTask.Callback {

    public Server(BluetoothAdapter adapter) {
        ServerAcceptConnectionTask task = new ServerAcceptConnectionTask();
        task.execute(adapter);
    }

    @Override
    public void onConnectionAccepted(BluetoothSocket socket) {

    }
}
