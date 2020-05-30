package com.hrproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;
import com.hrproject.Activities.Vendor.Edit_Vendor_Profile;
import com.hrproject.Activities.Vendor.VendorChatHistory;
import com.hrproject.Activities.user.UserChatHistory;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Booking_Vendor extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Address_get_set> datalist=new ArrayList<>();
    UserSessionManager sessionManager;
    AlertDialog p;
    LinearLayout layout,no_booking;
    GlobalVariables globalVariables;
  //  Button book_booking;
    String vendor_id,rating,unique_id,status,review;
    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_booking__vendor, container, false);

        sessionManager=new UserSessionManager(getActivity());
        HashMap<String,String> data=sessionManager.getUserDetails();
        vendor_id=data.get(sessionManager.KEY_ID);

        globalVariables=GlobalVariables.getInstance();

        layout=v.findViewById(R.id.layout_booking);
    //    book_booking=v.findViewById(R.id.book_booking);
        no_booking=v.findViewById(R.id.ven_no_booking);
        recyclerView=v.findViewById(R.id.vendor_order_detail);


      //  RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        ///mLayoutManager

        layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        checknet();

        return  v;
    }
    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
           /* androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (getActivity());
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();*/


            VENDOR_HISTORY();
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
                    p.dismiss();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }


    }

    private void VENDOR_HISTORY() {
        System.out.println("vendor_id:"+vendor_id);
        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.vendor_service_history(vendor_id);

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL::",response.toString());
             //   p.dismiss();
                try{
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                        datalist=new ArrayList<>(response.body());
                        Vendor_order_adapter adapter=new Vendor_order_adapter(getActivity(),datalist);
                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        no_booking.setVisibility(View.VISIBLE);

                    }
                }
                catch(Exception ex){
                    Log.d("EXCEPTION::",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.i("FAILURE ERROR:",t.getMessage());
         //       p.dismiss();
            }
        });
    }

    public class Vendor_order_adapter extends RecyclerView.Adapter<Vendor_order_adapter.MyViewHolder> {
        Context c;
        ArrayList<Address_get_set> data;


        public Vendor_order_adapter(FragmentActivity item_accept_reject_list, ArrayList<Address_get_set> moviesList) {
            this.data = moviesList;
            this.c = item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public Vendor_order_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_vendor_history, viewGroup, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final Vendor_order_adapter.MyViewHolder myViewHolder, final int i) {
            status=data.get(i).getStatus();

            if (status.equalsIgnoreCase("completed")){

                myViewHolder.show_vendor_rating.setVisibility(View.VISIBLE);
                myViewHolder.write_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myViewHolder.write_review.setVisibility(View.GONE);
                        myViewHolder.enter_review.setVisibility(View.VISIBLE);

                        myViewHolder.enter_review.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                data.get(i).setVendor_review(""+myViewHolder.enter_review.getText().toString());
                                review=""+data.get(i).getVendor_review();

                                VENDOR_REVIEW();
                            }
                        });
                    }
                });

                myViewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        data.get(i).setUser_rating(""+myViewHolder.ratingBar.getRating());
                        rating=""+data.get(i).getUser_rating();

                        VENDOR_RATING();
                        //      System.out.println("rating::"+rating);
                    }
                });
            }

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data.get(i).getStatus().equalsIgnoreCase("Auto Cancelled")){

                    }
                    else {
                        globalVariables.setLatitude(data.get(i).getLatitude());
                        globalVariables.setLongitude(data.get(i).getLongitude());
                        globalVariables.setMobile(data.get(i).getUser_mobile());
                        globalVariables.setUser_id(data.get(i).getUser_id());
                        globalVariables.setUnique_id(data.get(i).getUnique_id());
                        globalVariables.setUser_name(data.get(i).getUser_name());
                        startActivity(new Intent(getActivity(), VendorChatHistory.class));
                    }
                }
            });



            if (data.get(i).getStatus().equalsIgnoreCase("completed")){
                myViewHolder.history_amount.setVisibility(View.VISIBLE);
                myViewHolder.history_otp.setVisibility(View.VISIBLE);
            }
            else {
                if(data.get(i).getStatus().equalsIgnoreCase("Amount decided")){
                    myViewHolder.history_amount.setVisibility(View.VISIBLE);

                }
                else{
                    myViewHolder.history_amount.setVisibility(View.GONE);
                }
                myViewHolder.history_otp.setVisibility(View.GONE);
            }

            System.out.println("rating::"+rating);
            String statusString="";
            if (data.get(i).getStatus().equalsIgnoreCase("accept")){
                 statusString = "<b>" + getResources().getString(R.string.status) + "</b> " + ": "+
                        "<font color='#09bd83' >"+data.get(i).getStatus()+"</font>";
               // myViewHolder.status.setTextColor(Color.parseColor("#09bd83"));
           }
            else if (data.get(i).getStatus().equalsIgnoreCase("reject")){
                statusString = "<b>" + getResources().getString(R.string.status) + "</b> " + ": "+
                        "<font color='#D5436A' >"+data.get(i).getStatus()+"</font>";
            }
            else {
                statusString = "<b>" + getResources().getString(R.string.status) + "</b> " + ": "+
                    "<font color='#000000' >"+data.get(i).getStatus()+"</font>";
            }

            unique_id=data.get(i).getUnique_id();

            String orderString = "<b>" + getResources().getString(R.string.order_no) + "</b> " +" #000"+data.get(i).getUnique_id();
            myViewHolder.order_no.setText(Html.fromHtml(orderString));
            myViewHolder.status.setText(Html.fromHtml(statusString));

            myViewHolder.history_amount.setText(Html.fromHtml("<b>" +getResources().getString(R.string.service_amnt)
                    +": "+ "</b>" + "Rs."+data.get(i).getAmount()));

            myViewHolder.vendor_name.setText(data.get(i).getUser_name());
            myViewHolder.vendor_mobile.setText("+91-"+data.get(i).getUser_mobile());
            myViewHolder.service.setText(data.get(i).getCategory_name() + "|" + data.get(i).getSubcategory());
            myViewHolder.history_date.setText(data.get(i).getDate_time());


            String sourceString = "<b>" + getResources().getString(R.string.service_otp) + "</b> " + " "+data.get(i).getOtp();
            myViewHolder.history_otp.setText(Html.fromHtml(sourceString));

            Picasso.with(c).load(data.get(i).getUser_image()).placeholder(R.drawable.man).into(myViewHolder.user_image);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView order_no, status, vendor_name, vendor_mobile,service,write_review
                    ,history_otp,history_date,history_amount;
            public EditText enter_review;
            public ImageView user_image;
            public RatingBar ratingBar;
            LinearLayout show_vendor_rating;

            public MyViewHolder(View view) {
                super(view);
                history_amount=view.findViewById(R.id.history_amount);
                write_review=view.findViewById(R.id.ven_write_review);
                enter_review=view.findViewById(R.id.ven_enter_review);
                ratingBar=view.findViewById(R.id.vendor_rating);
                order_no = view.findViewById(R.id.ven_history_id);
                status = view.findViewById(R.id.ven_history_status);
                show_vendor_rating=view.findViewById(R.id.show_vendor_rating);
                vendor_name = view.findViewById(R.id.ven_history_name);
                vendor_mobile = view.findViewById(R.id.ven_history_mobile);
                user_image = view.findViewById(R.id.ven_history_image);
                service = view.findViewById(R.id.ven_history_service);
                history_otp=view.findViewById(R.id.history_otp);
                history_date=view.findViewById(R.id.service_date);
            }
        }
    }

    private void VENDOR_REVIEW() {
        System.out.println("review:"+review);
        System.out.println("unique_id:"+unique_id);

        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.update_vendor_review(unique_id,review);

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL:",response.toString());
            //    p.dismiss();

                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    Snackbar snackbar = Snackbar.make(layout, "" + response.body().get(0).getMsg(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else {
                    Snackbar snackbar = Snackbar.make(layout, "" + response.body().get(0).getMsg(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.i("FAILURE ERROR:",t.getMessage());
              //  p.dismiss();
            }

        });
    }

    private void VENDOR_RATING() {

        System.out.println("rating:::"+rating);
        System.out.println("unique_id:"+unique_id);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.update_vendor_rating(unique_id,rating);


        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL::",response.toString());
  //              p.dismiss();
                try{
                    if (response.body().get(0).getError().equalsIgnoreCase("0")) {
                        Snackbar snackbar = Snackbar.make(layout, "" + response.body().get(0).getMsg(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    else
                        {
                            Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                            snackbar.show();
                    }
                }

                catch(Exception ex){
                    Log.d("EXCEPTION::",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.i("FAILURE ERROR:",t.getMessage());
    //            p.dismiss();
            }
        });
    }
}
