package com.sliit.yashstar.buzzapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomContactsActivity extends AppCompatActivity {

    ListView listCustomContactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_contacts);
        listCustomContactsList = (ListView) findViewById(R.id.custom_contacts);

        PopulateList();
    }

    //Insert selected contacts in to the SQLite database
    public void InsertContact(String contactId, String contactName, String[] contactNo)
    {
        int index = 0;
        String concatContactNo = "";
        BuzzDBHandler dbHandler = new BuzzDBHandler(this);
        SQLiteDatabase database = dbHandler.getWritableDatabase();

        if(contactNo.length > 1)
        {
            for(int i = 0; i<contactNo.length; i++)
            {
                concatContactNo += contactNo[i].toString() + "#";
            }
        }

        String sql =
                "INSERT OR REPLACE INTO " + BuzzDBSchema.CustomContacts.TABLE_NAME +
                "( " + BuzzDBSchema.CustomContacts.COL_CONTACT_ID + ", " + BuzzDBSchema.CustomContacts.COL_CONTACT_NAME + ", " + BuzzDBSchema.CustomContacts.COL_CONTACT_NO+ " )" +
                "VALUES( '" + contactId+ "' , ' " +contactName+ "' , ' " + concatContactNo+ "' )" ;
        database.execSQL(sql);
    }

    private void PopulateList() {
        BuzzDBHandler buzzDBHandler = new BuzzDBHandler(this);
        SQLiteDatabase buzzDB = buzzDBHandler.getReadableDatabase();
        List<String> listCustomContacts = new ArrayList<String>();

        String[] selectedColums = {
                BuzzDBSchema.CustomContacts.COL_CONTACT_ID,
                BuzzDBSchema.CustomContacts.COL_CONTACT_NAME,
                BuzzDBSchema.CustomContacts.COL_CONTACT_NO
        };

        Cursor getCustomContacts = buzzDB.query(
                true,
                BuzzDBSchema.CustomContacts.TABLE_NAME,
                selectedColums,
                null,
                null,
                null,
                null,
                null,
                null
        );

//        Cursor getCustomContacts = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            getCustomContacts = buzzDB.rawQuery("SELECT DISTINCT * FROM " + BuzzDBSchema.CustomContacts.TABLE_NAME, null,null);
//        }

        if (getCustomContacts.getCount() == 0) {
            dlgWarning();
        }
        else
        {
            while (getCustomContacts.moveToNext()) {
                String contactId = getCustomContacts.getString(getCustomContacts.getColumnIndex(BuzzDBSchema.CustomContacts.COL_CONTACT_ID));
                String contactName = getCustomContacts.getString(getCustomContacts.getColumnIndex(BuzzDBSchema.CustomContacts.COL_CONTACT_NAME));
                String contactNo = getCustomContacts.getString(getCustomContacts.getColumnIndex(BuzzDBSchema.CustomContacts.COL_CONTACT_NO));
            }

            getCustomContacts.close();
            ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listCustomContacts);
            listCustomContactsList.setAdapter(simpleAdapter);
        }
    }

    private void dlgWarning()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("There is no contact list available. Create new list?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
