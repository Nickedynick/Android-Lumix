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
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class CameraConnection extends AsyncTask<CameraConnection.Command, Integer, Long> {

    Activity activity;

    SharedPreferences sharedPref;

    String ipAddress;

    String port;

    public enum Command
    {
        GetState,
        StartServer,
        StopServer,
        Viewer,
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
            case GetState:
                return "?mode=getstate";
            case StartServer:
                return "?mode=startstream&value=" + this.port;
            case StopServer:
                return "?mode=stopstream";
            case Viewer:
                return "?mode=camcmd&value=recmode";
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

    private CameraConnection.Command executedCommand;

    protected Long doInBackground(CameraConnection.Command... commands) {
        int count = commands.length;
        long taskResponse = 0;
        boolean result = false;

        for (int i = 0; i < count; i++) {

            this.executedCommand = commands[i];

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

                    result = processXML(responseString);

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

            // Quit if CamReply result is not ok.
            if (!result) break;
        }

        if (result) taskResponse = 1;

        return taskResponse;
    }

    private boolean processXML(String xml)
    {
        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            XMLParser xmlParser = new XMLParser();
            CamReply response = xmlParser.parse(stream);

            //Log.d(this.activity.getString(R.string.DebugTag), "Cam response: " + String.valueOf(response.Result));

            // ToDo: Do other things here based on CamReply received.

            return response.Result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    protected void onProgressUpdate(Integer... progress) {
        // Nothing.
    }

    protected void onPostExecute(Long result) {
//        if (this.executedCommand.equals(Command.Viewer))
//        {
//            if (result == 1)
//            {
//                Log.d(this.activity.getString(R.string.DebugTag), "Starting UDP server...");
//
//                MainActivity mainActivity = (MainActivity)this.activity;
//                mainActivity.server = new UDPServer(this.activity);
//                mainActivity.server.execute();
//            }
//        }
    }
}
