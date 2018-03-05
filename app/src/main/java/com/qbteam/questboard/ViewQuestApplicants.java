package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    ArrayList<String> applicantList = new ArrayList<String>();
    ArrayList<String> tempList = new ArrayList<String>();
    ArrayList<String>  employeeID = new ArrayList<String>();

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

        applist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("id view: ", tempList.get(position));
                Intent viewIntent = new Intent(ViewQuestApplicants.this, AccountPageEmployer.class);
                Bundle bundle = new Bundle();
                bundle.putString("postID", tempList.get(position));
                bundle.putString("employeeID", employeeID.get(position));
                viewIntent.putExtras(bundle);
                startActivity(viewIntent);
//                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(ViewQuestApplicants.this, ViewPostedQuestEmployer.class);
                Bundle bundle = new Bundle();
                bundle.putString("postID", postID);
                viewIntent.putExtras(bundle);
                startActivity(viewIntent);
                finish();
//                ViewQuestApplicants.super.onBackPressed();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        applicantList.clear();
        applist = (ListView) findViewById(R.id.applicantList);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        final String postPath = postID.replace("%40", "@");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                tempList = (ArrayList<String>)dataSnapshot.child("/applicantIDs/").getValue();

                for (int i = 0; i < tempList.size(); i++)
                {
                    String pathUser = "users/" + tempList.get(i) + "/";

                    DatabaseReference databaseReferenceUser = database.getReference();
                    databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            user = dataSnapshot.getValue(QBUser.class);
                            applicantList.add(user != null ?  user.getName() : "");
                            employeeID.add(dataSnapshot.getKey());

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewQuestApplicants.this, android.R.layout.simple_list_item_1, applicantList);
                            applist.setAdapter(arrayAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
