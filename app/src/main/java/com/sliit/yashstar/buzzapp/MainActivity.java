package com.sliit.yashstar.buzzapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import android.util.Log;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<String> lstNumbers = new ArrayList<>();
    private Map<String, String> lstSentNumbers = new HashMap<>();
    private Map<String, String> lstDeliveredNumbers = new HashMap<>();

    private FusedLocationProviderClient mFusedLocationClient;

    private SmsManager smsManager;
    private BroadcastReceiver smsSendResultsReceiver;
    private BroadcastReceiver smsDeliveryResultsReceiver;

    private String SMS_MESSAGE = null;
    public static String LOC_LATITUDE = null;
    public static String LOC_LONGITUDE = null;
    private String LOG_CURRENT_TIMESTAMP = null;

    private final String SMS_SENT = "BUZZAPP_SMS_SENT";
    private final String SMS_DELIVERED = "BUZZAPP_SMS_DELIVERED";

    private int SMS_CODE = 0;
    private int NO_OF_RECIPIENTS = 0;
    private int NO_OF_SENT_MSGS = 0;

    private Button btnSend;
    private TextView txtCurrentLocation;

    private LocationCallback mLocationCallback;

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
        //txtCurrentLocation.setText("Fetching curent location...");
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setEnabled(false);

        checkAndGrantPermission();
        setLocation();

        smsManager = SmsManager.getDefault();
        handleReceiveResults();

        Log.i("TTT.onCreate", "registered broadcasters ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsSendResultsReceiver, new IntentFilter(SMS_SENT));
        registerReceiver(smsDeliveryResultsReceiver, new IntentFilter(SMS_DELIVERED));
        Log.i("TTT.onResume", "registered broadcasters ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(smsSendResultsReceiver);
        //unregisterReceiver(smsDeliveryResultsReceiver);
        //Log.i("TTT.onPause", "unregistered broadcasters ");
    }

    @Override
    protected  void onDestroy() {
        unregisterReceiver(smsSendResultsReceiver);
        unregisterReceiver(smsDeliveryResultsReceiver);
        super.onDestroy();
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
            MainActivity.this.startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (id == R.id.nav_send) {
            MainActivity.this.startActivity(new Intent(getApplicationContext(), LogHeaderActivity.class));
        }
        else if (id == R.id.nav_contact_list)
        {
            MainActivity.this.startActivity(new Intent(getApplicationContext(), Contact_list.class));
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
        LOG_CURRENT_TIMESTAMP = Util.GetCurrentDateTime();

        Log.i("TTT.btnSendOnClick", "Click!");

        if(true) {
            SMS_MESSAGE = Util.GetSOSMessage(this);
            //bulkSendWithThreads();
            //bulkSend();
            //sendMessage();
        }
        else {
            SMS_MESSAGE = "Please send help!";
            //sendMessage();
        }
    }

    private void loadNumbers() {
        lstNumbers.add("0773387575"); //Trat
        lstNumbers.add("0773746968"); //Yash
        //lstNumbers.add("0777391244"); //pesu
        //lstNumbers.add("0770422390"); //daree
        //lstNumbers.add("0766420930"); //uvini
        /*lstNumbers.add("0773612218"); //janith
        lstNumbers.add("0773946070"); //aaq
        lstNumbers.add("0774306392"); //bhana
        lstNumbers.add("0777225877"); //anji
        lstNumbers.add("0775869055"); //pancha
        lstNumbers.add("0773387575"); //Trat*/
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


    private void bulkSend() {
        PendingIntent smsSentPendingIntent;
        PendingIntent smsSentDeliveredIntent;
        Intent smsSentIntent = new Intent(SMS_SENT);
        Intent smsDeliveryIntent = new Intent(SMS_DELIVERED);

        for(String number : lstNumbers) {
            try {
                Thread.sleep(3000);

                smsSentIntent.putExtra("NUMBER", number);
                smsSentIntent.putExtra("LOG_TIMESTAMP", LOG_CURRENT_TIMESTAMP);
                smsDeliveryIntent.putExtra("NUMBER", number);
                smsDeliveryIntent.putExtra("LOG_TIMESTAMP", LOG_CURRENT_TIMESTAMP);

                smsSentPendingIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsSentIntent, PendingIntent.FLAG_ONE_SHOT);
                smsSentDeliveredIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsDeliveryIntent, PendingIntent.FLAG_ONE_SHOT);
                InsertLogRecord(LOG_CURRENT_TIMESTAMP, number);

                smsManager.sendTextMessage(number, null, SMS_MESSAGE, smsSentPendingIntent, smsSentDeliveredIntent);
                Log.i("TTT.SendNexMessage", "Message send to " + number);
                SMS_CODE++;

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

        }

    }

    private void bulkSendWithThreads() {
        PendingIntent smsSentPendingIntent;
        PendingIntent smsSentDeliveredIntent;
        Intent smsSentIntent = new Intent(SMS_SENT);
        Intent smsDeliveryIntent = new Intent(SMS_DELIVERED);

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(3, true);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executor = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, queue, handler);
        try {
            for(String number : lstNumbers) {
                smsSentIntent.putExtra("NUMBER", number);
                smsSentIntent.putExtra("LOG_TIMESTAMP", LOG_CURRENT_TIMESTAMP);
                smsDeliveryIntent.putExtra("NUMBER", number);
                smsDeliveryIntent.putExtra("LOG_TIMESTAMP", LOG_CURRENT_TIMESTAMP);

                smsSentPendingIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsSentIntent, PendingIntent.FLAG_ONE_SHOT);
                smsSentDeliveredIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsDeliveryIntent, PendingIntent.FLAG_ONE_SHOT);

                final PendingIntent execSmsSentPendingIntent = smsSentPendingIntent;
                final PendingIntent execSmsSentDeliveredIntent = smsSentDeliveredIntent;
                final String execNumber = number;

                executor.execute(new Runnable() {

                    @Override
                    public void run() {

                        InsertLogRecord(LOG_CURRENT_TIMESTAMP, execNumber);

                        smsManager.sendTextMessage(execNumber, null, SMS_MESSAGE, execSmsSentPendingIntent, execSmsSentDeliveredIntent);
                        Log.i("TTT.SendNexMessage", "Message send to " + execNumber);
                        }
                    });

                SMS_CODE++;


            }
            executor.shutdown();
            while (executor.isTerminated() == false){
                Thread.sleep(50);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    private void sendNextMessage() {
        PendingIntent smsSentPendingIntent;
        PendingIntent smsSentDeliveredIntent;
        Intent smsSentIntent = new Intent(SMS_SENT);
        Intent smsDeliveryIntent = new Intent(SMS_DELIVERED);

        smsSentIntent.putExtra("NUMBER", lstNumbers.get(0));
        smsSentIntent.putExtra("LOG_TIMESTAMP", LOG_CURRENT_TIMESTAMP);
        smsDeliveryIntent.putExtra("NUMBER", lstNumbers.get(0));
        smsDeliveryIntent.putExtra("LOG_TIMESTAMP", LOG_CURRENT_TIMESTAMP);

        smsSentPendingIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsSentIntent, PendingIntent.FLAG_ONE_SHOT);
        smsSentDeliveredIntent = PendingIntent.getBroadcast(this, SMS_CODE, smsDeliveryIntent, PendingIntent.FLAG_ONE_SHOT);

        InsertLogRecord(LOG_CURRENT_TIMESTAMP, lstNumbers.get(0));
        //smsManager.sendTextMessage(lstNumbers.get(0), null, SMS_MESSAGE, smsSentPendingIntent, smsSentDeliveredIntent);
        Log.i("TTT.SendNexMessage", "Message send to " + lstNumbers.get(0));
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
        /*
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    OnSuccess(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                }
            };
        };
        */

    }

    private void handleReceiveResults() {
        smsSendResultsReceiver = new BroadcastReceiver() {
            @SuppressLint("NewApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String sToastMessage = "Blah";
                String sStatus = null;
                String smsReceiveAction = intent.getAction();
                String smsReceiveNumber = intent.getStringExtra("NUMBER");
                String smsLogTimeStamp = intent.getStringExtra("LOG_TIMESTAMP");

                if(smsReceiveAction.equals(SMS_SENT)) {
                    sStatus = getSMSSentStatus(getResultCode());
                    if(sStatus.equals("RESULT_OK")) {
                        NO_OF_SENT_MSGS++;
                    }
                    ModifyLogRecord(smsLogTimeStamp, smsReceiveNumber, sStatus, null);
                    lstSentNumbers.put(smsReceiveNumber, sStatus);

                }

            }
        };

        smsDeliveryResultsReceiver = new BroadcastReceiver() {
            @SuppressLint("NewApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String sToastMessage = "Blah";
                String sStatus = null;
                String smsReceiveAction = intent.getAction();
                String smsReceiveNumber = intent.getStringExtra("NUMBER");
                String smsLogTimeStamp = intent.getStringExtra("LOG_TIMESTAMP");

                if(smsReceiveAction.equals(SMS_DELIVERED)) {
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
                    ModifyLogRecord(smsLogTimeStamp, smsReceiveNumber, null, sStatus);
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
        //SMS_MESSAGE = String.format("PLEASE SEND HELP!! - %s", sLocationString);

        sCurrentLocation = String.format("Latitude - %s, Longitude - %s", LOC_LATITUDE, LOC_LONGITUDE);

        txtCurrentLocation.setText(sCurrentLocation);
        btnSend.setEnabled(true);

        Util.SetCurretLocation(this, lat, lon);
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

    private String getSMSSentStatus(int resultCode) {
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

    private void InsertLogRecord(String logTimeStamp, String number) {
        BuzzDBHandler buzzDBHandler = new BuzzDBHandler(this);
        SQLiteDatabase buzzDB = buzzDBHandler.getWritableDatabase();
        ContentValues logValues = new ContentValues();

        logValues.put(BuzzDBSchema.SMSLog.COL_LOG_TIMESTAMP, logTimeStamp);
        logValues.put(BuzzDBSchema.SMSLog.COL_SMS_MESSAGE, SMS_MESSAGE);
        logValues.put(BuzzDBSchema.SMSLog.COL_SMS_NUMBER, number);


        buzzDB.insert(BuzzDBSchema.SMSLog.TABLE_NAME, null, logValues);
    }

    private void ModifyLogRecord(String logTimeStamp, String number, String sentStatus, String deliveryStatus) {
        BuzzDBHandler buzzDBHandler = new BuzzDBHandler(this);
        SQLiteDatabase buzzDB = buzzDBHandler.getWritableDatabase();
        ContentValues logValues = new ContentValues();

        String selection = String.format("%s = ? AND %s = ?", BuzzDBSchema.SMSLog.COL_LOG_TIMESTAMP, BuzzDBSchema.SMSLog.COL_SMS_NUMBER);
        String[] selectionArgs = new String[] {logTimeStamp, number};

        if(sentStatus == null) {
            logValues.put(BuzzDBSchema.SMSLog.COL_SMS_DELIVERY_STATUS, deliveryStatus);
        }
        else {
            logValues.put(BuzzDBSchema.SMSLog.COL_SMS_SENT_STATUS, sentStatus);
        }

        buzzDB.update(BuzzDBSchema.SMSLog.TABLE_NAME, logValues, selection, selectionArgs);
    }

    protected void location_OnClick(View view) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("LAT", LOC_LATITUDE);
        mapIntent.putExtra("LON", LOC_LONGITUDE);

        startActivity(mapIntent);
    }

}
