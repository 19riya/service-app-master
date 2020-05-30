package com.hrproject.Activities.Vendor;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.hrproject.Activities.user.ProductList_Description;
import com.hrproject.Fragments.Booking_Vendor;
import com.hrproject.Fragments.Home_Vendor;
import com.hrproject.Fragments.Vendor_Notification;
import com.hrproject.Fragments.Vendor_Profile;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vendor_Welcome extends AppCompatActivity {
    BottomNavigationView navView;
    String updated_address,vendor_id;
    UserSessionManager session;
    CoordinatorLayout layout;
    Double latitude,longitude;
    Menu bottomNavigationMenu;
    boolean doubleBackToExitPressedOnce=false;
    String notifyStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__welcome);

        session=new UserSessionManager(Vendor_Welcome.this);
        HashMap<String,String> data=session.getUserDetails();
        vendor_id=data.get(session.KEY_ID);
        System.out.println("vendor_id:::"+vendor_id);

        notifyStatus=getIntent().getExtras().getString("notifyStatus");
        System.out.println("notify_status:"+notifyStatus);

        navView = findViewById(R.id.nav_view);
        layout=findViewById(R.id.container);
        bottomNavigationMenu = navView.getMenu();

        loadHFragment();

        ConnectionDetector cd = new ConnectionDetector(Vendor_Welcome.this);
            Boolean isInternetPresent = cd.isConnectingToInternet();
            // check for Internet status
            if (isInternetPresent) {
                GPSTracker gps = new GPSTracker(Vendor_Welcome.this);

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    System.out.println("latitude:"+latitude);
                    System.out.println("longitude:"+longitude);

                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(Vendor_Welcome.this, Locale.getDefault());


                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        if(addresses.size()>0)
                        {
                            updated_address = addresses.get(0).getAddressLine(0);
                            Log.d("check_address",updated_address);
                            //   location_text.setText(""+address);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    // \n is for new line
                }
                else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.

                    gps.showSettingsAlert();
                }

                AsyncTasks asynctask=new AsyncTasks();
                asynctask.execute();

                navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if (id==R.id.navigation_home){
                            loadHFragment();
                            return true;
                        }
                        if (id==R.id.navigation_dashboard){
                            loadBFragment();
                            return true;
                        }
                        if (id==R.id.navigation_notifications){
                            loadNFragment();
                            return true;
                        }
                        if (id==R.id.vendor_profile){
                            loadPFragment();
                            return true;
                        }
                        return false;
                    }
                });
            }

            else{
                SweetAlertDialog ff=new SweetAlertDialog(Vendor_Welcome.this,SweetAlertDialog.WARNING_TYPE);
                ff.setTitleText(getResources().getString(R.string.failed));
                ff.setContentText(getResources().getString(R.string.internet_connection));
                ff.setCanceledOnTouchOutside(false);
                ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                ff.show();
            }


 /*       if (getIntent().hasExtra("notify")){
            Log.i("Helllooooo","---------------");
            navView.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
            bottomNavigationMenu.performIdentifierAction(R.id.navigation_notifications, 0);
        }

        if (savedInstanceState == null) {

            loadHFragment();
        }*/


     if(notifyStatus.equalsIgnoreCase("1"))
    {


        Bundle bundle = new Bundle();
        bundle.putString("notifyStatus", "notificationcome");
        Vendor_Notification fragment1 = new Vendor_Notification();
        fragment1.setArguments(bundle);
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.frame_container, fragment1);
        ft1.commit();
    }
    }



   /* @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String msg=intent.getStringExtra("notify");
        System.out.println("msg:::"+msg);

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        if (msg!=null){
            if (msg.equals("notify")){
                Vendor_Notification fragment1=new Vendor_Notification();
                fragmentTransaction.replace(R.id.frame_container, fragment1);
                fragmentTransaction.commit();
            }
        }
    }
*/
    private void loadNFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putString("notifyStatus", "nofound");
        Vendor_Notification fragment = new Vendor_Notification();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment).addToBackStack("Home_Vendor");
        ft.commit();
    }

    private void loadPFragment() {
        Vendor_Profile fragment = new Vendor_Profile();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment).addToBackStack("Home_Vendor");
        ft.commit();
    }

    private void loadBFragment() {
        Booking_Vendor fragment = new Booking_Vendor();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment).addToBackStack("Home_Vendor");
        ft.commit();
    }

    private void loadHFragment() {
        Home_Vendor fragment =new Home_Vendor();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.commit();
    }

    private class AsyncTasks extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {

            ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Get_Set>> call=a.update_vendor_location("9",String.valueOf(latitude),String.valueOf(longitude),updated_address);

            System.out.println("latitutde"+String.valueOf(latitude));
            System.out.println("Longitude"+String.valueOf(longitude));
            System.out.println("Address"+updated_address);

            call.enqueue(new Callback<ArrayList<Get_Set>>() {
                @Override
                public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
                {
                    if(response.body().get(0).getError().equalsIgnoreCase("0"))
                    {
                        Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                       }
                    else
                    {
                        Snackbar snackbar=Snackbar.make(layout,""+getResources().getString(R.string.loc_nfound),Snackbar.LENGTH_LONG);
                        snackbar.show();
                       }
                }

                @Override
                public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {

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
            navView.getMenu().getItem(0).setChecked(true);
            System.out.println("back_pressed:"+"popBackStack");
        }
       else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,""+getResources().getString(R.string.back_again), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        }
       else {
            System.out.println("back_pressed:"+"super");
            super.onBackPressed();
            return;
        }

        /*navView= findViewById(R.id.nav_view);
        if (navView.getSelectedItemId() == R.id.navigation_home)
        {
            super.onBackPressed();
            finish();
        }
        else
        {
            navView.setSelectedItemId(R.id.navigation_home);
        }*/
    }
}
