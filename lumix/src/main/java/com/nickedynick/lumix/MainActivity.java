package com.nickedynick.lumix;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ShareActionProvider;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // This is a comment!

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;

        switch (position)
        {
            case 0:
            default:
                fragment = GalleryFragment.newInstance(position + 1);
                break;
            case 1:
                fragment = LiveFragment.newInstance(position + 1);
                break;
            case 2:
                fragment = ConnectionFragment.newInstance(position + 1);
                break;
        }

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.section_Gallery);
                break;
            case 2:
                mTitle = getString(R.string.section_Live);
                break;
            case 3:
                mTitle = getString(R.string.section_Connection);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        }
    }

    private ShareActionProvider mShareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.menu_item_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) item.getActionProvider();

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class GalleryFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GalleryFragment newInstance(int sectionNumber) {
            GalleryFragment fragment = new GalleryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public GalleryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

            if (rootView != null)
            {
                GridView gridview = (GridView) rootView.findViewById(R.id.gridView);
                gridview.setAdapter(new ImageAdapter(rootView.getContext()));
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LiveFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LiveFragment newInstance(int sectionNumber) {
            LiveFragment fragment = new LiveFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public LiveFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_live, container, false);

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            String ipAddress = sharedPref.getString(getString(R.string.sharedPrefCameraIP), getString(R.string.cameraIP));
            String args = "";
            String url = "http://" + ipAddress + "/cam.cgi" + args;

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
                    Log.v("Lumix", responseString);
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ConnectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ConnectionFragment newInstance(int sectionNumber) {
            ConnectionFragment fragment = new ConnectionFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ConnectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            String cameraSSID = sharedPref.getString(getString(R.string.sharedPrefCameraSSID), "\"" + getString(R.string.cameraSSID) + "\"");
            String cameraPSK = sharedPref.getString(getString(R.string.sharedPrefCameraPSK), "\"" + getString(R.string.cameraPSK) + "\"");

            EditText editTextSSID = (EditText) rootView.findViewById(R.id.editTextSSID);
            editTextSSID.setText(cameraSSID);

            EditText editTextPassword = (EditText) rootView.findViewById(R.id.editTextPSK);
            editTextPassword.setText(cameraPSK);

            // Set up button.
            Button bConnect = (Button)rootView.findViewById(R.id.buttonConnect);
            bConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectToCamera(view.getRootView());
                }
            });

            // Set up button.
            Button bDisconnect = (Button)rootView.findViewById(R.id.buttonDisconnect);
            bDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reconnectToWifi(view.getRootView());
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        public void connectToCamera(View view)
        {
            Log.v("Lumix", "Connecting...");
            WifiManager wifiManager = (WifiManager)view.getContext().getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String currentSSID = wifiInfo.getSSID();

            EditText editTextSSID = (EditText) view.findViewById(R.id.editTextSSID);
            EditText editTextPSK = (EditText) view.findViewById(R.id.editTextPSK);

            String cameraSSID = editTextSSID.getText().toString();
            String cameraPSK = editTextPSK.getText().toString();

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (!currentSSID.equals(cameraSSID)) editor.putString(getString(R.string.sharedPrefLastSSID), currentSSID);

            editor.putString(getString(R.string.sharedPrefCameraSSID), cameraSSID);
            editor.putString(getString(R.string.sharedPrefCameraPSK), cameraPSK);

            editor.commit();

            Boolean scanned = wifiManager.startScan();

            Log.v("Lumix", "Last Scan Results:");
            for (ScanResult sr : wifiManager.getScanResults())
            {
                Log.v("Lumix", "SSID: " + sr.SSID + "\r\n   Capabilities" + sr.capabilities);

                if (sr.SSID.equals(cameraSSID))
                {
                    Log.v("Lumix", "Network found: " + sr.SSID);
                }
            }

            Log.v("Lumix", "Configured APs:");
            WifiConfiguration wc;
            int netId = 0;

            Boolean bWifiConfigured = false;

            for (WifiConfiguration wcPre : wifiManager.getConfiguredNetworks())
            {
                Log.v("Lumix", wcPre.SSID + " >> " + cameraSSID);

                if (wcPre.SSID.equals(cameraSSID))
                {
                    bWifiConfigured = true;
                    netId = wcPre.networkId;
                }
            }

            if (!bWifiConfigured)
            {
                wc = new WifiConfiguration();
                wc.SSID = cameraSSID;
                wc.preSharedKey = cameraPSK;
                wc.status = WifiConfiguration.Status.ENABLED;

                netId = wifiManager.addNetwork(wc);
            }

            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.setWifiEnabled(true);
        }

        public void reconnectToWifi(View view)
        {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

            String lastSSID = sharedPref.getString(getString(R.string.sharedPrefLastSSID), "");

            if (!lastSSID.equals(""))
            {
                WifiManager wifiManager = (WifiManager)view.getContext().getSystemService(Context.WIFI_SERVICE);

                for (WifiConfiguration wcPre : wifiManager.getConfiguredNetworks())
                {
                    if (wcPre.SSID.equals(lastSSID))
                    {
                        int netId = wcPre.networkId;
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(netId, true);
                        wifiManager.setWifiEnabled(true);
                    }
                }
            }
        }
    }
}
