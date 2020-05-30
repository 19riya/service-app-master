package com.hrproject.Activities.user;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hrproject.Fragments.Booking_User;
import com.hrproject.Fragments.Home_User;
import com.hrproject.Fragments.User_Inbox;
import com.hrproject.Fragments.User_Profile;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Welcome extends AppCompatActivity {

    BottomNavigationView navigation;
    String updated_address,user_id;
    UserSessionManager session;
    CoordinatorLayout layout;
    private int LOCATION_CODE=99;
    Double latitude,longitude;
    boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__welcome);

        navigation = findViewById(R.id.navigation);

        layout=findViewById(R.id.frag_layout);
        session = new UserSessionManager(User_Welcome.this);
        HashMap<String, String> data = session.getUserDetails();
        user_id = data.get(session.KEY_ID);

        Home_User fragment = new Home_User();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_frame_container, fragment);
        ft.commit();

        ConnectionDetector cd = new ConnectionDetector(User_Welcome.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {

            navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    int id = menuItem.getItemId();

                    if (id == R.id.home) {
                        loadHFragment();
                        return true;
                    } else if (id == R.id.my_bookings) {
                        loadBFragment();
                        return true;
                    }
                    else if(id == R.id.my_notification){
                        loadIFragment();
                        return true;
                    }
                    else if (id == R.id.profile) {
                        loadPFragment();
                        return true;
                    }
                    return false;
                }
            });

            if (permissionAlreadyGranted()) {
                System.out.println("welcome, permission granted!!");
                getLocation();

                return;
            }

            requestPermission();

        }
        else {
            SweetAlertDialog ff = new SweetAlertDialog(User_Welcome.this, SweetAlertDialog.WARNING_TYPE);
            ff.setTitleText(getResources().getString(R.string.failed));
            ff.setContentText(getResources().getString(R.string.internet_connection));
            ff.setCanceledOnTouchOutside(false);
            ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {

                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }
    }

    private void getLocation() {
        GPSTracker gps = new GPSTracker(User_Welcome.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            System.out.println("latitude:"+latitude);
            System.out.println("longitude:"+longitude);

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(User_Welcome.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses.size() > 0) {
                    updated_address = addresses.get(0).getAddressLine(0);


                    Log.d("check_address", updated_address);
                    //   location_text.setText(""+address);
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            AsyncTasker asynctask = new AsyncTasker();
            asynctask.execute();

            // \n is for new line
        }
        else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }

    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(User_Welcome.this, Manifest.permission.ACCESS_FINE_LOCATION );

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(User_Welcome.this, Manifest.permission.ACCESS_FINE_LOCATION )) {

        }
        ActivityCompat.requestPermissions(User_Welcome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_CODE) {


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //    Toast.makeText(getActivity(), "Permission granted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(getActivity(), "Permission is denied!", Toast.LENGTH_SHORT).show();
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION );
                if (! showRationale) {
                    openSettingsDialog();
                }
            }
        }
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(User_Welcome.this);
        builder.setTitle(getResources().getString(R.string.required_permission));
        builder.setMessage(getResources().getString(R.string.permission_explain));
        builder.setPositiveButton(getResources().getString(R.string.to_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel_changes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void loadIFragment() {
        User_Inbox fragment = new User_Inbox();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_frame_container, fragment).addToBackStack("Home_User");
        ft.commit();
    }

    private void loadHFragment() {
        Home_User fragment = new Home_User();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_frame_container, fragment);
        ft.commit();
    }

    private void loadBFragment() {
        Booking_User fragment = new Booking_User();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_frame_container, fragment).addToBackStack("Home_User");
        ft.commit();
    }

    private void loadPFragment() {
        User_Profile fragment = new User_Profile();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_frame_container, fragment).addToBackStack("Home_User");
        ft.commit();
    }

    private class AsyncTasker extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Get_Set>> call=a.update_user_location(user_id,String.valueOf(latitude),String.valueOf(longitude),updated_address);

            System.out.println("latitutde"+String.valueOf(latitude));
            System.out.println("Longitude"+String.valueOf(longitude));
            System.out.println("Address"+updated_address);

            call.enqueue(new Callback<ArrayList<Get_Set>>() {
                @Override
                public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
                {
                    Log.d("URL:",response.toString());
                    if(response.body().get(0).getError().equalsIgnoreCase("0"))
                    {
                        Snackbar snackbar=Snackbar.make(layout,response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    else
                    {
                        Snackbar snackbar=Snackbar.make(layout,getResources().getString(R.string.loc_nfound),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {/*
                    AsyncTasker tasker=new AsyncTasker();
                    tasker.execute();*/
                }
            });

            return updated_address;
        }
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigation .getMenu().getItem(0).setChecked(true);
            System.out.println("selected_id:"+navigation.getSelectedItemId());
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,""+getResources().getString(R.string.back_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        } else {
            super.onBackPressed();
            return;
        }
    }

/*
    @Override
    public void onBackPressed() {
        navigation= findViewById(R.id.navigation);
        if (navigation.getSelectedItemId() == R.id.home)
        {
            super.onBackPressed();
            finish();
        }
        else
        {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }
*/
}
