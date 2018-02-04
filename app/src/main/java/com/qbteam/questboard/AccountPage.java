package com.qbteam.questboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class AccountPage extends AppCompatActivity {

    EditText bio;
    Button changePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        bio = (EditText) findViewById(R.id.bio);

        changePicture = (Button) findViewById(R.id.changePicture);



    }

}
