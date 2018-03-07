package com.qbteam.questboard;

import android.content.Intent;
import android.location.Location;
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

public class ViewPostedQuestEmployee extends AppCompatActivity {
    private TextView questTitleTextView2, questDescriptionTextView2, locationTextView, requirementsTextView2, rewardsTextView2;
    private Button applyQuestButton, viewQuestGiverProfileButton, backButton;

    String postID;
    private FirebaseAuth mobileAuth;
    private FirebaseUser currentUser;

    double latitude = 0.0;
    double longitude = 0.0;
    float distance = (float) 0.0;

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createIntent = new Intent(ViewPostedQuestEmployee.this,
                        QuestList.class);
                startActivity(createIntent);
//                ViewPostedQuestEmployee.super.onBackPressed();
            }
        });

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
                        boolean appliedTo = false;
                        Log.d("applicant id", mobileAuth.getUid().toString());
                        for(String appID : post.applicantIDs)
                        {
                            if(mobileAuth.getUid().toString().compareTo(appID) == 0)
                            {
                                appliedTo = true;
                            }
                        }
                        if(!appliedTo)
                        {
                            post.applicantIDs.add(mobileAuth.getUid().toString());
                            databaseReference.child(postPath).setValue(post);
                            Toast.makeText(ViewPostedQuestEmployee.this, "You have applied to the quest!", Toast.LENGTH_LONG).show();
                            appliedTo = true;
                        }
                        else
                        {
                            Toast.makeText(ViewPostedQuestEmployee.this, "You have already applied to this quest!", Toast.LENGTH_LONG).show();
                        }
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
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QBPost post = dataSnapshot.getValue(QBPost.class);
                        Intent intentEdit = new Intent(ViewPostedQuestEmployee.this, AccountPageEmployee.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("employerID", post.getPosterID());
                        bundle.putString("postID", postID);
                        intentEdit.putExtras(bundle);
                        startActivity(intentEdit);
//                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }// TODO: 2/27/2018 Copy paste from viewpostedquestemployer

    @Override
    protected void onStart() {
        super.onStart();

        questTitleTextView2 = (TextView) findViewById(R.id.questTitleTextView2);
        questDescriptionTextView2 = (TextView) findViewById(R.id.questDescriptionTextView2);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
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

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();
        String path = "users/" + currentUser.getUid().toString() + "/";

        final FirebaseDatabase database_you = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference_you = database_you.getReference();
        databaseReference_you.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QBUser user = dataSnapshot.getValue(QBUser.class);
                Log.d("any key name", dataSnapshot.toString());
                latitude = user.latitude;
                longitude = user.longitude;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

                Location loc_post = new Location("");
                loc_post.setLatitude(post.latitude);
                loc_post.setLongitude(post.longitude);
                Location loc_user = new Location("");
                loc_user.setLatitude(latitude);
                loc_user.setLongitude(longitude);
                distance = loc_user.distanceTo(loc_post) * (float) 0.000621371;
                String addr = post.getAddress() + " (" + Float.toString(distance) + " miles away)";
                locationTextView.setText(addr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
