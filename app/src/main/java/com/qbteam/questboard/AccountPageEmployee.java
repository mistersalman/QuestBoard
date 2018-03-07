package com.qbteam.questboard;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class AccountPageEmployee extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 22;
    private static final int PDF_REQUEST_CODE = 23;
    float averageRating = 0;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    TextView Bio, Name, Education, Age;
    Button editAcct, goBack, downloadResume, goRatings, contactButton;
    ImageView imageView;
    RatingBar Ratings;

    Uri filePath, downloadUrl;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    QBUser employerUser = new QBUser();
    QBPost currentPost = new QBPost();
    String userID;
    String username;
    String postID;
    String userType = "employee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page_employee);

        goBack = (Button) findViewById(R.id.backButton);
        downloadResume = (Button) findViewById(R.id.downloadResume);
        goRatings = (Button) findViewById(R.id.rateButton);

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            userID = extrasBundle.getString("employerID", userID);
            postID = extrasBundle.getString("postID", postID);
        }

        String postPath = postID.replace("%40", "@");

        String pathUser = "users/" + userID + "/";
        FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceUser = databaseUser.getReference();
        databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employerUser = dataSnapshot.getValue(QBUser.class);

                StorageReference pathRef = storageRef.child(employerUser.getEmail()+"/profile.png");
                imageView = (ImageView) findViewById(R.id.imageView);
                Glide.with(AccountPageEmployee.this)
                        .using(new FirebaseImageLoader())
                        .load(pathRef)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseDatabase databasePost = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReferencePost = databasePost.getReference();
        databaseReferencePost.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentPost = dataSnapshot.getValue(QBPost.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        StorageReference pathRef = storageRef.child(employerUser.getEmail()+"/profile.png");
        imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(pathRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
        */

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent createIntent = new Intent(AccountPageOwner.this,
//                        MainActivity.class);
//                startActivity(createIntent);
//                finish();
                AccountPageEmployee.super.onBackPressed();
                finish();
                //Go to account management page
            }
        });

        downloadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference resumePath = storageRef.child(employerUser.getEmail()+"/" + employerUser.getEmail()+"resume.pdf");
                resumePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        i.setPackage("com.android.chrome");
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException a) {
                            i.setPackage(null);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountPageEmployee.this, "Resume does not exist", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        goRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currentPost.hiredID().equals(currentUser.getUid()))
                {
                    Toast.makeText(AccountPageEmployee.this, "You were not hired for this quest", Toast.LENGTH_LONG).show();
                }
                else if(currentPost.getRated())
                {
                    Toast.makeText(AccountPageEmployee.this, "You have already rated this user", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intentEdit = new Intent(AccountPageEmployee.this, Ratings.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", userID);
                    bundle.putString("postID", postID);
                    bundle.putString("userType", userType);
                    intentEdit.putExtras(bundle);
                    startActivity(intentEdit);
                }
            }
        });

        String path = "users/" + currentUser.getUid().toString() + "/";
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QBUser user = dataSnapshot.getValue(QBUser.class);
                username = user.getName();
                contactButton = (Button) findViewById(R.id.contactButton);
                contactButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent createIntent = new Intent(AccountPageEmployee.this,
                                TwilioMessaging.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        createIntent.putExtras(bundle);

                        startActivity(createIntent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Get current user
        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();

        //Gets and loads current user's profile.png into imageview

        StorageReference pathRef = storageRef.child(employerUser.getEmail()+"/profile.png");
        imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(AccountPageEmployee.this)
                .using(new FirebaseImageLoader())
                .load(pathRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if image is selected
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            //Uploads image as profile.png, works for jpg as well
            //Gets path of image
            filePath = data.getData();
            //Set destination
            StorageReference fileRef = storageRef.child(employerUser.getEmail()+"/profile.png");
            //Uploads the file
            UploadTask uploadTask = fileRef.putFile(filePath);
            //Runs depending on upload success or failure, current ain't doing anything
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });

        }

        if(requestCode == PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            //Uploads image as profile.png, works for jpg as well
            //Gets path of image
            filePath = data.getData();
            //Set destination
            StorageReference fileRef = storageRef.child(employerUser.getEmail()+"/resume.pdf");
            //Uploads the file
            UploadTask uploadTask = fileRef.putFile(filePath);
            //Runs depending on upload success or failure, current ain't doing anything
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bio = (TextView) findViewById(R.id.Bio);
        Name = (TextView) findViewById(R.id.Name);
        Education = (TextView) findViewById(R.id.Education);
        Age= (TextView) findViewById(R.id.Age);
        Ratings = (RatingBar) findViewById(R.id.ratingBar);

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();
        String path = "users/" + userID + "/";

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QBUser user = dataSnapshot.getValue(QBUser.class);
                Log.d("any key name", dataSnapshot.toString());
                String years = Integer.toString(user.getAge());
                Age.setText(years);
                Name.setText(user.getName());
                Education.setText(user.getEducation());
                Bio.setText(user.getBio());
                averageRating = user.getTotalStars() / user.getNumberOfRatings();
                if(user.getNumberOfRatings() == 0)
                {
                    averageRating = (float)5;
                }
                Ratings.setRating(averageRating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
