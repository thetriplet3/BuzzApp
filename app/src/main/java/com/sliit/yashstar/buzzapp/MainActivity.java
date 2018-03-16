package com.sliit.yashstar.buzzapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<String> lstNumbers = new ArrayList<>();
    private Map<String, String> lstSentNumbers = new HashMap<>();
    private Map<String, String> lstDeliveredNumbers = new HashMap<>();

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
    private int NO_OF_RECIPIENTS = 0;
    private int NO_OF_SENT_MSGS = 0;

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

    protected void btnSendOnClick(View view) {
        loadNumbers();
        SMS_CODE = 0;
        NO_OF_SENT_MSGS = 0;
        NO_OF_RECIPIENTS = lstNumbers.size();
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
            @SuppressLint("NewApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String sToastMessage = "Blah";
                String sStatus = null;
                String smsReceiveAction = intent.getAction();
                String smsReceiveNumber = intent.getStringExtra("NUMBER");

                if(smsReceiveAction.equals(SMS_SENT)) {
                    sStatus = getSMSSentStatus(getResultCode());
                    if(sStatus.equals("RESULT_OK")) {
                        NO_OF_SENT_MSGS++;
                    }
                    lstSentNumbers.put(smsReceiveNumber, sStatus);

                    if (!lstNumbers.isEmpty()) {
                        sendNextMessage();
                    }
                }
                else if(smsReceiveAction.equals(SMS_DELIVERED)) {
                    SmsMessage smsMsg = null;

                    byte[] pdu = intent.getByteArrayExtra("pdu");
                    String format = intent.getStringExtra("format");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && format != null) {
                        smsMsg = SmsMessage.createFromPdu(pdu, format);
                    }
                    else {
                        smsMsg = SmsMessage.createFromPdu(pdu);
                    }

                    sStatus = getSMSDeliveryStatus(smsMsg.getStatus());
                    lstDeliveredNumbers.put(smsReceiveNumber, sStatus);
                }

                if(lstNumbers.isEmpty()) {
                    if(NO_OF_SENT_MSGS == NO_OF_RECIPIENTS) {
                        sToastMessage = String.format("Message sent to all recipients.");
                    }
                    else {
                        sToastMessage = String.format("Message sent to %d out of %d recipients.", NO_OF_SENT_MSGS, NO_OF_RECIPIENTS);
                    }
                    Toast.makeText(context, sToastMessage, Toast.LENGTH_SHORT).show();
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
        SMS_MESSAGE = String.format("PLEASE SEND HELP!! - %s", sLocationString);

        sCurrentLocation = String.format("Latitude - %s, Longitude - %s", LOC_LATITUDE, LOC_LONGITUDE);

        txtCurrentLocation.setText(sCurrentLocation);
        btnSend.setEnabled(true);
    }

    private String getSMSDeliveryStatus(int resultCode) {
        switch (resultCode) {
            case Telephony.Sms.STATUS_COMPLETE:
                return "STATUS_COMPLETE";
            case Telephony.Sms.STATUS_FAILED:
                return "STATUS_FAILED";
            case Telephony.Sms.STATUS_PENDING:
                return "STATUS_PENDING";
            case Telephony.Sms.STATUS_NONE:
                return "STATUS_NONE";
            default:
                return "STATUS_UNKNOWN";
        }
    }

    String getSMSSentStatus(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                return "RESULT_OK";
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                return "RESULT_ERROR_GENERIC_FAILURE";
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                return "RESULT_ERROR_RADIO_OFF";
            case SmsManager.RESULT_ERROR_NULL_PDU:
                return "RESULT_ERROR_NULL_PDU";
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                return "RESULT_ERROR_NO_SERVICE";
            default:
                return "RESULT_ERROR_UNKNOWN";
        }
    }
}
