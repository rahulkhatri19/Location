package in.khatri.rahul.locationapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.khatri.rahul.locationapp.R;
import in.khatri.rahul.locationapp.utils.SharedPreferenceUtils;

public class UserActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    private LocationManager locationManager;
    DatabaseReference databaseReference;
    GoogleMap mMap;
    LatLng latLng;
    MarkerOptions markerOptions;
    double latitude, longitude;
    boolean doubleBackToExitPressedOnce= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        markerOptions = new MarkerOptions();
        mapFragment.getMapAsync(this);
        if (!isOnline()){
            alertDialog();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
      //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this); // min time in ms and distance meter.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String stLocation = "Lat: " + location.getLatitude() + " Long: " + location.getLongitude();
        Log.e("location:", stLocation);
        if (location.getLatitude() > 0 && location.getLongitude() > 0) {
            saveLatLong(location.getLatitude(), location.getLongitude());
            latitude= location.getLatitude();
            longitude= location.getLongitude();
            onMapReady(mMap);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("Status:", "Changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("Provider:", "Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("Provider:", "Disabled");
        Toast.makeText(this, "Please Enable GPS", Toast.LENGTH_LONG).show();
    }

    private void saveLatLong(double latitude, double longitude) {
        Log.e("Phone: ", new SharedPreferenceUtils(UserActivity.this).getPhone());
        databaseReference.child(new SharedPreferenceUtils(UserActivity.this).getPhone()).child("name").setValue(new SharedPreferenceUtils(UserActivity.this).getName());
        databaseReference.child(new SharedPreferenceUtils(UserActivity.this).getPhone()).child("latitude").setValue(latitude);
        databaseReference.child(new SharedPreferenceUtils(UserActivity.this).getPhone()).child("longitude").setValue(longitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (latitude > 0 && longitude > 0) {

            latLng = new LatLng(latitude, longitude);
            markerOptions.position(latLng);
            markerOptions.title(new SharedPreferenceUtils(UserActivity.this).getPhone());
            markerOptions.snippet("Your Current Location");
            mMap = googleMap;
            mMap.clear();
            // latLng = new LatLng(12.9434563, 77.6204741);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
        else {
            Toast.makeText(this, "Not able to Fetch Current Location", Toast.LENGTH_SHORT).show();
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
            new SharedPreferenceUtils(UserActivity.this).setLogin("null");
            new SharedPreferenceUtils(UserActivity.this).setLoginFlag(false);
            new SharedPreferenceUtils(UserActivity.this).setPhone("null");
            startActivity(new Intent(UserActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UserActivity.this);
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
