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

    public class State
    {
        public String Battery;
        public String CameraMode;
        public int Capacity;
        public String SDCardStatus;
        public String SDMemory;
        public int CapacityVideo;
        public boolean Record;
        public boolean BurstIntervalStatus;
        public boolean SDAccess;
        public String rem_disp_typ;
        public int ProgressTime;
        public String Operate;
        public String version;
    }

    public class MenuInfo
    {
        public String Model;
        public String Version;
        public MainMenu Main_Menu;
        public QMenu Q_Menu;
    }

    public class MainMenu
    {
        public MenuItem CreativeControl;
        public MenuItem CreativeControl_Pop;
        public MenuItem CreativeControl_Retro;
        public MenuItem CreativeControl_HighKey;
        public MenuItem CreativeControl_LowKey;
        public MenuItem CreativeControl_Sepia;
        public MenuItem CreativeControl_DynamicMono;
        public MenuItem CreativeControl_ImpressiveArt;
        public MenuItem CreativeControl_HighDynamic;
        public MenuItem CreativeControl_CrossProcess;
        public MenuItem CreativeControl_ToyPhoto;
        public MenuItem CreativeControl_Diorama;
        public MenuItem CreativeControl_SoftFocus;
        public MenuItem CreativeControl_CrossFilter;
        public MenuItem CreativeControl_OnePointColour;

        public MenuItem Flash;
        public MenuItem Flash_Auto;
        public MenuItem Flash_iAuto;
        public MenuItem Flash_AutoRedEye;
        public MenuItem Flash_ForcedFlashOn;
        public MenuItem Flash_ForcedOnRedEye;
        public MenuItem Flash_SlowSync;
        public MenuItem Flash_SlowSyncRedEye;
        public MenuItem Flash_ForcedFlashOff;

        public MenuItem SelfTimer;
        public MenuItem SelfTimer_10s;
        public MenuItem SelfTimer_2s;
        public MenuItem SelfTimer_Off;

        public MenuItem Macro;
        public MenuItem Macro_AF;
        public MenuItem Macro_Zoom;
        public MenuItem Macro_Off;

        public MenuItem AspectRatio;
        public MenuItem AspectRatio_4_3;
        public MenuItem AspectRatio_3_2;
        public MenuItem AspectRatio_16_9;
        public MenuItem AspectRatio_1_1;

        public MenuItem PictureSize;
        public MenuItem PictureSize_18m;
        public MenuItem PictureSize_16m;
        public MenuItem PictureSize_14m;
        public MenuItem PictureSize_13_5m;
        public MenuItem PictureSize_12_5m;
        public MenuItem PictureSize_12m;
        public MenuItem PictureSize_12mez;
        public MenuItem PictureSize_10_5m;
        public MenuItem PictureSize_10_5mez;
        public MenuItem PictureSize_10m;
        public MenuItem PictureSize_10mez;
        public MenuItem PictureSize_9m;
        public MenuItem PictureSize_9mez;
        public MenuItem PictureSize_8m;
        public MenuItem PictureSize_8mez;
        public MenuItem PictureSize_7_5m;
        public MenuItem PictureSize_7_5mez;
        public MenuItem PictureSize_6m;
        public MenuItem PictureSize_6mez;
        public MenuItem PictureSize_5m;
        public MenuItem PictureSize_5mez;
        public MenuItem PictureSize_4_5m;
        public MenuItem PictureSize_4_5mez;
        public MenuItem PictureSize_4m;
        public MenuItem PictureSize_3_5m;
        public MenuItem PictureSize_3_5mez;
        public MenuItem PictureSize_3m;
        public MenuItem PictureSize_3mez;
        public MenuItem PictureSize_2_5m;
        public MenuItem PictureSize_2_5mez;
        public MenuItem PictureSize_1m;
        public MenuItem PictureSize_1mez;
        public MenuItem PictureSize_0_3m;
        public MenuItem PictureSize_0_3mez;
        public MenuItem PictureSize_0_2m;
        public MenuItem PictureSize_0_2mez;

        public MenuItem PhotoSize;
        public MenuItem PhotoSize_4_3_18m;
        public MenuItem PhotoSize_4_3_16m;
        public MenuItem PhotoSize_4_3_14m;
        public MenuItem PhotoSize_4_3_10mez;
        public MenuItem PhotoSize_4_3_10m;
        public MenuItem PhotoSize_4_3_8m;
        public MenuItem PhotoSize_4_3_5mez;
        public MenuItem PhotoSize_4_3_5m;
        public MenuItem PhotoSize_4_3_4m;
        public MenuItem PhotoSize_4_3_3mez;
        public MenuItem PhotoSize_4_3_0_3mez;
        public MenuItem PhotoSize_4_3_0_3m;
        public MenuItem PhotoSize_3_2_16m;
        public MenuItem PhotoSize_3_2_14m;
        public MenuItem PhotoSize_3_2_12_5m;
        public MenuItem PhotoSize_3_2_7m;
        public MenuItem PhotoSize_3_2_3_5m;
        public MenuItem PhotoSize_3_2_2_5m;
        public MenuItem PhotoSize_16_9_13_5m;
        public MenuItem PhotoSize_16_9_12m;
        public MenuItem PhotoSize_16_9_10_5m;
        public MenuItem PhotoSize_16_9_6m;
        public MenuItem PhotoSize_16_9_2m;
        public MenuItem PhotoSize_1_1_13_5m;
        public MenuItem PhotoSize_1_1_12m;
        public MenuItem PhotoSize_1_1_10_5m;
        public MenuItem PhotoSize_1_1_6m;
        public MenuItem PhotoSize_1_1_3m;
        public MenuItem PhotoSize_1_1_2_5m;

        public MenuItem Quality;
        public MenuItem QualityFine;
        public MenuItem QualityStandard;

        public MenuItem AFMode;
        public MenuItem AFMode_FaceDetection;
        public MenuItem AFMode_AFTracking;
        public MenuItem AFMode_23Area;
        public MenuItem AFMode_Spot;
        public MenuItem AFMode_1Area;
        public MenuItem AFMode_PinPoint;

        public MenuItem LightMetering;
        public MenuItem LightMetering_Multi;
        public MenuItem LightMetering_Centre;
        public MenuItem LightMetering_Spot;

        public MenuItem Burst;
        public MenuItem Burst_Auto;
        public MenuItem Burst_AF_Cont_2;
        public MenuItem Burst_AF_Cont_5;
        public MenuItem Burst_AF_Single_10;
        public MenuItem Burst_AF_Single_40;
        public MenuItem Burst_AF_Single_60;
        public MenuItem Burst_Flash;
        public MenuItem Burst_Off;

        public MenuItem ColourMode;
        public MenuItem ColourMode_Standard;
        public MenuItem ColourMode_Vivid;
        public MenuItem ColourMode_BV;
        public MenuItem ColourMode_Sepia;
        public MenuItem ColourMode_Happy;

        public MenuItem VideoFormat;
        public MenuItem VideoFormat_AVCHD;
        public MenuItem VideoFormat_MP4;

        public MenuItem VideoQuality;
        public MenuItem VideoQuality_avchd_60p_28mbps_gps;
        public MenuItem VideoQuality_avchd_60p_28mbps;
        public MenuItem VideoQuality_avchd_60i_17mbps_gps;
        public MenuItem VideoQuality_avchd_60i_17mbps;
        public MenuItem VideoQuality_avchd_60p_17mbps_gps;
        public MenuItem VideoQuality_avchd_60p_17mbps;
        public MenuItem VideoQuality_avchd_50p_28mbps_gps;
        public MenuItem VideoQuality_avchd_50p_28mbps;
        public MenuItem VideoQuality_avchd_50i_17mbps_gps;
        public MenuItem VideoQuality_avchd_50i_17mbps;
        public MenuItem VideoQuality_avchd_50p_17mbps_gps;
        public MenuItem VideoQuality_avchd_50p_17mbps;
        public MenuItem VideoQuality_avchd_30p_24mbps;
        public MenuItem VideoQuality_avchd_24p_24mbps;
        public MenuItem VideoQuality_mp4_30p_20mbps;
        public MenuItem VideoQuality_mp4_30p_10mbps;
        public MenuItem VideoQuality_mp4_30p_4mbps;
        public MenuItem VideoQuality_mp4_25p_20mbps;
        public MenuItem VideoQuality_mp4_25p_10mbps;
        public MenuItem VideoQuality_mp4_25p_4mbps;

        public MenuItem LiveView_Quality;
        public MenuItem LiveView_VGA;
        public MenuItem LiveView_QVGA;
    }

    public class QMenu
    {
        public MenuItem F_SS2;

        public MenuItem Shutter_Speed2;

        public MenuItem Aperture2;

        public MenuItem Exposure2;

        public MenuItem Exposure_m5;
        public MenuItem Exposure_m14_3;
        public MenuItem Exposure_m13_3;
        public MenuItem Exposure_m4;
        public MenuItem Exposure_m11_3;
        public MenuItem Exposure_m10_3;
        public MenuItem Exposure_m3;
        public MenuItem Exposure_m8_3;
        public MenuItem Exposure_m7_3;
        public MenuItem Exposure_m2;
        public MenuItem Exposure_m5_3;
        public MenuItem Exposure_m4_3;
        public MenuItem Exposure_m1;
        public MenuItem Exposure_m2_3;
        public MenuItem Exposure_m1_3;
        public MenuItem Exposure_0;
        public MenuItem Exposure_p1_3;
        public MenuItem Exposure_p2_3;
        public MenuItem Exposure_p1;
        public MenuItem Exposure_p4_3;
        public MenuItem Exposure_p5_3;
        public MenuItem Exposure_p2;
        public MenuItem Exposure_p7_3;
        public MenuItem Exposure_p8_3;
        public MenuItem Exposure_p3;
        public MenuItem Exposure_p10_3;
        public MenuItem Exposure_p11_3;
        public MenuItem Exposure_p4;
        public MenuItem Exposure_p13_3;
        public MenuItem Exposure_p14_3;
        public MenuItem Exposure_p5;
    }

    public class MenuItem
    {
        public String ID;
        public boolean Enabled;
        public String Value;
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

                    if (commands[i].equals(Command.GetState))
                    {
                        processXML(responseString);
                    }
                    else if (commands[i].equals(Command.Capabilities))
                    {
                        processXML(responseString);
                    }

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
