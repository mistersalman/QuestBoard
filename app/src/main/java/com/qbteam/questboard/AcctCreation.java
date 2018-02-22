package com.qbteam.questboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcctCreation extends AppCompatActivity{

    QBUser test;
    Button signUpButton, returnButton;
    EditText inputEmail, inputPass, confirmPass;
    private FirebaseAuth mobileAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acct_creation);

        //EditText values to hold the inputs provided by the user
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPass = (EditText) findViewById(R.id.inputPass);
        confirmPass = (EditText) findViewById(R.id.confirmPass);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        returnButton = (Button) findViewById(R.id.returnButton);

        mobileAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = (String) inputEmail.getText().toString();
                String pass = (String) inputPass.getText().toString();
                String confPass = (String) confirmPass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confPass))
                {
                    if(pass.equals(confPass))
                    {
                        mobileAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                //Sign up and use a completion listener to make sure it went through
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                //Successful authentication, go to activity_main
                                {
                                    QBUser user = new QBUser(email);
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = database.getReference();
                                    String path = "users/" + mobileAuth.getUid().toString();
                                    databaseReference.child(path).setValue(user);
                                    final String userIDPath = "userID/";
                                    databaseReference.child(userIDPath).setValue(mobileAuth.getUid().toString());
                                    goToMain();
                                    //Private method which redirects you to activity_main
                                }
                                else
                                //Invalid authentication, stay on same screen
                                {
                                    Toast.makeText(AcctCreation.this, "Invalid Account info, try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(AcctCreation.this, "Passwords do not match, try again.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(AcctCreation.this, "One or more requirements is blank, try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent( AcctCreation.this,
                        AcctLogin.class);
                startActivity(createIntent);
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
        Intent createIntent = new Intent(AcctCreation.this,
                MainActivity.class);
        startActivity(createIntent);
        finish();
    }
}
