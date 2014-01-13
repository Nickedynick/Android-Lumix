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
        StopServer,
        Viewer,
        Query,
        Capabilities,
        Playback,
        Record,
        Capture,
        CaptureCancel,
        FocusWideFast,
        FocusWideNormal,
        FocusTeleNormal,
        FocusTeleFast,
        ZoomWideFast,
        ZoomWideNormal,
        ZoomTeleNormal,
        ZoomTeleFast,
        ZoomStop
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
        switch (command)
        {
            //ToDo: Develop code for UDP server (DatagramSocket) to get live feed.
            //ToDo: MJPEG. What is it, and how do I show a stream of it?
            case StartServer:
                return "?mode=startstream&value=" + this.port;
            case StopServer:
                return "?mode=stopstream";
            case Viewer:
                return "?mode=camcmd&value=recmode";
            case Query:
                return "?mode=getstate";
            case Capabilities:
                return "?mode=getinfo&type=curmenu";
            case Playback:
                return "?mode=camcmd&value=playmode";
            case Record:
                return "?mode=camcmd&value=video_recstart";
            case Capture:
                return "?mode=camcmd&value=capture";
            case CaptureCancel:
                return "?mode=camcmd&value=capture_cancel";
            case FocusWideFast:
                return "?mode=camctrl&type=focus&value=wide-fast";
            case FocusWideNormal:
                return "?mode=camctrl&type=focus&value=wide-normal";
            case FocusTeleFast:
                return "?mode=camctrl&type=focus&value=tele-normal";
            case FocusTeleNormal:
                return "?mode=camctrl&type=focus&value=tele-fast";
            case ZoomWideFast:
                return "?mode=camcmd&value=wide-fast";
            case ZoomWideNormal:
                return "?mode=camcmd&value=wide-normal";
            case ZoomTeleFast:
                return "?mode=camcmd&value=tele-normal";
            case ZoomTeleNormal:
                return "?mode=camcmd&value=tele-fast";
            case ZoomStop:
                return "?mode=camcmd&value=zoomstop";
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
