package com.sliit.yashstar.buzzapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    boolean bLocation;
    boolean bIsModified;
    String sSOSMessage;
    String sLocationString;
    SharedPreferences sharedPref;

    EditText txtEditMessage;
    CheckBox chbEditLocation;
    TextView txtEditTextPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bIsModified = false;
        txtEditMessage = (EditText)findViewById(R.id.txtMessage);
        chbEditLocation = (CheckBox)findViewById(R.id.chbLocation);
        txtEditTextPreview = (TextView)findViewById(R.id.txtPreview);
        sharedPref = this.getSharedPreferences(getString(R.string.str_saved_info), this.MODE_PRIVATE);

        sLocationString = String.format("https://www.google.com/maps/?q=%s,%s", Util.GetSavedLatitude(this), Util.GetSavedLongitude(this));
        addEventChangeListners();

        sSOSMessage = sharedPref.getString(getString(R.string.str_default_message), getString(R.string.settings_default_message));
        bLocation = sharedPref.getBoolean(getString(R.string.str_enable_location), true);

        txtEditMessage.setText(sSOSMessage);
        chbEditLocation.setChecked(bLocation);
    }

    protected void btnSaveOnClick(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if(!sSOSMessage.equals(txtEditMessage.getText().toString())) {
            editor.putString(getString(R.string.str_default_message), txtEditMessage.getText().toString());

            bIsModified = true;
        }

        if(bLocation != chbEditLocation.isChecked()) {
            editor.putBoolean(getString(R.string.str_enable_location), chbEditLocation.isChecked());

            bIsModified = true;
        }

        editor.putString(getString(R.string.str_sos_message), txtEditTextPreview.getText().toString());
        editor.commit();

        if(bIsModified) {
            Toast.makeText(this, "Changes Saved.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addEventChangeListners() {
        txtEditMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtEditTextPreview.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(chbEditLocation.isChecked()) {
                    txtEditTextPreview.append("\n" + sLocationString);
                }
            }
        });

        chbEditLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    txtEditTextPreview.append("\n" + sLocationString);
                }
                else {
                    txtEditTextPreview.setText(txtEditMessage.getText().toString());
                }
            }
        });
    }
}
