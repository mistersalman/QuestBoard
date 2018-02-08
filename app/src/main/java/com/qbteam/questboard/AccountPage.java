package com.qbteam.questboard;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AccountPage extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 22;
    private static final int PDF_REQUEST_CODE = 23;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    TextView Bio, Name, Education, Age;
    Button editAcct, goHome;
    ImageView imageView;

    Uri filePath, downloadUrl;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        editAcct = (Button) findViewById(R.id.editButton);
        goHome = (Button) findViewById(R.id.homeButton);

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();

        StorageReference pathRef = storageRef.child(currentUser.getEmail()+"/profile.png");
        imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(pathRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);

        editAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(AccountPage.this,
                        AccountPageEdit.class);
                startActivity(createIntent);
                //Go to account management page
            }
        });

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(AccountPage.this,
                        MainActivity.class);
                startActivity(createIntent);
                finish();
                //Go to account management page

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
        StorageReference pathRef = storageRef.child(currentUser.getEmail()+"/profile.png");
        imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this)
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
            StorageReference fileRef = storageRef.child(currentUser.getEmail()+"/profile.png");
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
            StorageReference fileRef = storageRef.child(currentUser.getEmail()+"/resume.pdf");
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

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();
        String path = "users/" + currentUser.getUid().toString() + "/";

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
