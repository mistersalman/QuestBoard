package com.qbteam.questboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Mark Spencer on 2/21/2018.
 */

public class QuestList extends AppCompatActivity {
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Button profile, newQuest;
    ListView questList;

    QBPost posts[];
    String titles[];
    String descriptions[];

    FirebaseAuth mobileAuth;
    FirebaseUser currentUser = mobileAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_list);

        FirebaseApp.initializeApp(this);

        profile = (Button) findViewById(R.id.profile);
        newQuest = (Button) findViewById(R.id.newQuest);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(QuestList.this,
                        AccountPage.class);
                startActivity(createIntent);
                finish();
                //Go to account management page
            }
        });

        newQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(QuestList.this,
                        PostNewQuest.class);
                startActivity(createIntent);
                finish();
                //Go to account management page

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

        questList = (ListView) findViewById(R.id.itemList);
        String path = "posts";

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int[] i = {0};
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = (String) ds.getKey();

                    DatabaseReference keyReference = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
                    keyReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            posts[i[0]].title = dataSnapshot.child("title").getValue(String.class);
                            titles[i[0]] = posts[i[0]].getTitle();
                            posts[i[0]].description = dataSnapshot.child("description").getValue(String.class);
                            descriptions[i[0]] = posts[i[0]].getDescription();
                            posts[i[0]].rewards = dataSnapshot.child("rewards").getValue(String.class);
                            i[0] += 1;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuestList.this, R.layout.quest_list, R.id.itemList, titles);
                questList.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
