package com.example.mealshared.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Parser;
import com.example.mealshared.Models.Post;
import com.example.mealshared.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostTweetActivity extends AppCompatActivity {
    private String Email;
    private Button postButton;
    private EditText Str_context;
    private String PureString;
    private String place=null;
    private TextView textViewLocation;
    LocationManager locationManager;
    LocationListener locationListener;
    final DatabaseHelper DB = DatabaseHelper.getInstance();
    private RxPermissions rxPermissions;
    private boolean hasPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.activity_post_tweet);
        Email = (getIntent().getStringExtra("Email"));
        Str_context = findViewById(R.id.post_content);
        postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(PostListener);
        textViewLocation = (TextView) findViewById(R.id.location_post);
        textViewLocation.setOnClickListener(LocationChangeListener);
        hasPermissions = false;
        //check for location IO permission
        checkVersion();




    }

    private String FindLocation() {
        locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onLocationChanged(@NonNull Location location) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(addressList!=null && addressList.size()>0) {

                        place = "";

                        if(addressList.get(0).getThoroughfare()!=null) {
                            place = place + addressList.get(0).getThoroughfare() + " ";
                        }

                        if(addressList.get(0).getLocality()!=null) {
                            place = place + addressList.get(0).getLocality() + " ";
                        }

                        if(addressList.get(0).getAdminArea()!=null) {
                            place = place + addressList.get(0).getAdminArea();
                        }

//                        Toast.makeText(getApplicationContext(), place, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        return place;
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkVersion() {
        //Android6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
            rxPermissions = new RxPermissions(this);
            //Permation
            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(granted -> {
                        if (granted) {
                            showMsg("Location Permissions enabled");
                            hasPermissions = true;
                        } else {
                            showMsg("Location Permissions not enabled");
                            hasPermissions = false;
                        }
                    });
        } else {
            showMsg("No need to request dynamic permissions");
        }
    }




    public View.OnClickListener PostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String PostedTime =  formatter.format(date);
            PureString = Str_context.getText().toString();
            if (PureString.length() == 0) {
                Toast.makeText(PostTweetActivity.this,"You Cannot Post a Empty Post",Toast.LENGTH_SHORT).show();
                return;
            }
            Parser parser = new Parser(PureString);
            String tags = parser.extractTag();
            String content = parser.extractContent();
            DB.getUser(Email, TempUser -> {
                int UID = TempUser.getUid();
                Post post = new Post(UID,PostedTime,content,tags,place);
                DB.AddPost(post);
                TempUser.AddPost(post);
                DB.UpdateUsersData(TempUser);
                DB.UpdateTreeNode(TempUser);
            });

            Toast.makeText(PostTweetActivity.this,"Successfully posted",Toast.LENGTH_SHORT).show();
            finish();

            //call the parser here to get tag/tags
            //new a post and add this to user' attributes;
        }
    };

    private View.OnClickListener LocationChangeListener = v -> {
        place = FindLocation();
        if(place!=null) {
            textViewLocation.setText(place);
        }
    };
}