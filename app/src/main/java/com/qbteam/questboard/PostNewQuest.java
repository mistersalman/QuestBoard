package com.qbteam.questboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PostNewQuest extends AppCompatActivity {

    private EditText questTitleEditText, questDescriptionEditText, requirementsEditText, rewardsEditText, tagsEditText;
    private Button postQuestButton;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_quest);

        postQuestButton = (Button) findViewById(R.id.postQuestButton);

        postQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questTitleEditText = (EditText) findViewById(R.id.questTitleEditText);
                questDescriptionEditText = (EditText) findViewById(R.id.questDescriptionEditText);
                requirementsEditText = (EditText) findViewById(R.id.requirementsEditText);
                rewardsEditText = (EditText) findViewById(R.id.rewardsEditText);
                tagsEditText = (EditText) findViewById(R.id.tagsEditText);

                if((!TextUtils.isEmpty(questTitleEditText.getText().toString())) && (!TextUtils.isEmpty(questDescriptionEditText.getText().toString()))
                        && (!TextUtils.isEmpty(requirementsEditText.getText().toString())) && (!TextUtils.isEmpty(rewardsEditText.getText().toString()))
                        && (!TextUtils.isEmpty(tagsEditText.getText().toString()))) //make sure nothing is empty
                {
                    List<String> tags = Arrays.asList(tagsEditText.getText().toString().split("\\s*,\\s*")); //create string list with all
                    mobileAuth = FirebaseAuth.getInstance();
                    currentUser = mobileAuth.getCurrentUser();
                    final String userPath = "users/" + currentUser.getUid().toString() + "/";

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                    String date = simpleDateFormat.format(new Date());

                    String emailNoDot = currentUser.getEmail().replaceAll("[.]", "");

                    final String postID = emailNoDot + date.toString() + "/";
                    System.out.println(postID);

                    final QBPost post = new QBPost(questTitleEditText.getText().toString(), questDescriptionEditText.getText().toString(),
                            requirementsEditText.getText().toString(), rewardsEditText.getText().toString(), tags);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference databaseReference = database.getReference();
                    final String postPath = "posts/" + postID + "/";
                    databaseReference.child(postPath).setValue(post);

                    databaseReference.child(userPath).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            QBUser user = dataSnapshot.getValue(QBUser.class);
                            user.posts.add(postID);
                            databaseReference.child(userPath).child("posts").setValue(user.posts);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    goToMain();
                }
                else
                {
                    Toast.makeText(PostNewQuest.this, "Missing inputs!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToMain()
    {
        Intent createIntent = new Intent(PostNewQuest.this,
                MainActivity.class);
        startActivity(createIntent);
        finish();
    }
}
