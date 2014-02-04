package com.nickedynick.lumix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

//ToDo: Develop code for UDP server (DatagramSocket) to get live feed.
public class UDPServer extends AsyncTask<Integer, Integer, Long> {

    private Activity activity;

    DatagramSocket socket;

    Bitmap bmp = null;

    Boolean kill = false;

    int port;
    private InetAddress myIP;

    public UDPServer(Activity activity)
    {
        this.activity = activity;

        Log.d(activity.getString(R.string.DebugTag), "Creating socket...");

        try {
            this.myIP = getLocalIpAddress();
            this.port = Integer.parseInt(activity.getString(R.string.cameraPort));
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            Log.e(activity.getString(R.string.DebugTag), e.getStackTrace().toString());
        }
    }

    public InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(activity.getString(R.string.DebugTag), ex.toString());
        }
        return null;
    }

    // ToDo: Check this out: http://stackoverflow.com/questions/3205191/android-and-mjpeg

    @Override
    protected Long doInBackground(Integer... integers) {
        try {
            byte[] outBuffer;
            byte[] inBuffer = new byte[30000];

            int offset=132;

            DatagramPacket packet;

            int n = 0;

            while (!kill)
            {
                try {
                    packet = new DatagramPacket(inBuffer, inBuffer.length, myIP, port);

                    // ToDo: I think my socket sometimes stops receiving packets.
                    socket.receive(packet);

                    outBuffer = packet.getData();

                    for (int i = 130; i < 320; i += 1)
                    {
                        if (outBuffer[i]==-1 && outBuffer[i+1]==-40)
                        {
                            offset = i;
                        }
                    }

//                    byte[] newBuffer = Arrays.copyOfRange(outBuffer, offset, packet.getLength());
//                    bmp = BitmapFactory.decodeStream( new ByteArrayInputStream(newBuffer));

//                    MJpegInputStream mStream = new MJpegInputStream(new ByteArrayInputStream(outBuffer));
//                    bmp = mStream.readMJpegFrame();

                    // ToDo: java.lang.ArrayIndexOutOfBoundsException here occasionally. Probably sending incomplete data.
                    bmp = BitmapFactory.decodeByteArray(outBuffer, offset, packet.getLength());

                    // ToDo: Stream freezes after a certain amount of time. May be a memory leak.
                    publishProgress(n, offset);
                    n++;
                }
                catch (Exception e){
                    bmp = null; // This seems to extend life a bit, but not enough.
                    e.printStackTrace();
                }
            }

            socket.close();

        } catch (Exception e) {
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

        int n = progress[0];
        int offset = progress[1];

        Log.d(activity.getString(R.string.DebugTag), "Image " + String.valueOf(n) + ": " + String.valueOf(offset));

        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);

        // ToDo: Investigate canvas approach properly.

        Log.d(activity.getString(R.string.DebugTag), "Image " + String.valueOf(n) + " set!");
    }

    protected void onPostExecute(Long result) {
        Log.d(activity.getString(R.string.DebugTag), "UDP server finished...");

        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
    }
}
