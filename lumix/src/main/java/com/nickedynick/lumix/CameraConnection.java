package com.nickedynick.lumix;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Nick on 13/01/14.
 */
public class CameraConnection extends AsyncTask<CameraConnection.Command, Integer, Long> {

    Activity activity;

    SharedPreferences sharedPref;

    String ipAddress;

    String port;

    public enum Command
    {
        StartServer,
        StopServer
    }

    public CameraConnection(Activity activity)
    {
        this.activity = activity;
        this.sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        this.ipAddress = sharedPref.getString(activity.getString(R.string.sharedPrefCameraIP), activity.getString(R.string.cameraIP));
        this.port = activity.getString(R.string.cameraPort);
    }

    private String arguments(Command command)
    {
        /*
        Start UDP server: http://192.168.54.1/cam.cgi?mode=startstream&value=49199
        Stop server: http://192.168.54.1/cam.cgi?mode=stopstream
        Viewer: http://192.168.54.1/cam.cgi?mode=camcmd&value=recmode
        Query: http://192.168.54.1/cam.cgi?mode=getstate
        Playback: http://192.168.54.1/cam.cgi?mode=camcmd&value=playmode
        Record: http://192.168.54.1/cam.cgi?mode=camcmd&value=video_recstart

        Focus
        Wide fast: http://192.168.54.1/cam.cgi?mode=camctrl&type=focus&value=wide-fast
        Wide normal: http://192.168.54.1/cam.cgi?mode=camctrl&type=focus&value=wide-normal
        Tele normal: http://192.168.54.1/cam.cgi?mode=camctrl&type=focus&value=tele-normal
        Tele fast: http://192.168.54.1/cam.cgi?mode=camctrl&type=focus&value=tele-fast

        Zoom:
        Wide fast: http://192.168.54.1/cam.cgi?mode=camcmd&value=wide-fast
        Wide normal: http://192.168.54.1/cam.cgi?mode=camcmd&value=wide-normal
        Tele normal: http://192.168.54.1/cam.cgi?mode=camcmd&value=tele-normal
        Tele fast: http://192.168.54.1/cam.cgi?mode=camcmd&value=tele-fast
        Stop: http://192.168.54.1/cam.cgi?mode=camcmd&value=zoomstop

        Capture: http://192.168.54.1/cam.cgi?mode=camcmd&value=capture
        Capture cancel: http://192.168.54.1/cam.cgi?mode=camcmd&value=capture_cancel

        Capabilities: http://192.168.54.1/cam.cgi?mode=getinfo&type=curmenu
         */

        switch (command)
        {
            case StartServer:
                return "?mode=startstream&value=" + this.port;
            case StopServer:
                return "?mode=stopstream";
            default:
                return "";
        }
    }

    protected Long doInBackground(CameraConnection.Command... commands) {
        int count = commands.length;
        long totalSize = 0;

        for (int i = 0; i < count; i++) {

            String args = arguments(commands[i]);
            String url = "http://" + this.ipAddress + "/cam.cgi" + args;

            //ToDo: Refactor HttpClient as AsyncTask, add UDP server (DatagramSocket).
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                response = httpclient.execute(new HttpGet(url));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    String responseString = out.toString();
                    Log.d(activity.getString(R.string.DebugTag), responseString);

                    processXML(responseString);
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }

    private void processXML(String xml)
    {
        XMLParser xmlParser = new XMLParser();

    }

    protected void onProgressUpdate(Integer... progress) {
        Log.d(activity.getString(R.string.DebugTag), progress[0].toString());
    }

    protected void onPostExecute(Long result) {
        //Log.d(activity.getString(R.string.DebugTag), "Downloaded " + result + " bytes");
    }
}
