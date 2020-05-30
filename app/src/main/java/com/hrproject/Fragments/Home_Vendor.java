package com.hrproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.material.snackbar.Snackbar;
import com.hrproject.Activities.Vendor.VendorRating_Review;
import com.hrproject.Activities.Vendor.Vendor_Show_Profile;
import com.hrproject.Activities.user.Rating_Review;
import com.hrproject.Activities.user.VMapsActivity;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.Slider_Get_Set;
import com.hrproject.GetterSetter.profile_get_set;
import com.hrproject.HelperClasses.CardAdapter;
import com.hrproject.HelperClasses.CardItem;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.ShadowTransformer;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hrproject.HelperClasses.CardAdapter.MAX_ELEVATION_FACTOR;


public class Home_Vendor extends Fragment {
    Double latitude,longitude;
    String address,vendor_id;
    UserSessionManager session;
    ImageView category_image,home_exp_image;
    LinearLayout layout;
    CircularImageView vendor_image;
    TextView vendor_name,category,sub_category,experiance_time;
    String ven_subCate,unique_id="",status="";
    AlertDialog p;
    RatingBar ratings;
    ViewPager home_viewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home__vendor, container, false);

        session=new UserSessionManager(getActivity());
        HashMap<String,String> data=session.getUserDetails();
        vendor_id=data.get(session.KEY_ID);
        home_viewPager=view.findViewById(R.id.viewPager);

        HashMap<String,String> service_status=session.getChatId();
        unique_id=service_status.get(session.KEY_CHATID);
        status=service_status.get(session.KEY_Status);

        System.out.println("unique_id:"+unique_id);
        System.out.println("unique_id:"+status);


        mCardAdapter = new CardPagerAdapter();

        if(unique_id.equalsIgnoreCase("")){

        }
        else {
            if (status.equalsIgnoreCase("4"))
                startActivity(new Intent(getActivity(), VendorRating_Review.class));
            else {

            }
        }

        System.out.println("user_id:::"+vendor_id);

        ratings=view.findViewById(R.id.ratings);
        experiance_time=view.findViewById(R.id.experiance_time);
        home_exp_image=view.findViewById(R.id.home_exp_image);
        vendor_name= view.findViewById(R.id.ven_name);
        category=view.findViewById(R.id.ven_category);
        sub_category=view.findViewById(R.id.ven_sub_category);
        category_image=view.findViewById(R.id.ven_category_img);
        vendor_image=view.findViewById(R.id.vendor_img);


        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            GPSTracker gps = new GPSTracker(getActivity());

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();


                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                    if (addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);

                        //     Log.d("check_cityName",city);
                        Log.d("check_address", address);

                    }

                    // \n is for new line
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }


            MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
            myAsyncTasks.execute();

            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (getActivity());
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();

            VENDOR_DETAILS();
            SLIDER();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
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

        return view;
    }



    public class MyAsyncTasks extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            System.out.println("vendor_id:"+vendor_id);
            System.out.println("longitude:"+longitude);
            System.out.println("latitude:"+latitude);
            System.out.println("address:"+address);
            ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Get_Set>> call=apiInterface.update_vendor_location(vendor_id,String.valueOf(longitude),String.valueOf(latitude),address);

            call.enqueue(new Callback<ArrayList<Get_Set>>() {
                @Override
                public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {

                    Log.d("URL::",response.toString());

                    Log.d("new URL::",response.body().toString());

                    try {
                        if (response.body().get(0).getError().equalsIgnoreCase("0")) {
                            Log.i("MESAAGE:",response.body().get(0).getMsg());
                        }
                        else {
                            Log.i("MESSAGE:",response.body().get(0).getMsg());
                        }
                    }
                    catch (Exception ex){
                        Log.e("ERROR......",ex.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                    Log.i("FAILURE ERROR:::",t.getMessage());
                }
            });


            return address;
        }
    }

    private void VENDOR_DETAILS() {
        System.out.println("vendor_id:"+vendor_id);
        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=apiInterface.vendor_basic_details(vendor_id);

        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {
                Log.d("URL::",response.toString());
                p.dismiss();
                try{


                    home_exp_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                            View mView = getLayoutInflater().inflate(R.layout.pager_main, null);

                            ImageView proceed = (ImageView) mView.findViewById(R.id.zoom_certificate);

                            Picasso.with(getActivity()).load(response.body().get(0).getExperiance_image())
                            .placeholder(R.drawable.man).into(proceed);

                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                        }
                    });


                    category_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                            View mView = getLayoutInflater().inflate(R.layout.pager_main, null);

                            ImageView proceed = (ImageView) mView.findViewById(R.id.zoom_certificate);

                            Picasso.with(getActivity()).load(response.body().get(0).getCategory_image())
                                    .placeholder(R.drawable.man).into(proceed);

                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                        }
                    });


                    experiance_time.setText(response.body().get(0).getExperiance());

                    ratings.setRating(Float.parseFloat(response.body().get(0).getRating()));

                    ven_subCate=response.body().get(0).getSub_categories();
                    System.out.println("ven_subCate:"+ven_subCate);

                    String repl =  ven_subCate.replaceAll(",","|");

                    System.out.println("repl:"+repl);
                    sub_category.setText(repl+"");

                    vendor_name.setText(response.body().get(0).getName());
                    category.setText(response.body().get(0).getCategory_name());

                    Picasso.with(getActivity()).load(response.body().get(0).getProfile_image())
                            .placeholder(R.drawable.cleaning_off).into(vendor_image);
                    Picasso.with(getActivity()).load(response.body().get(0).getCategory_image())
                            .placeholder(R.drawable.man).into(category_image);
                    Picasso.with(getActivity()).load(response.body().get(0).getExperiance_image())
                            .placeholder(R.drawable.man).into(home_exp_image);

                }
                catch (Exception ex){
                    Log.d("ERROR::",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                Log.i("FAILURE ERROR:",t.getMessage());
                p.dismiss();
            }
        });
    }


    private void SLIDER(){
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Slider_Get_Set>> call=apiInterface.view_slider("2");

        call.enqueue(new Callback<ArrayList<Slider_Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Slider_Get_Set>> call, Response<ArrayList<Slider_Get_Set>> response) {
                p.dismiss();

                try {
              //      if (response.body().get(0).getError().equalsIgnoreCase("0")){

                      /*  ArrayList<ProductGetSet> image_slider=new ArrayList<>();
                        image_slider=response.body().get(0).getResult();
*/
                        for (int i=0;i<response.body().size();i++){
                            mCardAdapter.addCardItem(new CardItem(response.body().get(i).getSlider_image()));
                        }


                        mCardShadowTransformer = new ShadowTransformer(home_viewPager, mCardAdapter);
                        mCardShadowTransformer.enableScaling(true);
                        home_viewPager.setAdapter(mCardAdapter);
                        home_viewPager.setOffscreenPageLimit(3);

                 //   }
                }
                catch (Exception ex){
                    Log.d("slider_error:",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Slider_Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.d("slider_failure:",t.getMessage());
            }
        });
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

        private List<CardView> mViews;
        private List<CardItem> mData;
        private float mBaseElevation;

        public CardPagerAdapter() {
            mData = new ArrayList<>();
            mViews = new ArrayList<>();
        }

        public void addCardItem(CardItem item) {
            mViews.add(null);
            mData.add(item);
        }

        public float getBaseElevation() {
            return mBaseElevation;
        }

        @Override
        public CardView getCardViewAt(int position) {
            return mViews.get(position);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.slider_pager, container, false);
            container.addView(view);
            bind(mData.get(position), view);
            CardView cardView = (CardView) view.findViewById(R.id.slider_card);

            if (mBaseElevation == 0) {
                mBaseElevation = cardView.getCardElevation();
            }

            cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
            mViews.set(position, cardView);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mViews.set(position, null);
        }

        private void bind(CardItem item, View view) {

            ImageView imageView = view.findViewById(R.id.slider_img);

            Picasso.with(getActivity())
                    .load(item.getImage())
                    .into(imageView);
            //  imageView.setImageResource(item.getImage());
        }

    }

}
