package com.hrproject.Activities.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.ExpandableHeightGridView;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sub_Product_List extends AppCompatActivity {

    String address1;
    ExpandableHeightGridView SubList;
    String category_id="",category_name="";
    Double latitude,longitude;
    UserSessionManager sessionManager;
    AlertDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub__product__list);
        category_name = getIntent().getExtras().getString("category_name");
        category_id   = getIntent().getExtras().getString("category_id");

        SubList=findViewById(R.id.SubList);
        SubList.setExpanded(true);

        sessionManager=new UserSessionManager(Sub_Product_List.this);

        Toolbar toolbar=findViewById(R.id.title_subProduct);
        toolbar.setTitle(category_name);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            { finish(); }

        });

        ConnectionDetector cd = new ConnectionDetector(Sub_Product_List.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (Sub_Product_List.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();

            subProductShow();

            GPSTracker gps = new GPSTracker(Sub_Product_List.this);

            // Check if GPS enabled
            if(gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();


                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(Sub_Product_List.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if(addresses.size()>0)
                    {
                        address1 = addresses.get(0).getAddressLine(0);

                        Log.d("check_address",address1);
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
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Sub_Product_List.this,SweetAlertDialog.WARNING_TYPE);
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

        checknet();

    } // end oncreate method

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Sub_Product_List.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            subProductShow();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Sub_Product_List.this,SweetAlertDialog.WARNING_TYPE);
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
    }


    private void subProductShow()
    {
        System.out.println("language_type:"+sessionManager.getlanguage());
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.sub_category_list(category_id,sessionManager.getlanguage());
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {


                Log.d("response_Sie",response.toString());
                Log.d("response_Siezz", String.valueOf(response.body().size()));

                p.dismiss();
                SubList.setAdapter(new SubListAdapter(Sub_Product_List.this,response.body()));

            }// end on craete emthod

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
                p.dismiss();
                Log.d("failueMessage",t.getMessage());
            }
        });
    }

    public class SubListAdapter extends BaseAdapter
    {
        ArrayList<Get_Set> body;
        AppCompatActivity activity;
        public SubListAdapter(Sub_Product_List list, ArrayList<Get_Set> body) {

            this.activity=list;
            this.body=body;
        }

        @Override
        public int getCount() {
            return body.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sub_cat_list, null, true);

            ImageView subCatImage=view.findViewById(R.id.subCatImage);
            TextView subCatName=view.findViewById(R.id.subCateName);

            subCatName.setText(""+body.get(i).getSub_category());
            Picasso.with(activity).load(body.get(i).getSub_category_image()).into(subCatImage);

            System.out.println("sub_category_id"+body.get(i).getSub_category_id());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* System.out.println("sub_category_id......"+body.get(i).getSub_category_id());
                    System.out.println("sub_category_id......"+body.get(i).getSub_category());
                    System.out.println("sub_category_id......"+body.get(i).getCategory_name());*/

                    Intent intent=new Intent(Sub_Product_List.this,ProductList_Description.class);
                    intent.putExtra("Sub_category_id",body.get(i).getSub_category_id());
                    intent.putExtra("category_id",category_id);
                    intent.putExtra("address",address1);
                    intent.putExtra("sub_category_name",body.get(i).getSub_category());
                    intent.putExtra("category_name",body.get(i).getCategory_name());
                    startActivity(intent);
                }
            });

            return  view;
        }
    }

}
