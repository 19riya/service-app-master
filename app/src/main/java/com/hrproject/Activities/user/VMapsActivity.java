package com.hrproject.Activities.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
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

public class VMapsActivity extends AppCompatActivity implements OnMapReadyCallback , LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    String area_name,category_id,sub_category_id,sub_category_name,category_name,address,user_id;
    TextView vendor_address,change_address;
    Button confirm_address;
    UserSessionManager session;
    double longitude,latitude;
    LinearLayout layout;
    AlertDialog p;
    String address1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmaps);

        session=new UserSessionManager(VMapsActivity.this);
        HashMap<String,String> data=session.getUserDetails();
        user_id=data.get(session.KEY_ID);

        vendor_address=findViewById(R.id.vendor_location);
        confirm_address=findViewById(R.id.confirm_address);
        change_address=findViewById(R.id.change_address);
        layout=findViewById(R.id.layout_map);

        sub_category_id=getIntent().getExtras().getString("Sub_category_id");
        category_id=getIntent().getExtras().getString("category_id");
        sub_category_name=getIntent().getExtras().getString("sub_category_name");
        category_name=getIntent().getExtras().getString("category_name");


        System.out.println("category_id"+category_id);
        System.out.println("sub_category_id"+sub_category_id);
        System.out.println("sub_category_name"+sub_category_name);
        System.out.println("category_name"+category_name);


        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                (VMapsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.loader, null);
        mBuilder.setView(mView);
        p = mBuilder.create();
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCancelable(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        p.setCanceledOnTouchOutside(false);
        p.show();

        change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VMapsActivity.this, ProductList_Description.class);
                intent.putExtra("category_id",category_id);
                intent.putExtra("sub_category_name",sub_category_name);
                intent.putExtra("category_name",category_name);
                intent.putExtra("sub_category_id",sub_category_id);
                intent.putExtra("address",address);
                startActivity(intent);
                finish();
            }
        });

        confirm_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VMapsActivity.this, ProductList_Description.class);
                intent.putExtra("address",address);
                intent.putExtra("category_id",category_id);
                intent.putExtra("sub_category_name",sub_category_name);
                intent.putExtra("category_name",category_name);
                intent.putExtra("Sub_category_id",sub_category_id);
                startActivity(intent);
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void UPDATE_LOCATION_JSON() {
        Log.d("latitude",String.valueOf(latitude));
        Log.d("longitude",String.valueOf(longitude));
        Log.d("adress",address);


        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=apiInterface.update_user_location(user_id,String.valueOf(longitude),String.valueOf(latitude),address);


        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {
                Log.d("URL::",response.toString());
                Log.d("new URL::",response.body().toString());
                p.dismiss();

                try {
                    if (response.body().get(0).getError().equalsIgnoreCase("0")) {
                        Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    else {
                        Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();        }
                }
                catch (Exception ex){
                    Log.e("ERROR......",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.i("FAILURE ERROR:::",t.getMessage());
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
            ConnectionDetector cd = new ConnectionDetector(VMapsActivity.this);
            Boolean isInternetPresent = cd.isConnectingToInternet();
            // check for Internet status
            if (isInternetPresent) {
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker


                // Check if GPS enabled
                GPSTracker gps = new GPSTracker(VMapsActivity.this);

                if (gps.canGetLocation()) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
                else {
                    gps.showSettingsAlert();
                }
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));


                Geocoder geocoder;
                List<Address> addresses=null;
                geocoder=new Geocoder(this,Locale.getDefault());

                try{
                    addresses=geocoder.getFromLocation(latitude,longitude,1);

                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                if (addresses.size()>0){

                    address=addresses.get(0).getAddressLine(0);
                    area_name=addresses.get(0).getFeatureName();


                    System.out.println("arear_name"+area_name);
                }


                System.out.print("vendor current address......"+address);
                vendor_address.setText(address);

                UPDATE_LOCATION_JSON();


                //stop location updates
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }

            }
            else{
                SweetAlertDialog ff=new SweetAlertDialog(VMapsActivity.this,SweetAlertDialog.WARNING_TYPE);
                ff.setTitleText(getResources().getString(R.string.failed));
                ff.setContentText(getResources().getString(R.string.internet_connection));
                ff.setCanceledOnTouchOutside(false);
                ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        p.dismiss();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                ff.show();
            }



    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


}
