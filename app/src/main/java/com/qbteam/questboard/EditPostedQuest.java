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
import java.util.Locale;

public class EditPostedQuest extends AppCompatActivity implements LocationListener{

    private EditText questTitleEditText, questDescriptionEditText, requirementsEditText, rewardsEditText, tagsEditText;
    private Button addLocationButton, updateQuestButton;
    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;
    LocationManager locationManager;
    double latitude = 0;
    double longitude = 0;

    String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_posted_quest);

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

        if(extrasBundle != null)
        {
            postID = extrasBundle.getString("postID", postID);
        }

        addLocationButton = (Button) findViewById(R.id.addLocationButton);
        updateQuestButton = (Button) findViewById(R.id.updateQuestButton);

        final String postPath = postID.replace("%40", "@");
        Log.d("id edit: ", postID);

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
                    ActivityCompat.requestPermissions(EditPostedQuest.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                }

                getLocation();
                Toast.makeText(EditPostedQuest.this, "Location Updated!", Toast.LENGTH_LONG).show();

            }
        });

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
                        databaseReference.child(postPath).child("latitude").setValue(latitude);
                        databaseReference.child(postPath).child("longitude").setValue(longitude);
                        Toast.makeText(EditPostedQuest.this, "Info Updated!", Toast.LENGTH_LONG).show();

                        Intent intentEdit = new Intent(EditPostedQuest.this, ViewPostedQuestEmployer.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("postID", postID);
                        intentEdit.putExtras(bundle);
                        startActivity(intentEdit);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentBundle = getIntent();
        Bundle extrasBundle = intentBundle.getExtras();

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

        String postPath = postID.replace("%40", "@");

        Log.d("postID: ", postPath);

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
        Toast.makeText(EditPostedQuest.this, "ENABLE GPS BRUH", Toast.LENGTH_SHORT).show();
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
