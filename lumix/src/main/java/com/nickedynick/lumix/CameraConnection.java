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

        /*
        <item id="menu_item_id_afmode" enable="yes" value="aftracking" />
        <item id="menu_item_id_afmode_facedetection" enable="yes" />
        <item id="menu_item_id_afmode_aftracking" enable="yes" />
        <item id="menu_item_id_afmode_23area" enable="yes" />
        <item id="menu_item_id_afmode_spot" enable="yes" />
        <item id="menu_item_id_afmode_1area" enable="yes" />
        <item id="menu_item_id_afmode_pinpoint" enable="no" />

        <item id="menu_item_id_lightmet" enable="yes" value="multi" />
        <item id="menu_item_id_lightmet_multi" enable="yes" />
        <item id="menu_item_id_lightmet_center" enable="yes" />
        <item id="menu_item_id_lightmet_spot" enable="yes" />

        <item id="menu_item_id_burst" enable="yes" value="off" />
        <item id="menu_item_id_burst_auto" enable="no" />
        <item id="menu_item_id_burst_af_cont_2" enable="yes" />
        <item id="menu_item_id_burst_af_cont_5" enable="yes" />
        <item id="menu_item_id_burst_af_sgl_10" enable="yes" />
        <item id="menu_item_id_burst_af_sgl_40" enable="yes" />
        <item id="menu_item_id_burst_af_sgl_60" enable="yes" />
        <item id="menu_item_id_burst_flash" enable="no" />
        <item id="menu_item_id_burst_off" enable="yes" />

        <item id="menu_item_id_color_mode" enable="yes" value="standard" />
        <item id="menu_item_id_color_mode_standard" enable="yes" />
        <item id="menu_item_id_color_mode_vivid" enable="yes" />
        <item id="menu_item_id_color_mode_bw" enable="yes" />
        <item id="menu_item_id_color_mode_sepia" enable="yes" />
        <item id="menu_item_id_color_mode_happy" enable="no" />

        <item id="menu_item_id_videoformat" enable="no" />
        <item id="menu_item_id_videoformat_avchd" enable="no" />
        <item id="menu_item_id_videoformat_mp4" enable="no" />

        <item id="menu_item_id_v_quality" enable="yes" value="mp4_30p_20mbps" />
        <item id="menu_item_id_v_quality_avchd_60p_28mbps_gps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_60p_28mbps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_60i_17mbps_gps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_60i_17mbps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_60p_17mbps_gps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_60p_17mbps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_50p_28mbps_gps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_50p_28mbps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_50i_17mbps_gps" enable="yes" />
        <item id="menu_item_id_v_quality_avchd_50i_17mbps" enable="yes" />
        <item id="menu_item_id_v_quality_avchd_50p_17mbps_gps" enable="yes" />
        <item id="menu_item_id_v_quality_avchd_50p_17mbps" enable="yes" />
        <item id="menu_item_id_v_quality_avchd_30p_24mbps" enable="no" />
        <item id="menu_item_id_v_quality_avchd_24p_24mbps" enable="no" />
        <item id="menu_item_id_v_quality_mp4_30p_20mbps" enable="no" />
        <item id="menu_item_id_v_quality_mp4_30p_10mbps" enable="no" />
        <item id="menu_item_id_v_quality_mp4_30p_4mbps" enable="no" />
        <item id="menu_item_id_v_quality_mp4_25p_20mbps" enable="yes" />
        <item id="menu_item_id_v_quality_mp4_25p_10mbps" enable="yes" />
        <item id="menu_item_id_v_quality_mp4_25p_4mbps" enable="yes" />

        <item id="menu_item_id_liveview_quality" enable="yes" value="vga" />
        <item id="menu_item_id_liveviewsize_vga" enable="yes" />
        <item id="menu_item_id_liveviewsize_qvga" enable="yes" />
        */
    }

    public class QMenu
    {
        /*
        <item id="menu_item_id_f_and_ss2" enable="yes" />

        <item id="menu_item_id_shutter_speed2" enable="no" />

        <item id="menu_item_id_aperture2" enable="no" />

        <item id="menu_item_id_exposure2" enable="no" />

        <item id="menu_item_id_exposure_m5" enable="no" />
        <item id="menu_item_id_exposure_m14_3" enable="no" />
        <item id="menu_item_id_exposure_m13_3" enable="no" />
        <item id="menu_item_id_exposure_m4" enable="no" />
        <item id="menu_item_id_exposure_m11_3" enable="no" />
        <item id="menu_item_id_exposure_m10_3" enable="no" />
        <item id="menu_item_id_exposure_m3" enable="no" />
        <item id="menu_item_id_exposure_m8_3" enable="no" />
        <item id="menu_item_id_exposure_m7_3" enable="no" />
        <item id="menu_item_id_exposure_m2" enable="no" />
        <item id="menu_item_id_exposure_m5_3" enable="no" />
        <item id="menu_item_id_exposure_m4_3" enable="no" />
        <item id="menu_item_id_exposure_m1" enable="no" />
        <item id="menu_item_id_exposure_m2_3" enable="no" />
        <item id="menu_item_id_exposure_m1_3" enable="no" />
        <item id="menu_item_id_exposure_0" enable="yes" />
        <item id="menu_item_id_exposure_p1_3" enable="no" />
        <item id="menu_item_id_exposure_p2_3" enable="no" />
        <item id="menu_item_id_exposure_p1" enable="no" />
        <item id="menu_item_id_exposure_p4_3" enable="no" />
        <item id="menu_item_id_exposure_p5_3" enable="no" />
        <item id="menu_item_id_exposure_p2" enable="no" />
        <item id="menu_item_id_exposure_p7_3" enable="no" />
        <item id="menu_item_id_exposure_p8_3" enable="no" />
        <item id="menu_item_id_exposure_p3" enable="no" />
        <item id="menu_item_id_exposure_p10_3" enable="no" />
        <item id="menu_item_id_exposure_p11_3" enable="no" />
        <item id="menu_item_id_exposure_p12_3" enable="no" />
        <item id="menu_item_id_exposure_p13_3" enable="no" />
        <item id="menu_item_id_exposure_p14_3" enable="no" />
        <item id="menu_item_id_exposure_p15_3" enable="no" />
         */
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
            //ToDo: Develop code for UDP server (DatagramSocket) to get live feed.
            //ToDo: MJPEG. What is it, and how do I show a stream of it?
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
