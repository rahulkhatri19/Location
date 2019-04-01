package in.khatri.rahul.locationapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.khatri.rahul.locationapp.R;
import in.khatri.rahul.locationapp.model.User;
import in.khatri.rahul.locationapp.utils.SharedPreferenceUtils;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText etUserNo;
    DatabaseReference databaseReference;
    GoogleMap mMap;
    LatLng latLng;
    MarkerOptions markerOptions;
    double latitude, longitude;
    String name="";
    boolean doubleBackToExitPressedOnce= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        etUserNo = findViewById(R.id.et_user_no);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        markerOptions = new MarkerOptions();

        mapFragment.getMapAsync(this);

        if (!isOnline()){
            alertDialog();
        }
    }

    public void userMethod(View view) {
        if (etUserNo.getText().toString().trim().length() != 10) {
            etUserNo.requestFocus();
            etUserNo.setError("Please Enter valid number");
        } else {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(etUserNo.getText().toString().trim()).exists()) {
                        User user = dataSnapshot.child(etUserNo.getText().toString().trim()).getValue(User.class);
                        String location = "Lat: " + String.valueOf(user.getLatitude()) + " Long: " + String.valueOf(user.getLongitude());
                        Log.e("location:", location);
                        name= user.getName();
                        latitude= user.getLatitude();
                        longitude= user.getLongitude();
                        onMapReady(mMap);
                    } else {
                        Toast.makeText(HomeActivity.this, "Invalid User Number", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Error", String.valueOf(databaseError));
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (latitude > 0 && longitude > 0) {

            latLng = new LatLng(latitude, longitude);
            markerOptions.position(latLng);
            markerOptions.title(name);
            markerOptions.snippet(etUserNo.getText().toString().trim());
            mMap = googleMap;
            mMap.clear();
            // latLng = new LatLng(12.9434563, 77.6204741);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        } else {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
         // New Delhi Location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(28.644800, 77.216721)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(4));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout){
            new SharedPreferenceUtils(HomeActivity.this).setLogin("null");
            new SharedPreferenceUtils(HomeActivity.this).setLoginFlag(false);
            new SharedPreferenceUtils(HomeActivity.this).setPhone("null");
            startActivity(new Intent(HomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    private void alertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
        builder1.setMessage("Please Connect to Internet");
        builder1.setCancelable(false).setIcon(R.drawable.ic_internet).setTitle("No Internet Connection");
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                }).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isOnline()) {
                    alertDialog();
                } else {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alert = builder1.create();
        alert.show();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
