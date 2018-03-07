package com.qbteam.questboard;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

public class AccountPageEmployer extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 22;
    private static final int PDF_REQUEST_CODE = 23;
    float averageRating = 0;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    TextView Bio, Name, Education, Age, locationTextView2;
    Button editAcct, goBack, downloadResume, goRatings, hireButton, contactButton;
    ImageView imageView;
    RatingBar Ratings;

    Uri filePath, downloadUrl;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    QBUser employeeUser = new QBUser();
    QBPost currentPost = new QBPost();
    String userID;
    String username;
    String postID;
    String userType = "employer";

    double latitude = 0.0;
    double longitude = 0.0;
    float distance = (float) 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page_employer);

        goBack = (Button) findViewById(R.id.backButton);
        downloadResume = (Button) findViewById(R.id.downloadResume);
        goRatings = (Button) findViewById(R.id.rateButton);
        hireButton = (Button) findViewById(R.id.hireButton);

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            userID = extrasBundle.getString("employeeID", userID);
            postID = extrasBundle.getString("postID", postID);
        }

        final String postPath = postID.replace("%40", "@");

        String pathUser = "users/" + userID + "/";
        FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceUser = databaseUser.getReference();
        databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employeeUser = dataSnapshot.getValue(QBUser.class);

                StorageReference pathRef = storageRef.child(employeeUser.getEmail()+"/profile.png");
                imageView = (ImageView) findViewById(R.id.imageView);
                Glide.with(AccountPageEmployer.this)
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
                AccountPageEmployer.super.onBackPressed();
                finish();
                //Go to account management page
            }
        });

        downloadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference resumePath = storageRef.child(employeeUser.getEmail()+"/" + employeeUser.getEmail()+"resume.pdf");
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
                        Toast.makeText(AccountPageEmployer.this, "Resume does not exist", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        goRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currentPost.hiredID().equals(userID))
                {
                    Toast.makeText(AccountPageEmployer.this, "You did not hire this user for this quest", Toast.LENGTH_LONG).show();

                }
                else if(currentPost.getCompleted())
                {
                    Toast.makeText(AccountPageEmployer.this, "You have already rated this user", Toast.LENGTH_LONG).show();

                }
                else
                {
                    Intent intentEdit = new Intent(AccountPageEmployer.this, Ratings.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", userID);
                    bundle.putString("postID", postID);
                    bundle.putString("userType", userType);
                    intentEdit.putExtras(bundle);
                    startActivity(intentEdit);
                }
            }
        });

        hireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPost.getHired())
                {
                    Toast.makeText(AccountPageEmployer.this, "You have already hired a user for this quest", Toast.LENGTH_LONG).show();
                }
                else
                {
                    databaseReferencePost.child(postPath).child("hired").setValue(true);
                    databaseReferencePost.child(postPath).child("hiredID").setValue(userID);
                    Toast.makeText(AccountPageEmployer.this, "You have hired this user for your quest", Toast.LENGTH_LONG).show();

                }
            }
        });

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();
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
                        Intent createIntent = new Intent(AccountPageEmployer.this,
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

        StorageReference pathRef = storageRef.child(employeeUser.getEmail()+"/profile.png");
        imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(AccountPageEmployer.this)
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
            StorageReference fileRef = storageRef.child(employeeUser.getEmail()+"/profile.png");
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
            StorageReference fileRef = storageRef.child(employeeUser.getEmail()+"/resume.pdf");
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
        locationTextView2 = (TextView) findViewById(R.id.locationTextView2);

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

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();
        path = "users/" + userID + "/";

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

                Location loc_employer = new Location("");
                loc_employer.setLatitude(user.latitude);
                loc_employer.setLongitude(user.longitude);
                Location loc_employee = new Location("");
                loc_employee.setLatitude(latitude);
                loc_employee.setLongitude(longitude);
                distance = loc_employer.distanceTo(loc_employee) * (float) 0.000621371;
                String addr = user.getAddress() + " (" + Float.toString(distance) + " miles away)";
                locationTextView2.setText(addr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
