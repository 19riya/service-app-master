package com.hrproject.Activities.Vendor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import com.hrproject.Activities.user.Rating_Review;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Accept_Rquest_Order2 extends AppCompatActivity {
    Button call_user,message,submit_otp;
    TextView user_otp;
    String unique_id,number,vendor_id,user_id;
    UserSessionManager session;
    GlobalVariables globalVariables;
    CoordinatorLayout match_otp;
    Context c;
    AlertDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept__rquest__order2);
        c=this;

      /*  Intent intent=getIntent();
        order=intent.getExtras().getString("order");
        number=intent.getExtras().getString("phone");*/
      //  user_id=intent.getExtras().getString("user_id");
       // System.out.println("user_id:"+user_id);

        globalVariables=GlobalVariables.getInstance();

        session=new UserSessionManager(Accept_Rquest_Order2.this);
        HashMap<String,String> data=session.getUserDetails();
        vendor_id=data.get(session.KEY_ID);

        unique_id=globalVariables.getUnique_id();

        System.out.println("unique_id:"+unique_id);

     /*   System.out.println("order:"+order);
        System.out.println("number:"+number);*/
        System.out.println("vendor_id:"+vendor_id);

        submit_otp=findViewById(R.id.submit_user_otp);
        user_otp=findViewById(R.id.enter_user_otp);
        match_otp=findViewById(R.id.match_otp);
        match_otp.setVisibility(View.VISIBLE);
      /*  androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Accept_Rquest_Order2.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_order2_detail, null);
        mBuilder.setView(mView);
        androidx.appcompat.app.AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        call_user=mView.findViewById(R.id.call_user);
        message=mView.findViewById(R.id.chat_user);
        order_no=mView.findViewById(R.id.order_no2);

        order_no.setText(getResources().getString(R.string.order_no)+"#000"+order);
        call_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Accept_Rquest_Order2.this,Messaging_Vendor_Activity.class);
                intent.putExtra("notifyStatus","0");
                startActivity(intent);



                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:" + number));
                startActivity(smsIntent);
            }
        });*/
        Log.i("welcome", "welcome");

        submit_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_otp.getText().toString().length()==0)
                    user_otp.setError(getResources().getString(R.string.err_otp));

                else
                    checknet();
            }
        });

    }//end onCreate()

  /*  private void checknet_request() {
        ConnectionDetector cd = new ConnectionDetector(Accept_Rquest_Order2.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent){
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (Accept_Rquest_Order2.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
           *//* ACCEPT_REQUEST();*//*
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Accept_Rquest_Order2.this,SweetAlertDialog.WARNING_TYPE);
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

    }*/

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Accept_Rquest_Order2.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent){
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (Accept_Rquest_Order2.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
            CHECK_OTP();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Accept_Rquest_Order2.this,SweetAlertDialog.WARNING_TYPE);
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

    private void CHECK_OTP() {

        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.check_service_otp(unique_id,user_otp.getText().toString());

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL::",response.toString());
                p.dismiss();

                try{
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){

                        SweetAlertDialog ff=new SweetAlertDialog(Accept_Rquest_Order2.this,SweetAlertDialog.SUCCESS_TYPE);
                        ff.setContentText(response.body().get(0).getMsg());
                        ff.setCanceledOnTouchOutside(false);
                        ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog)
                            {

                                System.out.println("employee_name:"+globalVariables.getEmployee_name());

                                session.setChatId(unique_id,"4",globalVariables.getEmployee_name());
                                startActivity(new Intent(Accept_Rquest_Order2.this, VendorRating_Review.class));
                                sweetAlertDialog.dismissWithAnimation();
                                finish();

                            }
                        });

                        ff.show();

                    /*    Snackbar snackbar=Snackbar.make(match_otp,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();*/
                        user_otp.setText(" ");

                    }
                    else{
                        displaySnackbar(response.body().get(0).getMsg(),match_otp);
                        /*Snackbar snackbar=Snackbar.make(match_otp,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();*/
                    }
                }
                catch (Exception ex) {
                    Log.d("ERROR!::", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                p.dismiss();
                Log.i("FAILURE ERROR:",t.getMessage());
            }
        });
    }

    private static void displaySnackbar(String msg, CoordinatorLayout bottom_layout) {
        int duration = 3000;
        final TSnackbar snackbar = TSnackbar.make(bottom_layout, msg, TSnackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.WHITE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    snackbar.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, duration);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        View snackbarView = snackbar.getView();
        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
        params.height = 150;
        snackbarView.setLayoutParams(params);
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setMaxLines(1);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
