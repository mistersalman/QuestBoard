package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

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

        applyEditQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(applyEditQuestButton.getText().toString().compareTo("Edit") == 0)
                {
                    Intent intentEdit = new Intent(ViewPostedQuest.this, EditPostedQuest.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", postID);
                    intentEdit.putExtras(bundle);
                    startActivity(intentEdit);
                }
                else
                {
                    Intent intentList = new Intent(ViewPostedQuest.this, QuestList.class);
                    startActivity(intentList);
                }
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
        tagsTextView2 = (TextView) findViewById(R.id.tagsTextView);

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();

        //TODO Create corresponding bundle in QuestList
//    if(extrasBundle != null)
//    {
//        postID = extrasBundle.getString("postID", QuestList.POST_ID);
//    }
        //TODO get values from DB through post ID

        checkButtonText();

    }

    private void checkButtonText()
    {
        applyEditQuestButton = (Button) findViewById(R.id.applyEditQuestButton);
        String path = "users/" + currentUser.getUid().toString() + "/";

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QBUser user = dataSnapshot.getValue(QBUser.class);
                Log.d("any key name", dataSnapshot.toString());
                for(String s : user.getPosts())
                {
                    if(s.compareTo(postID) == 0)
                    {
                        applyEditQuestButton.setText("Edit");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
