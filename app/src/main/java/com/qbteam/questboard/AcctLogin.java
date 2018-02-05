package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AcctLogin extends AppCompatActivity {

    Button loginButton, joinButton;
    EditText inputEmail, inputPass;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acct_login);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPass = (EditText) findViewById(R.id.inputPass);

        loginButton = (Button) findViewById(R.id.signinButton);
        joinButton = (Button) findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(AcctLogin.this,
                        MainActivity.class);
                startActivity(createIntent);
                //This is a simple construct used to transition from one activity to another
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(AcctLogin.this,
                        AcctCreation.class);
                startActivity(createIntent);
                //This is a simple construct used to transition from one activity to another
            }
        });

    }

}
