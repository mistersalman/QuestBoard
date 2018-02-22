package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ViewPostedQuest extends AppCompatActivity {

    private TextView questTitleTextView2, questDescriptionTextView2, requirementsTextView2, rewardsTextView2, tagsTextView2;
    private Button applyEditQuestButton;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    Intent intentBundle = getIntent();
    Bundle extrasBundle = intentBundle.getExtras();

    String postID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posted_quest);
        applyEditQuestButton = (Button) findViewById(R.id.applyEditQuestButton);

        
    }

    @Override
    protected void onStart() {
        super.onStart();

        questTitleTextView2 = (TextView) findViewById(R.id.questTitleTextView2);
        questDescriptionTextView2 = (TextView) findViewById(R.id.questDescriptionTextView2);
        requirementsTextView2 = (TextView) findViewById(R.id.requirementsTextView2);
        rewardsTextView2 = (TextView) findViewById(R.id.rewardsTextView2);
        tagsTextView2 = (TextView) findViewById(R.id.tagsTextView);

        //TODO Create corresponding bundle in QuestList
//    if(extrasBundle != null)
//    {
//        postID = extrasBundle.getString("postID", QuestList.POST_ID);
//    }
        //TODO get values from DB through post ID

    }
}
