package com.sliit.yashstar.buzzapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LogHeaderActivity extends AppCompatActivity {

    ListView lsV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_header);
        lsV = (ListView) findViewById(R.id.lstLogHeader);

        PopulateList();
    }

    private void PopulateList() {
        BuzzDBHandler buzzDBHandler = new BuzzDBHandler(this);
        SQLiteDatabase buzzDB = buzzDBHandler.getWritableDatabase();
        List<String> lstLogTImestamp = new ArrayList<String>();

        String[] selectedColums = {
                BuzzDBSchema.SMSLog.COL_LOG_TIMESTAMP,
                BuzzDBSchema.SMSLog.COL_SMS_NUMBER,
                BuzzDBSchema.SMSLog.COL_SMS_SENT_STATUS,
                BuzzDBSchema.SMSLog.COL_SMS_DELIVERY_STATUS
        };

        Cursor getLogs = buzzDB.query(
                true,
                BuzzDBSchema.SMSLog.TABLE_NAME,
                selectedColums,
                null,
                null,
                null,
                null,
                null,
                null
        );

        while(getLogs.moveToNext()) {
            String logTimestamp  = getLogs.getString(getLogs.getColumnIndex(BuzzDBSchema.SMSLog.COL_LOG_TIMESTAMP));
            String logNumber  = getLogs.getString(getLogs.getColumnIndex(BuzzDBSchema.SMSLog.COL_SMS_NUMBER));
            String logSentStatus  = getLogs.getString(getLogs.getColumnIndex(BuzzDBSchema.SMSLog.COL_SMS_SENT_STATUS));
            String LogDeliveryStatus  = getLogs.getString(getLogs.getColumnIndex(BuzzDBSchema.SMSLog.COL_SMS_DELIVERY_STATUS));
            lstLogTImestamp.add(String.format("%s - %s - %s - %s", logTimestamp, logNumber, logSentStatus, LogDeliveryStatus));
        }

        getLogs.close();
        ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstLogTImestamp);
        lsV.setAdapter(simpleAdapter);
    }
}
