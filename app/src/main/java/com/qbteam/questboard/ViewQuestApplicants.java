package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestApplicants extends AppCompatActivity {

    ListView applist;
    Button back;

    List<String> applicantList = new ArrayList();
    List<String> tempList = new ArrayList();

    ArrayList<String> idList = new ArrayList();
    ArrayList<String> copyIdList = new ArrayList();
    List<String> userPostID = new ArrayList<>();

    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;
    String postID;
    QBUser user = new QBUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quest_applicants);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        applist = (ListView) findViewById(R.id.applicantList);
        back = (Button) findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(ViewQuestApplicants.this,
                        ViewPostedQuestEmployer.class);
                startActivity(createIntent);
                finish();
                //Go to account management page
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        /*
        creating the questlist, also known as itemList from the xml file. I'm 90% sure that this is how you access it. The documentation on the ListView is actually god fucking awful. So, yaknow.
         */
        applist = (ListView) findViewById(R.id.applicantList);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        final String postPath = postID.replace("%40", "@");

        //I think this is the part that's giving me my error, but I'm not sure, I mostly just got it from your accountPage file
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                QBPost post = dataSnapshot.getValue(QBPost.class);
                //Log.d("applicant shit", post.getTitle());
                tempList = post.getApplicants();

                for(int i = 0; i < tempList.size(); i++)
                {
                    String pathUser = "users/" + tempList.get(i) + "/";

                    final FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReferenceUser = databaseUser.getReference();
                    databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(QBUser.class);
                            applicantList.add(user.getName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewQuestApplicants.this, android.R.layout.simple_list_item_1, applicantList);
                applist.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
