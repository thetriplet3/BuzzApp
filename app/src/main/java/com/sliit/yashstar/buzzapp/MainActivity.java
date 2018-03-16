package com.sliit.yashstar.buzzapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<String> lstNumbers = new ArrayList<String>();
    private FusedLocationProviderClient mFusedLocationClient;

    private SmsManager smsManager;
    private IntentFilter smsIntentFilter;
    private BroadcastReceiver smsResultsReceiver;

    private String SMS_MESSAGE = null;
    private String LOC_LATITUDE = null;
    private String LOC_LONGITUDE = null;

    private final String SMS_SENT = "BUZZAPP_SMS_SENT";
    private final String SMS_DELIVERED = "BUZZAPP_SMS_DELIVERED";

    private int SMS_CODE = 0;

    private Button btnSend;
    private TextView txtCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Menu related code
        //region Auto generated code by the template
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion


        txtCurrentLocation = (TextView)findViewById(R.id.txtCurrentLocation);
        txtCurrentLocation.setText("Fetching curent location...");
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setEnabled(false);

        checkAndGrantPermission();
        setLocation();

        smsManager = SmsManager.getDefault();

        smsIntentFilter = new IntentFilter();
        smsIntentFilter.addAction(SMS_SENT);
        smsIntentFilter.addAction(SMS_DELIVERED);
        handleReceiveResults();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsResultsReceiver, smsIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsResultsReceiver);
    }

    //Menu related code
    //region Auto generated code by the template
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    private void btnSendOnClick(View view) {
        loadNumbers();
        if(true) {
            sendMessage();
        }
        else {
            SMS_MESSAGE = "Please send help!";
            sendMessage();
        }
    }

    private void loadNumbers() {
        //lstNumbers.add("0773746968");
        lstNumbers.add("0773387575");
    }

    private void checkAndGrantPermission() {
        int currentLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int sendSms =  ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int misc = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        List<String> lstRequiredPermission = new ArrayList<String>();

        if(currentLocation != PackageManager.PERMISSION_GRANTED) {
            lstRequiredPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(sendSms != PackageManager.PERMISSION_GRANTED) {
            lstRequiredPermission.add(Manifest.permission.SEND_SMS);
        }
        if(misc != PackageManager.PERMISSION_GRANTED) {
            lstRequiredPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if(!lstRequiredPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, lstRequiredPermission.toArray(new String[lstRequiredPermission.size()]), 1);
        }
    }

    private void sendMessage() {
        sendNextMessage();
    }

    private void sendNextMessage() {
        PendingIntent smsSentPendingIntent;
        PendingIntent smsSentDeliveredIntent;
        Intent smsSentIntent = new Intent(SMS_SENT);
        Intent smsDeliveryIntent = new Intent(SMS_DELIVERED);

        smsSentIntent.putExtra("NUMBER", lstNumbers.get(0));
        smsDeliveryIntent.putExtra("NUMBER", lstNumbers.get(0));

        smsSentPendingIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsSentIntent, PendingIntent.FLAG_ONE_SHOT);
        smsSentDeliveredIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsDeliveryIntent, PendingIntent.FLAG_ONE_SHOT);

        smsManager.sendTextMessage(lstNumbers.get(0), null, SMS_MESSAGE, smsSentPendingIntent, smsSentDeliveredIntent);

        SMS_CODE++;
        lstNumbers.remove(0);
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            OnSuccess(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                            //mFusedLocationClient.removeLocationUpdates()
                        }
                    }
                });

    }

    private void handleReceiveResults() {
        smsResultsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sToastMessage = "Blah";
                String smsReceiveAction = intent.getAction();
                String smsReceiveNumber = intent.getStringExtra("NUMBER");

                if(smsReceiveAction.equals(SMS_SENT)) {
                    sToastMessage = String.format("Message sent to %s", smsReceiveNumber);
                    Toast.makeText(context, sToastMessage, Toast.LENGTH_SHORT).show();
                    if (!lstNumbers.isEmpty()) {
                        sendNextMessage();
                    }
                }
                else if(smsReceiveAction.equals(SMS_DELIVERED)) {
                    //sToastMessage = String.format("Message delivered to %s", smsReceiveNumber);
                }
            }
        };
    }

    private void OnSuccess(String lat, String lon)  {
        String sLocationString = null;
        String sCurrentLocation = null;

        LOC_LATITUDE = lat;
        LOC_LONGITUDE = lon;
        sLocationString = String.format("https://www.google.com/maps/?q=%s,%s", LOC_LATITUDE, LOC_LONGITUDE);
        SMS_MESSAGE = String.format("Please send cheese koththu, Moda Yash!! - %s", sLocationString);

        sCurrentLocation = String.format("Latitude - %s, Longitude - %s", LOC_LATITUDE, LOC_LONGITUDE);

        txtCurrentLocation.setText(sCurrentLocation);
        btnSend.setEnabled(true);
    }
}
