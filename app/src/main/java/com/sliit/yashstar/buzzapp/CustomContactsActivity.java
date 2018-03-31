package com.sliit.yashstar.buzzapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomContactsActivity extends AppCompatActivity {

    ListView listCustomContactsList;
    TextView txtAddList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_contacts);
        listCustomContactsList = (ListView) findViewById(R.id.custom_contacts);
        txtAddList = (TextView) findViewById(R.id.add_contacts);
        txtAddList.setVisibility(View.VISIBLE);

        populateContacts();
        PopulateList();
    }

    //Insert selected contacts in to the SQLite database
//    public void InsertContact(String contactId, String contactName, String[] contactNo)
//    {
//        int index = 0;
//        String concatContactNo = "";
//        BuzzDBHandler dbHandler = new BuzzDBHandler(this);
//        SQLiteDatabase database = dbHandler.getWritableDatabase();
//
//        if(contactNo.length > 1)
//        {
//            for(int i = 0; i<contactNo.length; i++)
//            {
//                concatContactNo += contactNo[i].toString() + "#";
//            }
//        }
//
//        String sql =
//                "INSERT OR REPLACE INTO " + BuzzDBSchema.CustomContacts.TABLE_NAME +
//                "( " + BuzzDBSchema.CustomContacts.COL_CONTACT_ID + ", " + BuzzDBSchema.CustomContacts.COL_CONTACT_NAME + ", " + BuzzDBSchema.CustomContacts.COL_CONTACT_NO+ " )" +
//                "VALUES( '" + contactId+ "' , ' " +contactName+ "' , ' " + concatContactNo+ "' )" ;
//        database.execSQL(sql);
//    }

    private void PopulateList() {
        BuzzDBHandler buzzDBHandler = new BuzzDBHandler(this);
        SQLiteDatabase buzzDB = buzzDBHandler.getWritableDatabase();
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

        if (getCustomContacts.getCount() == 0) {
            dlgWarning();
        }
        else
        {
            int count = getCustomContacts.getCount();
            txtAddList.setVisibility(View.INVISIBLE);
            while (getCustomContacts.moveToNext()) {
                //String contactId = getCustomContacts.getString(getCustomContacts.getColumnIndex(BuzzDBSchema.CustomContacts.COL_CONTACT_ID));
                String contactName = getCustomContacts.getString(getCustomContacts.getColumnIndex(BuzzDBSchema.CustomContacts.COL_CONTACT_NAME));
                String contactNo = getCustomContacts.getString(getCustomContacts.getColumnIndex(BuzzDBSchema.CustomContacts.COL_CONTACT_NO));
                //String contactNo = "trying";
                listCustomContacts.add(String.format("%s\n%s",  contactName, contactNo));
            }

            getCustomContacts.close();
            ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listCustomContacts);
            listCustomContactsList.setAdapter(simpleAdapter);
        }
    }

    private void dlgWarning()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warn_create_list)
                .setTitle(R.string.warn_title);

        builder.setPositiveButton(R.string.warn_btn_Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Contact_list.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.warn_btn_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dlgWarn = builder.create();
        dlgWarn.show();
    }

    public void populateContacts()
    {
        Button btnAddContactList = (Button)findViewById(R.id.add_contacts);

        btnAddContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomContactsActivity.this, Contact_list.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == 2)
        {
            //PopulateList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remove_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.removeList:
                removeContactList();
                break;
        }
        return true;
    }

    private void removeContactList()
    {
        BuzzDBHandler buzzDBHandler = new BuzzDBHandler(this);
        SQLiteDatabase buzzDB = buzzDBHandler.getWritableDatabase();
        String sql = "DELETE FROM " + BuzzDBSchema.CustomContacts.TABLE_NAME;
        buzzDB.execSQL(sql);
        txtAddList.setVisibility(View.VISIBLE);

    }

}
