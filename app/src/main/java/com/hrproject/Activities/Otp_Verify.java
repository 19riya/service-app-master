package com.hrproject.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hrproject.Activities.Vendor.Vendor_Login;
import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.Activities.user.User_Login;
import com.hrproject.Activities.user.User_Welcome;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Otp_Verify extends AppCompatActivity
{
    TextView no_view;
    EditText otp_one,otp_two,otp_three,otp_four,otp_five,otp_six;
    Button verify_submit;
    AlertDialog progressDialog;
    String mobile,type,code1,notify;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp__verify);

        mobile = getIntent().getExtras().getString("mobile");
        type = getIntent().getExtras().getString("type");


     /*   session=new UserSessionManager(Otp_Verify.this);
        HashMap<String,String> data=session.getUserDetails();
        type=data.get(session.KEY_TYPE);
*/
        System.out.println("type"+type);

        no_view=findViewById(R.id.no_view);
        no_view.setText("91 "+mobile);

        notify=OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        System.out.println("notify..."+notify);

        otp_one= findViewById(R.id.otp_one);
        otp_two=findViewById(R.id.otp_two);
        otp_three=findViewById(R.id.otp_three);
        otp_four=findViewById(R.id.otp_four);
        otp_five=findViewById(R.id.otp_five);
        otp_six=findViewById(R.id.otp_six);

        verify_submit=findViewById(R.id.verify);

        otp_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (otp_one.getText().toString().length() == 1) {
                    otp_two.requestFocus();
                }
            }
        });
        otp_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp_two.getText().toString().length() == 0) {
                    otp_one.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (otp_two.getText().toString().length() == 1) {
                    otp_three.requestFocus();
                }
            }
        });
        otp_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp_three.getText().toString().length() == 0) {
                    otp_two.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otp_three.getText().toString().length() == 1) {
                    otp_four.requestFocus();
                }
            }
        });
        otp_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp_four.getText().toString().length() == 0) {
                    otp_three.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otp_four.getText().toString().length() == 1) {
                    otp_five.requestFocus();
                }
            }
        });
        otp_five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp_five.getText().toString().length() == 0) {
                    otp_four.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otp_five.getText().toString().length() == 1) {
                    otp_six.requestFocus();
                }
            }
        });
        otp_six.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp_six.getText().toString().length() == 0)
                {
                    otp_five.requestFocus();
                }
            }
            @Override
            public void afterTextChanged( Editable editable )
            {// We can call api to verify the OTP here or on an explicit button click
            }
        });

        verify_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checknet();
            }
        });

        TextView resendOtp=findViewById(R.id.resend_otp);
       /* new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished)
            {

                resendOtp.setText("Time Remaining: " + millisUntilFinished / 1000);
                resendOtp.setClickable(false);
                resendOtp.setTextColor(Color.BLACK);
                //here you can have your logic to set text to edittext
            }

            public void onFinish()
            {

            }

        }.start();*/


        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Otp_Verify.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                progressDialog = mBuilder.create();
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                otp_resend_json();
            }
        });
    }// end oncreate method

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Otp_Verify.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            if (otp_one.getText().toString().equals("")||otp_two.getText().toString().equals("")||otp_three.getText().toString().equals("")||otp_four.getText().toString().equals("")
                    ||otp_five.getText().toString().equals("")||otp_six.getText().toString().equals("")) {

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.otpLayout), getResources().getString(R.string.valid_code), Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
            else
            {
                code1 = otp_one.getText().toString()+otp_two.getText().toString()+otp_three.getText().toString()
                        +otp_four.getText().toString()+otp_five.getText().toString()+otp_six.getText().toString();

                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Otp_Verify.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                progressDialog = mBuilder.create();
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                otp_verify();

            }
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Otp_Verify.this,SweetAlertDialog.WARNING_TYPE);
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
    private void otp_verify()
    {
        System.out.println("mobile:"+mobile);
        System.out.println("code:"+code1);
        System.out.println("notify:"+notify);
        System.out.println("type:"+type);
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Get_Set>> call=a.getverify_otp(mobile,code1,type,notify);


        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("url",response.toString());
                progressDialog.dismiss();

                if(response.body().get(0).getError().equalsIgnoreCase("0"))
                {

                    if(type.equalsIgnoreCase("user")) {

                        Intent intent=(new Intent(Otp_Verify.this, User_Welcome.class));
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent=(new Intent(Otp_Verify.this, Vendor_Welcome.class));
                        intent.putExtra("notifyStatus","0");
                        startActivity(intent);
                        finish();
                    }
                    OneSignal.sendTag(type,"yes");
                    OneSignal.sendTag(type,"message_sent");
                }
                else
                {
                    SweetAlertDialog ff=new SweetAlertDialog(Otp_Verify.this,SweetAlertDialog.ERROR_TYPE);
                    ff.setTitleText(getResources().getString(R.string.failed));
                    ff.setContentText(response.body().get(0).getMsg());
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        { sweetAlertDialog.dismissWithAnimation(); }
                    });
                    ff.show();
                }


            }// end onresponse

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
                progressDialog.dismiss();
                Log.d("failure_Error",t.getMessage());
            }
        });
    }

    private void otp_resend_json()
    {
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Get_Set>> call=a.getresend_otp(mobile,type);
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("URL:",response.toString());
                progressDialog.dismiss();
                if(response.body().get(0).getError().equalsIgnoreCase("0"))
                {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.otpLayout), getResources().getString(R.string.otp_success), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else
                {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.otpLayout), getResources().getString(R.string.otp_failed), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                    Log.d("FAILURE ERROR:: ",t.getMessage());
                    progressDialog.dismiss();
            }
        });
    }
}
