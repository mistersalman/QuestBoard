package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class EditPostedQuest extends AppCompatActivity {

    private EditText questTitleEditText, questDescriptionEditText, requirementsEditText, rewardsEditText, tagsEditText;
    private Button updateQuestButton;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    Intent intentBundle = getIntent();
    Bundle extrasBundle = intentBundle.getExtras();

    String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_posted_quest);

        updateQuestButton = (Button) findViewById(R.id.updateQuestButton);

        final String postPath = "posts/" + postID + "/";

        updateQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference.child(postPath).child("title").setValue(questTitleEditText.getText().toString());
                        databaseReference.child(postPath).child("description").setValue(questDescriptionEditText.getText().toString());
                        databaseReference.child(postPath).child("requirements").setValue(requirementsEditText.getText().toString());
                        databaseReference.child(postPath).child("rewards").setValue(rewardsEditText.getText().toString());
                        List<String> tags = Arrays.asList(tagsEditText.getText().toString().split("\\s*,\\s*"));
                        databaseReference.child(postPath).child("tags").setValue(tags);
                        Toast.makeText(EditPostedQuest.this, "Info Updated!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(EditPostedQuest.this, ViewPostedQuest.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                Intent createIntent = new Intent(AccountPageEdit.this,
//                        AccountPage.class);
//                startActivity(createIntent);
//                finish();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        questTitleEditText = (EditText) findViewById(R.id.questTitleEditText);
        questDescriptionEditText = (EditText) findViewById(R.id.questDescriptionEditText);
        requirementsEditText = (EditText) findViewById(R.id.requirementsEditText);
        rewardsEditText = (EditText) findViewById(R.id.rewardsEditText);
        tagsEditText = (EditText) findViewById(R.id.tagsEditText);

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();

        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        String postPath = "posts/" + postID + "/";

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QBPost post = dataSnapshot.getValue(QBPost.class);
                Log.d("any key name", dataSnapshot.toString());
                questTitleEditText.setText(post.getTitle(), TextView.BufferType.EDITABLE);
                questDescriptionEditText.setText(post.getDescription(), TextView.BufferType.EDITABLE);
                requirementsEditText.setText(post.getRequirements(), TextView.BufferType.EDITABLE);
                rewardsEditText.setText(post.getRewards(), TextView.BufferType.EDITABLE);
                String tag = "";

                for(int i = 0; i < post.getTags().size(); i ++)
                {
                    tag += post.getTags().get(i);
                    if(i != post.getTags().size() - 1)
                    {
                        tag += ", ";
                    }
                }
                tagsEditText.setText(tag, TextView.BufferType.EDITABLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
