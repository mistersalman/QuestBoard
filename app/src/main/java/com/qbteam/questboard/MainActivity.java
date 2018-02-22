package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button acctMgmt, logout, postQuest, viewQuestBoard;
    private FirebaseAuth mobileAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postQuest = (Button) findViewById(R.id.postQuestButton);
        viewQuestBoard = (Button) findViewById(R.id.viewQuestBoardButton);
        acctMgmt = (Button) findViewById(R.id.acctMgmtButton);
        logout = (Button) findViewById(R.id.logoutButton);
        //Instantiate buttons

        mobileAuth = FirebaseAuth.getInstance();
        //Instantiate firebase mAuth

        postQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(MainActivity.this,
                        PostNewQuest.class);
                startActivity(createIntent);
                //Go to account management page
            }
        });

        viewQuestBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(MainActivity.this,
                        QuestList.class);
                startActivity(createIntent);
                //Go to account management page
            }
        });

        acctMgmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(MainActivity.this,
                        AccountPage.class);
                startActivity(createIntent);
                //Go to account management page
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileAuth.signOut();
                //In-API mAuth signout called, deletes mAuth token
                goToLogin();
                //Return to login activity
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Declare and instantiate Firebase user with the current user to check if anyone is logged in

        if(currentUser == null)
        //If no user is logged in, go to activity_acct_login
        {
            goToLogin();
        }
    }

    private void goToLogin()
    //helper method to go to login activity
    {
        Intent createIntent = new Intent(MainActivity.this,
                AcctLogin.class);
        startActivity(createIntent);
        finish();
    }
}
