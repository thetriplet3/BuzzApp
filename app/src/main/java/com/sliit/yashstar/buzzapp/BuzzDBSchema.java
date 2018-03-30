package com.sliit.yashstar.buzzapp;

import android.provider.BaseColumns;

/**
 * Created by Tharushan on 2018-03-17.
 */

public final class BuzzDBSchema {
    private BuzzDBSchema(){}

    public static class SMSLog implements BaseColumns {
        public static final String TABLE_NAME = "SMSLog";
        public static final String COL_LOG_TIMESTAMP = "LogTimeStamp";
        public static final String COL_SMS_MESSAGE = "LogMessage";
        public static final String COL_SMS_NUMBER = "LogNumber";
        public static final String COL_SMS_SENT_STATUS = "LogSMSSentStatus";
        public static final String COL_SMS_DELIVERY_STATUS = "LogSMSDeliveryStatus";
    }

    public static class CustomContacts implements BaseColumns{
        public static final String TABLE_NAME = "CustomContacts";
        public static final String COL_CONTACT_ID = "ContactId";
        public static final String COL_CONTACT_NAME = "ContactName";
        public static final String COL_CONTACT_NO = "ContactNo";

    }
}
