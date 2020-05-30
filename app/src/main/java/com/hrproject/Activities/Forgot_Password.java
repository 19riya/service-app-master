package com.hrproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.hrproject.Activities.Vendor.Vendor_Login;
import com.hrproject.Activities.user.User_Login;
import com.hrproject.GetterSetter.Get_Set;
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

public class Forgot_Password extends AppCompatActivity
{
    TextInputLayout mobile_text;
    Button mobile_submit;
    String type;
    String mobile_valeu;
    UserSessionManager session;
    AlertDialog p,alertDialog;
    EditText otp_change,new_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);

        /*session=new UserSessionManager(Forgot_Password.this);
        HashMap<String,String> data=session.getUserDetails();
        type=data.get(session.KEY_TYPE);
*/

        Toolbar toolbar=findViewById(R.id.tool_forgot_profile);
        toolbar.setTitle(getResources().getString(R.string.forgot_password));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { finish(); }
        });


        mobile_text=findViewById(R.id.mobile_text);
        mobile_submit=findViewById(R.id.mobile_submit);

        type = getIntent().getExtras().getString("type");

        mobile_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checknet();
            }
        });

    }// end oncreate method

    private void onLoginFailed(){}

    public  boolean validate()
    {
        boolean valid=true;
        mobile_valeu=mobile_text.getEditText().getText().toString();

        if( (mobile_valeu.isEmpty()) || (mobile_valeu.length()!=10))
        {
            mobile_text.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_mobile)+" </font>"));
            valid = false;
        }
        else
        {
            mobile_text.setError(null);
        }
        return  valid;
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Forgot_Password.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {

            if (!validate()) {
                onLoginFailed();
                return;
            }
            else
            {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Forgot_Password.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                p = mBuilder.create();
                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                p.setCancelable(false);
                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                p.setCanceledOnTouchOutside(false);
                p.show();

                submit_forgot();
            }
        }
        else {
            SweetAlertDialog ff=new SweetAlertDialog(Forgot_Password.this,SweetAlertDialog.WARNING_TYPE);
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

    private void submit_forgot()
    {
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.getresend_otp(mobile_valeu,type);
        call.enqueue(new Callback<ArrayList<Get_Set>>()
        {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("Urlis ",response.toString());

                if(response.body().get(0).getError().equalsIgnoreCase("0"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Forgot_Password.this);
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.forgot_otp_verify, null);

                    builder.setCancelable(false);
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    Button submit_pass,cancel_otp;
                    TextView no_show;
                    no_show=dialogView.findViewById(R.id.no_show);

                    otp_change =  dialogView.findViewById(R.id.otp_send);
                    new_password =  dialogView.findViewById(R.id.new_password);

                    submit_pass=dialogView.findViewById(R.id.submit_otp);
                    cancel_otp=dialogView.findViewById(R.id.cancel_otp);

                    no_show.setText("+91-"+mobile_valeu);
                    cancel_otp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    submit_pass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if(!validate1())
                            {
                                   return;
                            }
                            else
                            {
                                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Forgot_Password.this);
                                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                                mBuilder.setView(mView);
                                p = mBuilder.create();
                                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                p.setCancelable(false);
                                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                p.setCanceledOnTouchOutside(false);
                                p.show();

                                new_password_change();
                            }
                        }
                    });

                }// end if under onResoponse
                else
                {
                    new SweetAlertDialog(Forgot_Password.this,
                            SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.failed))
                            .setContentText(response.body().get(0).getMsg())
                            .show();
                }
                p.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
                Log.d("failure ",t.getMessage());
                p.dismiss();
            }
        });
    }


    public boolean validate1() {
        boolean valid = true;

        String   otp_st=otp_change.getText().toString();

        String   user_pass_value=new_password.getText().toString();

        if ( (otp_st.isEmpty() ) || (otp_st.length()!=6))
        {
            otp_change.setError( Html.fromHtml(getResources().getString(R.string.err_otp)));
            valid = false;
        }
        else
        {   otp_change.setError(null);
        }

        if ((user_pass_value.isEmpty()) || (user_pass_value.length()<4) )
        {
            new_password.setError( Html.fromHtml(getResources().getString(R.string.err_pass)));
            valid = false;
        }
        else
        {   new_password.setError(null); }

        return valid;
    }// end validate


    private  void new_password_change()
    {
        ApiInterface a=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.getforgot_password
                (
                mobile_valeu,otp_change.getText().toString(),
                new_password.getText().toString(),type
                );
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("yurl_otp",response.toString());
                if (response.body().get(0).getError().equalsIgnoreCase("0")) {
                    p.dismiss();

                    alertDialog.dismiss();
                   // sweetAlertDialog.dismissWithAnimation();

                    if(type.equalsIgnoreCase("user")) {

                        startActivity(new Intent(Forgot_Password.this, User_Login.class));
                        finish();
                    }
                    else
                    {
                        startActivity(new Intent(Forgot_Password.this, Vendor_Login.class));
                        finish();
                    }
                }
                else
                {
                    p.dismiss();
                    SweetAlertDialog ff=new SweetAlertDialog(Forgot_Password.this, SweetAlertDialog.ERROR_TYPE);
                    ff.setTitleText(getResources().getString(R.string.failed));
                    ff.setContentText(response.body().get(0).getMsg());
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

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                Log.i("FAILURE ERROR::",t.getMessage());
                p.dismiss();
            }
        });
    }



}
