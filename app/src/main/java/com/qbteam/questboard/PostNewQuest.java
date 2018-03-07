package com.qbteam.questboard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Locale;

public class PostNewQuest extends AppCompatActivity implements LocationListener{

    private EditText questTitleEditText, questDescriptionEditText, requirementsEditText, rewardsEditText, tagsEditText;
    private Button addLocationButton, postQuestButton, backButton;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;
    double latitude = 0;
    double longitude = 0;
    String address = "";
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_quest);

        addLocationButton = (Button) findViewById(R.id.addLocationButton);
        postQuestButton = (Button) findViewById(R.id.postQuestButton);
        backButton = (Button) findViewById(R.id.backButton);

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PostNewQuest.super.onBackPressed();
//            }
//        });

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActivityCompat.requestPermissions(AccountPageEdit.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                if(checkLocationPermission())
//                {
//
//                    l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                }
//                Log.d("Latitude", Double.toString(l.getLatitude()));
//                Log.d("Longitude", Double.toString(l.getLongitude()));
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PostNewQuest.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                }

                getLocation();
                Toast.makeText(PostNewQuest.this, "Location Updated!", Toast.LENGTH_LONG).show();

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostNewQuest.super.onBackPressed();
            }
        });

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
                            requirementsEditText.getText().toString(), rewardsEditText.getText().toString(), tags, mobileAuth.getUid().toString(), latitude, longitude, address);
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        Log.d("lat and long", Double.toString(location.getLatitude()) + Double.toString(location.getLongitude()));

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            //locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
            //addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
            Log.d("new lat and long", Double.toString(location.getLatitude()) + Double.toString(location.getLongitude()));
            Log.d("address", addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2));
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
            Log.d("address:", address);
            Log.d("stored latitude", Double.toString(latitude));
            Log.d("stored longitude", Double.toString(longitude));
        }
        catch(Exception e)
        {

        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(PostNewQuest.this, "ENABLE GPS BRUH", Toast.LENGTH_SHORT).show();
    }

    private void getLocation()
    {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }
}
