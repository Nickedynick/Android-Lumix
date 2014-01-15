package com.nickedynick.lumix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

//ToDo: Develop code for UDP server (DatagramSocket) to get live feed.
//ToDo: MJPEG. What is it, and how do I show a stream of it?
public class UDPServer extends AsyncTask<Integer, Integer, Long> {

    private Activity activity;

    DatagramSocket socket;

    Bitmap bmp = null;

    Boolean kill = false;

    public UDPServer(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected Long doInBackground(Integer... integers) {
        try {
            Log.d(activity.getString(R.string.DebugTag), "Creating socket...");

            socket = new DatagramSocket(Integer.parseInt(activity.getString(R.string.cameraPort)));

            byte[] outBuffer;
            byte[] inBuffer = new byte[30000];

            int offset=132;

            DatagramPacket packet;

            //while (!kill)
            for (int n =0; n < 50; n++)
            {
                packet = new DatagramPacket(inBuffer, inBuffer.length);

                socket.receive(packet);

                outBuffer = packet.getData();

                for (int i = 130; i < 320; i += 1)
                {
                    if (outBuffer[i]==-1 && outBuffer[i+1]==-40)
                    {
                        offset = i;
                    }
                }

                byte[] newBuffer = Arrays.copyOfRange(outBuffer, offset, packet.getLength());

                bmp = BitmapFactory.decodeByteArray(newBuffer, 0, newBuffer.length);

                //ImageView imageView = (ImageView)activity.findViewById(R.id.imageView);
                //imageView.setImageBitmap(bmp);

                Log.d(activity.getString(R.string.DebugTag), "Image set!");
            }

            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onCancelled()
    {
        kill = true;
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.d(activity.getString(R.string.DebugTag), progress[0].toString());
    }

    protected void onPostExecute(Long result) {
        Log.d(activity.getString(R.string.DebugTag), "Did a things...");

        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
    }
}
