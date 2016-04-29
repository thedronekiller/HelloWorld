package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;

import java.io.IOException;


public class ServerAcceptConnectionTask extends AsyncTask<BluetoothAdapter,Void,BluetoothSocket>{

    private BluetoothServerSocket mmServerSocket;
    private Callback callback;

    public ServerAcceptConnectionTask(Callback callback) {
        this.callback = callback;

    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected BluetoothSocket doInBackground(BluetoothAdapter... params) {

        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        Log.v("TASK","ServerAcceptConnection doInBackground");
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = params[0].listenUsingRfcommWithServiceRecord(MainActivity.MAGIE,MainActivity.uuid);
        } catch (IOException e) { }
        mmServerSocket = tmp;

        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
                socket.getOutputStream().write("Allo".getBytes(),0,4);
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            }
        }
        return socket;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param bluetoothSocket The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        super.onPostExecute(bluetoothSocket);
        Log.v("TASK","ServerAcceptConnection onPostExecute");
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onConnectionAccepted(bluetoothSocket);
    }

    public interface Callback{
        void onConnectionAccepted(BluetoothSocket socket);
    }
}