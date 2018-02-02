package com.qbteam.questboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AcctCreation extends AppCompatActivity {

    Button signUpButton, returnButton;
    EditText inputEmail, inputPass, confirmPass;
    String email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acct_creation);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPass = (EditText) findViewById(R.id.inputPass);
        confirmPass = (EditText) findViewById(R.id.confirmPass);

        email = inputEmail.getText().toString();

        if(inputPass.getText().toString() == confirmPass.getText().toString())
        {
            password = confirmPass.getText().toString();
        }

        /*mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent createIntent = new Intent (AcctCreation.this,
                                    HomeScreen.class);
                            startActivity(createIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                        }

                        // ...
                    }
                });*/

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
