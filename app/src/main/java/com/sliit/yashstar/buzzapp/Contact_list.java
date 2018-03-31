package com.sliit.yashstar.buzzapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Comparator;

public class Contact_list extends AppCompatActivity {

    ListView listContacts;
    ArrayList arrayContacts;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
         Intent addContact = new Intent(Intent.ACTION_INSERT);
         addContact.setType(ContactsContract.Contacts.CONTENT_TYPE);
         startActivity(addContact);
         adapter.notifyDataSetChanged();
         listContacts.setAdapter(adapter);
            }
        });

        Button btnCreate = (Button) findViewById(R.id.createList);
        btnCreate.setVisibility(View.INVISIBLE);
        listContacts = (ListView) findViewById(R.id.contactList);

        showContacts();
    }

    //Menu item to create a custom contact list to send the message
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.createList:
                onCreateList();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Get all the contacts
    public void getContacts()
    {
        Cursor cursor;
        ContentResolver contentResolver = getContentResolver();
        StringBuffer output;
        arrayContacts = new ArrayList<String>();
        String contactNo;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String CONTACT_ID = ContactsContract.Contacts._ID;
        String CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_CONTACT_NO = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String PHONE_CONTACT_NO = ContactsContract.CommonDataKinds.Phone.NUMBER;

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        if(cursor.getCount() > 0)
        {
            while (cursor.moveToNext()) {
                output = new StringBuffer();

                String contactId = cursor.getString(cursor.getColumnIndex(CONTACT_ID));
                String contactName = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
                int hasContactNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_CONTACT_NO)));

                if (hasContactNo > 0) {
                    output.append("\n" + contactName);

                    Cursor cursorPhone = contentResolver.query(PHONE_URI, null, PHONE_CONTACT_ID + " = ? ", new String[]{contactId}, null);

                    while (cursorPhone.moveToNext()) {
                        contactNo = cursorPhone.getString(cursorPhone.getColumnIndex(PHONE_CONTACT_NO));
                        output.append("\n" + contactNo);
                    }
                    cursorPhone.close();
                }


                arrayContacts.add(output.toString());
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, arrayContacts);
            listContacts.setAdapter(adapter);
        }


    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else
        {
            // Android version is lesser than 6.0 or the permission is already granted.
            getContacts();
        }
    }

    private void onCreateList()
    {
        //set the list view to have multi selectable checkboxes
        listContacts.setChoiceMode(listContacts.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, arrayContacts);


        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.fab);
        btnAdd.hide();
        Button btnCreate = (Button) findViewById(R.id.createList);
        btnCreate.setVisibility(View.VISIBLE);

        listContacts.setAdapter(adapter);
    }
}
