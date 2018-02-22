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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mark Spencer on 2/21/2018.
 */

public class QuestList extends AppCompatActivity {
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Button profile, newQuest;
    ListView questList;

    ArrayList<String> titles = new ArrayList<String>();;
    ArrayList<String> descriptions = new ArrayList<String>();;
    ArrayList<String> postID = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_list);

        /*
        These are all the buttons, you should probably be able to see that pretty easy
         */
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
        /*
        creating the questlist, also known as itemList from the xml file. I'm 90% sure that this is how you access it. The documentation on the ListView is actually god fucking awful. So, yaknow.
         */
        questList = (ListView) findViewById(R.id.itemList);
        final String path = "posts/";


        //I think this is the part that's giving me my error, but I'm not sure, I mostly just got it from your accountPage file
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //I'm not sure why I need final and to make it an array, but whatever, that's the only way it would work.
                final int[] i = {0};
                //I believe this is the way to iterate through the children on the path of Posts, but I could very well have done this wrong
                for (DataSnapshot ds : dataSnapshot.child(path).getChildren()) {
                    String key = (String) ds.getRef().toString();

                    titles.add(key);
                    titles.add("test");
                    //DatabaseReference keyReference = FirebaseDatabase.getInstance().getReference().child("posts/").child(key);
                    DatabaseReference keyReference = ds.getRef();
                    //titles.add(dataSnapshot.child("/title").getValue(String.class));
                    //descriptions.add(dataSnapshot.child("/description").getValue(String.class));
//                    keyReference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            //I am storing the data snapshot data in an array of posts (for possible future use) and in the titles/description string lists for actual usage right now
//                            posts[i[0]].title = dataSnapshot.child("/title").getValue(String.class);
//                            titles[i[0]] = posts[i[0]].getTitle();
//                            posts[i[0]].description = dataSnapshot.child("/description").getValue(String.class);
//                            descriptions.add(dataSnapshot.child("/description").getValue(String.class));
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
                }
                //this should be where I assign the array adapter to display the titles for now. I had to find this layout, and add QuestList into the Android manifest.
                // The normal syntax I found was just "this, R.layout, etc" but for some reason it didn't work

                //List<String> titlelist = new ArrayList<String>(Arrays.asList(titles));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuestList.this, android.R.layout.simple_list_item_1, titles);
                questList.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
