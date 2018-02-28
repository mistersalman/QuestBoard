package com.qbteam.questboard;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AccountPageEdit extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 22;
    private static final int PDF_REQUEST_CODE = 23;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    EditText Bio, Name, Education, Age;
    Button viewAcct, changePicture, uploadResume, updateLocation, updateButton;
    ImageView imageView;

    LocationManager locationManager;
    LocationListener locationListener;

    Uri filePath, downloadUrl;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_page);

        Bio = (EditText) findViewById(R.id.Bio);
        Name = (EditText) findViewById(R.id.Name);
        Education = (EditText) findViewById(R.id.Education);
        Age = (EditText) findViewById(R.id.Age);

        viewAcct = (Button) findViewById(R.id.viewButton);
        changePicture = (Button) findViewById(R.id.changePicture);
        uploadResume = (Button) findViewById(R.id.homeButton);
        updateLocation = (Button) findViewById(R.id.updateLocation);
        updateButton = (Button) findViewById(R.id.updateButton);

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

        viewAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(AccountPageEdit.this,
                        AccountPageOwner.class);
                startActivity(createIntent);
                finish();
                //Go to account management page
            }
        });


        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Opens file explorer and looks for an image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //Calls onActivityResult after exit
                startActivityForResult(intent, IMAGE_REQUEST_CODE);

            }
        });

        uploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Opens file explorer and looks for a pdf
                Intent intent = new Intent();
                intent.setType("application/pdf"); //for pdf
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //Calls onActivityResult after exit
                startActivityForResult(intent, PDF_REQUEST_CODE);

            }
        });

        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bio = Bio.getText().toString();
                final String name = Name.getText().toString();
                final String education = Education.getText().toString();
                final int age = Integer.parseInt(Age.getText().toString());

                mobileAuth = FirebaseAuth.getInstance();
                currentUser = mobileAuth.getCurrentUser();
                final String path = "users/" + currentUser.getUid().toString() + "/";

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference.child(path).child("bio").setValue(bio);
                        databaseReference.child(path).child("name").setValue(name);
                        databaseReference.child(path).child("education").setValue(education);
                        databaseReference.child(path).child("age").setValue(age);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                Intent createIntent = new Intent(AccountPageEdit.this,
//                        AccountPageOwner.class);
//                startActivity(createIntent);
//                finish();
                Toast.makeText(AccountPageEdit.this, "Info Updated!", Toast.LENGTH_LONG).show();
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

        Bio = (EditText) findViewById(R.id.Bio);
        Name = (EditText) findViewById(R.id.Name);
        Education = (EditText) findViewById(R.id.Education);
        Age= (EditText) findViewById(R.id.Age);

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
                Age.setText(years, TextView.BufferType.EDITABLE);
                Name.setText(user.getName(), TextView.BufferType.EDITABLE);
                Education.setText(user.getEducation(), TextView.BufferType.EDITABLE);
                Bio.setText(user.getBio(), TextView.BufferType.EDITABLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
