package com.qbteam.questboard;

import android.app.Activity;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Clinton on 3/3/2018.
 */

public class Ratings extends Activity {

    QBPost currentPost = new QBPost();
    QBUser user = new QBUser();
    String userID = new String();
    String postID = new String();
    String userType = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ratings_popup_window);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*.50));

        final RatingBar ratingBar;
        Button submitButton;

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        submitButton = (Button) findViewById(R.id.submitButton);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            userID = extrasBundle.getString("userID", userID);
            postID = extrasBundle.getString("postID", postID);
            userType = extrasBundle.getString("userType", userType);
        }

        Log.d("user id view: ", userID);
        Log.d("post id view: ", postID);
        Log.d("user type view: ", userType);

        final String postPath = postID.replace("%40", "@");

        Log.d("user email view: ", user.getEmail());
        final FirebaseDatabase databasePost = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReferencePost = databasePost.getReference();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Float rating = ratingBar.getRating();
                if(userType.equals("employee"))
                {
                    final String pathUser = "users/" + userID + "/";
                    FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReferenceUser = databaseUser.getReference();
                    databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(QBUser.class);
                            databaseReferencePost.child(postPath).child("rated").setValue(true);
                            user.addRating(rating);
                            databaseReferencePost.child(pathUser).setValue(user);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    final String pathUser = "users/" + userID + "/";
                    FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReferenceUser = databaseUser.getReference();
                    databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(QBUser.class);
                            databaseReferencePost.child(postPath).child("completed").setValue(true);
                            user.addRating(rating);
                            databaseReferencePost.child(pathUser).setValue(user);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                finish();
            }
        });

    }
}
