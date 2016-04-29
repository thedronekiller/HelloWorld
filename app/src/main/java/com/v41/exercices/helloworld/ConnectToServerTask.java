package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Utilisateur on 2016-04-29.
 */
public class ConnectToServerTask extends AsyncTask<BluetoothSocket,Void,Boolean> {

    private ConnectToServerTask.Callback callback;

    public ConnectToServerTask(Callback callback) {
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
    protected Boolean doInBackground(BluetoothSocket... params) {
        try {
            params[0].connect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param b The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
        callback.onConnectionDone(b);
    }

    public interface Callback{
        void onConnectionDone(Boolean succes);
    }
}
