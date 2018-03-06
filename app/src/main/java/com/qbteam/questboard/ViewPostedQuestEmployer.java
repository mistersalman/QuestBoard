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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPostedQuestEmployer extends AppCompatActivity {

    private TextView questTitleTextView2, questDescriptionTextView2, requirementsTextView2, rewardsTextView2;
    private Button viewApplicantsButton, editQuestButton, closeJobButton, backButton, viewQuestGiverProfileButton;

    String postID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posted_quest_employer);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        //TODO Create corresponding bundle in QuestList
        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        Log.d("id view: ", postID);
        final String postPath = postID.replace("%40", "@");

        viewApplicantsButton = (Button) findViewById(R.id.viewApplicantsButton);
        editQuestButton = (Button) findViewById(R.id.editQuestButton);
        closeJobButton = (Button) findViewById(R.id.closeJobButton);
        backButton = (Button) findViewById(R.id.backButton);
        viewQuestGiverProfileButton = (Button) findViewById(R.id.viewQuestGiverProfileButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createIntent = new Intent(ViewPostedQuestEmployer.this,
                        QuestList.class);
                startActivity(createIntent);
//                ViewPostedQuestEmployee.super.onBackPressed();
            }
        });

        viewApplicantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(ViewPostedQuestEmployer.this, ViewQuestApplicants.class);
                Bundle bundle = new Bundle();
                bundle.putString("postID", postID);
                Log.d("PostID", postID);
                viewIntent.putExtras(bundle);
                startActivity(viewIntent);
                finish();
            }
        });

        editQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(ViewPostedQuestEmployer.this, EditPostedQuest.class);
                Bundle bundle = new Bundle();
                bundle.putString("postID", postID);
                intentEdit.putExtras(bundle);
                startActivity(intentEdit);
                finish();
            }
        });

        closeJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("any key name", dataSnapshot.toString());
                        databaseReference.child(postPath).child("completed").setValue(true);

                        Toast.makeText(ViewPostedQuestEmployer.this, "You have closed the quest!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        viewQuestGiverProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(ViewPostedQuestEmployer.this,
                        AccountPageOwner.class);
                startActivity(createIntent);
//                finish();
            }
        });
    }

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
