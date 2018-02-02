package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AcctCreation extends AppCompatActivity {

    Button signUpButton, returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acct_creation);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        returnButton = (Button) findViewById(R.id.returnButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent( AcctCreation.this,
                        HomeScreen.class);
                startActivity(createIntent);
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent( AcctCreation.this,
                        MainActivity.class);
                startActivity(createIntent);
            }
        });
    }
}
