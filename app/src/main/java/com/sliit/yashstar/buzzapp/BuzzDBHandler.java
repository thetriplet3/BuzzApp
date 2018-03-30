package com.sliit.yashstar.buzzapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tharushan on 2018-03-17.
 */

public class BuzzDBHandler extends SQLiteOpenHelper{
    private static final String DB_NAME = "BuzzApp.db";

    private static final String SQL_CREATE_SMSLOG_TAB =
            "CREATE TABLE " + BuzzDBSchema.SMSLog.TABLE_NAME + " (" +
                                BuzzDBSchema.SMSLog._ID + " INTEGER PRIMARY KEY," +
                                BuzzDBSchema.SMSLog.COL_LOG_TIMESTAMP + " TIMESTAMP," +
                                BuzzDBSchema.SMSLog.COL_SMS_MESSAGE + " TEXT, "+
                                BuzzDBSchema.SMSLog.COL_SMS_NUMBER + " TEXT, "+
                                BuzzDBSchema.SMSLog.COL_SMS_SENT_STATUS + " TEXT, "+
                                BuzzDBSchema.SMSLog.COL_SMS_DELIVERY_STATUS + " TEXT "+
                    ")";
    private static final String SQL_DELETE_SMSLOG_TAB =
            "DROP TABLE IF EXISTS " + BuzzDBSchema.SMSLog.TABLE_NAME ;

    private static final String SQL_CREATE_CUSTOMCONTACTS_TAB =
            "CREATE TABLE " + BuzzDBSchema.CustomContacts.TABLE_NAME + " (" +
                    BuzzDBSchema.CustomContacts.COL_CONTACT_ID + " INTEGER PRIMARY KEY," +
                    BuzzDBSchema.CustomContacts.COL_CONTACT_NAME + " TEXT," +
                    BuzzDBSchema.CustomContacts.COL_CONTACT_NO + " TEXT "+
                    ")";
    private static final String SQL_DELETE_CUSTOMCONTACTS_TAB =
            "DROP TABLE IF EXISTS " + BuzzDBSchema.CustomContacts.TABLE_NAME ;

    public BuzzDBHandler(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SMSLOG_TAB);
        db.execSQL(SQL_CREATE_CUSTOMCONTACTS_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SMSLOG_TAB);
        db.execSQL(SQL_DELETE_CUSTOMCONTACTS_TAB);
        onCreate(db);
    }
}
