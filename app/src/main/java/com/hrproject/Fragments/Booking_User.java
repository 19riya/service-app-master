package com.hrproject.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;
import com.hrproject.Activities.Forgot_Password;
import com.hrproject.Activities.Otp_Verify;
import com.hrproject.Activities.user.UserChatHistory;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Booking_User extends Fragment {
    RecyclerView listView;
    String rating="",status;
    GlobalVariables globalVariables;
    UserSessionManager session;
    String user_id,unique_id,review;
    LinearLayout layout,no_booking;
    Button book_booking;
    Context context;
    androidx.appcompat.app.AlertDialog p;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_booking__user, container, false);

        session=new UserSessionManager(getActivity());
        HashMap<String,String> data=session.getUserDetails();
        user_id=data.get(session.KEY_ID);

        globalVariables=GlobalVariables.getInstance();
        context=getActivity();
        listView=v.findViewById(R.id.user_order_detail);
        layout=v.findViewById(R.id.user_layout_booking);
        no_booking=v.findViewById(R.id.no_booking);
        book_booking=v.findViewById(R.id.book_booking);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        listView.setLayoutManager(layoutManager);

        checknet();

        return v;
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        System.out.println("status:::"+isInternetPresent);
        // check for Internet status
        if (isInternetPresent) {

            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
            USER_HISTORY();
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
    }

    private void USER_HISTORY() {
        System.out.println("user_id:::"+user_id);
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.user_service_history(user_id);

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL::",response.toString());

                p.dismiss();
                try{
                    if(response.body().get(0).getError().equalsIgnoreCase("0")){
                        ArrayList<Address_get_set> datalist=new ArrayList<>(response.body());
                        User_order_adapter adapter=new User_order_adapter(getActivity(),datalist);
                        listView.setAdapter(adapter);
                    }
                    else{
                        listView.setVisibility(View.GONE);
                        no_booking.setVisibility(View.VISIBLE);

                        book_booking.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Home_User fragment = new Home_User();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.user_frame_container, fragment);
                                ft.commit();
                            }
                        });
                    }

                }
                catch(Exception ex){
                    Log.d("EXCEPTION::",ex.getMessage());
                    USER_HISTORY();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                p.dismiss();
                USER_HISTORY();
                Log.i("FAILURE ERROR:",t.getMessage());
            }
        });
    }

    public class User_order_adapter extends RecyclerView.Adapter<User_order_adapter.MyViewHolder>
    {
        Context c;
        ArrayList<Address_get_set> data;

        public User_order_adapter(FragmentActivity item_accept_reject_list, ArrayList<Address_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public User_order_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_user_history, viewGroup, false);

            return new User_order_adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final User_order_adapter.MyViewHolder myViewHolder, final int i) {

                System.out.println("dataimage:"+data.get(i).getVendor_image());

                Picasso.with(c).load(data.get(i).getVendor_image()).placeholder(R.drawable.man).into(myViewHolder.user_image);

                //   myViewHolder.user_image.setImageResource(data.get(i).getVendor_image());

                rating=""+myViewHolder.ratingBar.getRating();
                unique_id=data.get(i).getUnique_id();

            if(data.get(i).getStatus().equalsIgnoreCase("Amount decided") ||
                    data.get(i).getStatus().equalsIgnoreCase("completed")){
                myViewHolder.uhistory_amount.setVisibility(View.VISIBLE);

            }
            else{
                myViewHolder.uhistory_amount.setVisibility(View.GONE);
            }

            if(data.get(i).getStatus().equalsIgnoreCase("Auto cancelled") ||
                    data.get(i).getStatus().equalsIgnoreCase("cancelled")){
                myViewHolder.history_otp.setVisibility(View.GONE);

            }
            else{
                myViewHolder.history_otp.setVisibility(View.VISIBLE);
            }

                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(data.get(i).getStatus().equalsIgnoreCase("auto cancelled")){

                        }
                        else{
                            globalVariables.setVendor_id(data.get(i).getVendor_id());
                            globalVariables.setUnique_id(data.get(i).getUnique_id());
                            globalVariables.setVendor_name(data.get(i).getVendor_name());
                            globalVariables.setMobile(data.get(i).getVendor_mobile());
                            startActivity(new Intent(getActivity(), UserChatHistory.class));
                        }
                    }
                });

            if(data.get(i).getStatus().equalsIgnoreCase("auto cancelled")){

                myViewHolder.user_name.setVisibility(View.GONE);
                myViewHolder.user_mobile.setVisibility(View.GONE);
            }
            else{
                myViewHolder.user_name.setVisibility(View.VISIBLE);
                myViewHolder.user_mobile.setVisibility(View.VISIBLE);
            }


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

            String orderString = "<b>" + getResources().getString(R.string.order_no) + "</b> " + " #000"+data.get(i).getUnique_id();
            myViewHolder.order_no.setText(Html.fromHtml(orderString));

           /* String statusString = "<b>" + getResources().getString(R.string.status) + "</b> " + ": "+
                    "<font color='#F34F3D' >"+data.get(i).getStatus()+"</font>";*/
            myViewHolder.status.setText(Html.fromHtml(statusString));

                myViewHolder.user_name.setText(data.get(i).getVendor_name());
                myViewHolder.user_mobile.setText("+91-"+data.get(i).getVendor_mobile());
                myViewHolder.service.setText(data.get(i).getCategory_name()+"|"+data.get(i).getSubcategory());

                myViewHolder.history_date.setText(data.get(i).getDate_time());

                String sourceString = "<b>" + getResources().getString(R.string.service_otp) + "</b> " + " "+data.get(i).getOtp();
                myViewHolder.history_otp.setText(Html.fromHtml(sourceString));

            myViewHolder.uhistory_amount.setText(Html.fromHtml("<b>" +getResources().getString(R.string.service_amnt)
                    +": "+ "</b>" + "Rs."+data.get(i).getAmount()));


                /*myViewHolder.history_otp.setText(Html.fromHtml("<b>"+getResources().getString(R.string.service_otp)+"</b>")
                        +" "+data.get(i).getOtp());*/

                System.out.println("status:"+data.get(i).getStatus());
                if (data.get(i).getStatus().equalsIgnoreCase("completed")){
                    myViewHolder.show_user_rating.setVisibility(View.VISIBLE);

                    myViewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            data.get(i).setUser_rating(""+myViewHolder.ratingBar.getRating());
                            rating=""+data.get(i).getUser_rating();
                            //      System.out.println("rating::"+rating);


                            USER_RATING();
                        }
                    });

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
                                    review=data.get(i).getVendor_review();

                                    USER_REVIEW();
                                }
                            });
                        }
                    });

                }
                else
                {
                    myViewHolder.show_user_rating.setVisibility(View.GONE);
                }

                System.out.println("rating::"+rating);

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
            public TextView order_no,status,user_name,user_mobile,service,write_review,history_date,history_otp,uhistory_amount;
            public EditText enter_review;
            public ImageView user_image;
            RatingBar ratingBar;
            LinearLayout show_user_rating;

            public MyViewHolder(View view) {
                super(view);
                write_review=view.findViewById(R.id.write_review);
                enter_review=view.findViewById(R.id.enter_review);
                ratingBar=view.findViewById(R.id.user_rating);
                order_no = view.findViewById(R.id.user_history_id);
                status = view.findViewById(R.id.user_history_status);
                show_user_rating=view.findViewById(R.id.show_user_rating);
                user_name = view.findViewById(R.id.user_history_name);
                user_mobile = view.findViewById(R.id.user_history_mobile);
                user_image= view.findViewById(R.id.user_history_image);
                service=view.findViewById(R.id.user_history_service);
                history_otp=view.findViewById(R.id.user_history_otp);
                history_date=view.findViewById(R.id.service_history_date);
                uhistory_amount=view.findViewById(R.id.uhistory_amount);
            }
        }
    }

    private void USER_REVIEW() {
        System.out.println("review::"+review);

        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.update_user_review(unique_id,review);

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL:",response.toString());
             //   p.dismiss();

                try{
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                        Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    else {
                        Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                catch (Exception ex){
                    Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                    snackbar.show();
                    USER_REVIEW();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.i("FAILURE ERROR:",t.getMessage());
               // p.dismiss();
            }
        });
    }

    private void USER_RATING() {
        System.out.println("rating::"+rating);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.update_user_rating(unique_id,rating);


        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL::",response.toString());
             //   p.dismiss();
                try{
                   if (response.body().get(0).getError().equals("0"))
                   {
                       Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
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
               // p.dismiss();
            }
        });
    }
}
