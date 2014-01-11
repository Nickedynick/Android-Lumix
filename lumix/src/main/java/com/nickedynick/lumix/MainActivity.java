package com.nickedynick.lumix;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
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
import android.widget.TextView;

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
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
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
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            if (rootView != null)
            {
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

                // Set up button.
                Button bConnect = (Button)rootView.findViewById(R.id.buttonConnect);
                bConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        connectToWifi(view);
                    }
                });
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        public void connectToWifi(View view)
        {
            //http://developer.android.com/reference/android/net/wifi/WifiManager.html

            Log.v("Lumix", "Connecting...");
            WifiManager wifiManager = (WifiManager)view.getContext().getSystemService(Context.WIFI_SERVICE);
            Boolean scanned = wifiManager.startScan();

            Log.v("Lumix", "Last Scan Results:");
            for (ScanResult sr : wifiManager.getScanResults())
            {
                Log.v("Lumix", "SSID: " + sr.SSID + "\r\n   Capabilities" + sr.capabilities);

                if (sr.SSID.equals(getString(R.string.wifiSSID)))
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
                Log.v("Lumix", wcPre.SSID + " >> " + getString(R.string.wifiSSID));

                if (wcPre.SSID.equals(getString(R.string.wifiSSID)))
                {
                    bWifiConfigured = true;
                    netId = wcPre.networkId;
                }
            }

            if (!bWifiConfigured)
            {
                // setup a wifi configuration
                wc = new WifiConfiguration();
                wc.SSID = getString(R.string.wifiSSID);
                wc.preSharedKey = getString(R.string.wifiPSK);
                wc.status = WifiConfiguration.Status.ENABLED;
                //wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                //wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                //wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                //wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                //wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                //wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                // connect to and enable the connection
                netId = wifiManager.addNetwork(wc);
            }

            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.setWifiEnabled(true);
            //wifiManager.reconnect();
        }
    }

}
