package com.nickedynick.lumix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceView;
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

    // ToDo: Check this out: http://stackoverflow.com/questions/3205191/android-and-mjpeg

    @Override
    protected Long doInBackground(Integer... integers) {
        try {
            Log.d(activity.getString(R.string.DebugTag), "Creating socket...");

            socket = new DatagramSocket(Integer.parseInt(activity.getString(R.string.cameraPort)));

            byte[] outBuffer;
            byte[] inBuffer = new byte[30000];

            int offset=132;

            DatagramPacket packet;

            int n = 0;

            while (!kill)
            {
                try {
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

                    /*byte[] newBuffer = Arrays.copyOfRange(outBuffer, offset, packet.getLength());
                    bmp = BitmapFactory.decodeByteArray(newBuffer, 0, newBuffer.length);*/

                    bmp = BitmapFactory.decodeByteArray(outBuffer, offset, packet.getLength());
                }
                catch (Exception except){
                    Log.e(activity.getString(R.string.DebugTag), except.getMessage());
                }

                publishProgress();
                n++;

                // ToDo: Stream freezes after a certain amount of time. M\y be a memory leak.
                Log.d(activity.getString(R.string.DebugTag), "Image " + String.valueOf(n) + " set!");

                //Thread.sleep(100);
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
        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        imageView.invalidate();
    }

    protected void onPostExecute(Long result) {
        Log.d(activity.getString(R.string.DebugTag), "UDP server finished...");

        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
    }
}
