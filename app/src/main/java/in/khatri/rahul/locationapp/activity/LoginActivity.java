package in.khatri.rahul.locationapp.activity;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.khatri.rahul.locationapp.R;
import in.khatri.rahul.locationapp.model.Phone;
import in.khatri.rahul.locationapp.utils.SharedPreferenceUtils;

public class LoginActivity extends AppCompatActivity {
    EditText etPhone;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    boolean doubleBackToExitPressedOnce= false;
    String name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPhone = findViewById(R.id.et_user_no);
        if (!isOnline()){
            alertDialog();
        }
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Validating User ...");
        // FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("phone");
    }

    public void loginMethod(View view) {
        if (etPhone.getText().toString().trim().length() != 10) {
            etPhone.setError("Please Enter valid Number");
            etPhone.requestFocus();
        } else {
            progressDialog.show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(etPhone.getText().toString().trim()).exists()) {
                        progressDialog.dismiss();
                        final Phone phone = dataSnapshot.child(etPhone.getText().toString().trim()).getValue(Phone.class);
                        new SharedPreferenceUtils(LoginActivity.this).setName(phone.getName());
                        new SharedPreferenceUtils(LoginActivity.this).setPhone(etPhone.getText().toString().trim());
                        new SharedPreferenceUtils(LoginActivity.this).setLoginFlag(true);
                       // Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                        String pos = String.valueOf(phone.getPosition());

                        if (pos.equals("admin")) {
                            new SharedPreferenceUtils(LoginActivity.this).setLogin("admin");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            View alterLayout = getLayoutInflater().inflate(R.layout.alert_box, null);
                            builder.setView(alterLayout);

                            final EditText etPassword = alterLayout.findViewById(R.id.et_user_no);
                            Button btnSubmit = alterLayout.findViewById(R.id.btn_submit);
                            Button btnCancel = alterLayout.findViewById(R.id.btn_cancel);

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (etPassword.getText().toString().equals("")){
                                        etPassword.setError("Password cant be blank");
                                        etPassword.requestFocus();
                                    } else if(etPassword.getText().toString().trim().equals(String.valueOf(phone.getPassword()))) {
                                       // p.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid User id or Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setCancelable(false);
                            final AlertDialog alert = builder.create();
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                }
                            });
                            alert.show();
                        } else if (pos.equals("user")) {
                            new SharedPreferenceUtils(LoginActivity.this).setLogin("user");
                            startActivity(new Intent(LoginActivity.this, UserActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("posi", pos);
                        Log.e("pass", String.valueOf(phone.getPassword()));
                        //   User user=dataSnapshot.child(editText.getText().toString()).getValue(User.class);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Error", String.valueOf(databaseError));
                }
            });
        }
    }
    private void alertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
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
}
