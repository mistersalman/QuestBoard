package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPostedQuestEmployee extends AppCompatActivity {
    private TextView questTitleTextView2, questDescriptionTextView2, requirementsTextView2, rewardsTextView2;
    private Button applyQuestButton, viewQuestGiverProfileButton, backButton;

    String postID;
    private FirebaseAuth mobileAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posted_quest_employee);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        //TODO Create corresponding bundle in QuestList
        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        Log.d("id view: ", postID);
        final String postPath = postID.replace("%40", "@");

        applyQuestButton = (Button) findViewById(R.id.applyQuestButton);
        viewQuestGiverProfileButton = (Button) findViewById(R.id.viewQuestGiverProfileButton);
        backButton = (Button) findViewById(R.id.backButton);

        applyQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QBPost post = dataSnapshot.getValue(QBPost.class);
                        mobileAuth = FirebaseAuth.getInstance();
                        Log.d("applicant id", mobileAuth.getUid().toString());
                        post.applicantIDs.add(mobileAuth.getUid().toString());
                        databaseReference.child(postPath).setValue(post);
                        Toast.makeText(ViewPostedQuestEmployee.this, "You have applied to the quest!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        viewQuestGiverProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(ViewPostedQuestEmployee.this, QuestList.class);
                startActivity(intentEdit);
                finish();
            }
        });
    }// TODO: 2/27/2018 Copy paste from viewpostedquestemployer

    @Override
    protected void onStart() {
        super.onStart();

        questTitleTextView2 = (TextView) findViewById(R.id.questTitleTextView2);
        questDescriptionTextView2 = (TextView) findViewById(R.id.questDescriptionTextView2);
        requirementsTextView2 = (TextView) findViewById(R.id.requirementsTextView2);
        rewardsTextView2 = (TextView) findViewById(R.id.rewardsTextView2);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        //TODO Create corresponding bundle in QuestList
        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }
        //TODO get values from DB through post ID
        Log.d("id view: ", postID);
        String postPath = postID.replace("%40", "@");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QBPost post = dataSnapshot.getValue(QBPost.class);
                Log.d("any key name", dataSnapshot.toString());
                questTitleTextView2.setText(post.getTitle());
                questDescriptionTextView2.setText(post.getDescription());
                requirementsTextView2.setText(post.getRequirements());
                rewardsTextView2.setText(post.getRewards());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
