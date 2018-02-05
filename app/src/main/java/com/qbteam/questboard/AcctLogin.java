package com.qbteam.questboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AcctLogin extends AppCompatActivity {

    private Button loginButton, joinButton;
    private EditText inputEmail, inputPass;
    private FirebaseAuth mobileAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acct_login);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPass = (EditText) findViewById(R.id.inputPass);
        //Email and pass EditText values instantiated

        loginButton = (Button) findViewById(R.id.signinButton);
        joinButton = (Button) findViewById(R.id.signupButton);
        //Login and join Button values instantiated

        mobileAuth = FirebaseAuth.getInstance();
        //Mobile Authentication through Firebase instantiated

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = (String) inputEmail.getText().toString();
                String password = (String) inputPass.getText().toString();
                //Get string values of email and password

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                //Make sure these values aren't null
                {
                    mobileAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    //Sign in and use a completion listener to make sure it went through
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            //Successful authentication, go to activity_main
                            {
                                goToMain();
                                //Private method which redirects you to activity_main
                            }
                            else
                            //Invalid authentication, stay on same screen
                            {
                                Toast.makeText(AcctLogin.this, "Invalid Login, Try Again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                //Invalid input failure, stay on same screen
                {
                    Toast.makeText(AcctLogin.this, "Invalid Login, Try Again.", Toast.LENGTH_LONG).show();
                }
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mobileAuth.getCurrentUser();

        if(currentUser != null)
        {
            goToMain();
        }

    }

    private void goToMain()
    {
        Intent createIntent = new Intent(AcctLogin.this,
                MainActivity.class);
        startActivity(createIntent);
        finish();
    }

}
