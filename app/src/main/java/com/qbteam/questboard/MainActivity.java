package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button loginButton, joinButton;
    EditText inputEmail, inputPass;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPass = (EditText) findViewById(R.id.inputPass);

        loginButton = (Button) findViewById(R.id.signinButton);
        joinButton = (Button) findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //No null inputs
                if ((inputEmail.getText().toString() != null) &&
                        (inputPass.getText().toString() != null))
                {
                    email = inputEmail.getText().toString();
                    password = inputPass.getText().toString();
                    //TODO CHECK FOR VALID ACCT
                    //TODO LOG USER IN
                    Intent createIntent = new Intent(MainActivity.this,
                            HomeScreen.class);
                    startActivity(createIntent);
                }
                else
                {
                    //TODO DISPLAY POPUP FOR BAD LOGIN
                }
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(MainActivity.this,
                        AcctCreation.class);
                startActivity(createIntent);
                //This is a simple construct used to transition from one activity to another
            }
        });

    }
}
