package com.nickedynick.lumix;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

//ToDo: Develop code for UDP server (DatagramSocket) to get live feed.
//ToDo: MJPEG. What is it, and how do I show a stream of it?
public class UDPServer extends AsyncTask<Integer, Integer, Long> {

    private Activity activity;

    public UDPServer(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected Long doInBackground(Integer... integers) {
        try {

            byte[] message = new byte[1500];

            DatagramPacket packet = new DatagramPacket(message, message.length);
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(activity.getString(R.string.cameraPort)));
            socket.receive(packet);

            String sentMessage = new String(message, 0, packet.getLength());

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.d(activity.getString(R.string.DebugTag), progress[0].toString());
    }

    protected void onPostExecute(Long result) {
        //Log.d(activity.getString(R.string.DebugTag), "Downloaded " + result + " bytes");
    }
}
