package com.v41.exercices.helloworld;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Utilisateur on 2016-04-29.
 */
public class ReadInputStreamTask extends AsyncTask<BluetoothSocket,Void,String>{

    private Callback callback;

    public ReadInputStreamTask(Callback callback) {
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
    protected String doInBackground(BluetoothSocket... params) {
        Log.v("TASK","Input doInBackground");
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        String message = "";

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = params[0].getInputStream().read(buffer);
                message = new String(buffer,0,bytes);
                // Send the obtained bytes to the UI activity

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return message;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param s The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.v("TASK","input OnPostExecute");
        callback.onMessageReceived(s);

    }

    public interface Callback {
        void onMessageReceived(String message);
    }
}
