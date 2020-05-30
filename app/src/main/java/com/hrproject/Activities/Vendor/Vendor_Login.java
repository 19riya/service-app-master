package com.hrproject.Activities.Vendor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hrproject.Activities.Forgot_Password;
import com.hrproject.Activities.Otp_Verify;
import com.hrproject.Activities.user.User_Login;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.onesignal.OneSignal;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vendor_Login extends AppCompatActivity {

    TextInputLayout name,pass;
    LinearLayout login_btn;
    String type;
    UserSessionManager sessionManager;
    String vendor_mobile,vendor_password;
    AlertDialog p;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__login);

        name=findViewById(R.id.name);
        pass=findViewById(R.id.pass);
        login_btn=findViewById(R.id.login_vendor);
        layout=findViewById(R.id.ven_login_layout);

        type="vendor";
        sessionManager=new UserSessionManager(Vendor_Login.this);

        Toolbar toolbar=findViewById(R.id.tool_ven_login);
        toolbar.setTitle(getResources().getString(R.string.back));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { finish(); }
        });


        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checknet();
            }
        });

        findViewById(R.id.forgot_vendor).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i=new Intent(Vendor_Login.this, Forgot_Password.class);
               i.putExtra("type","vendor");
               startActivity(i);
           }
       });

        findViewById(R.id.signup_vendor).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent( Vendor_Login.this,Vendor_Registration.class));
            }
        });
    } // end oncreate method



    private void onLoginFailed(){}

    public  boolean validate()
    {
        boolean valid=true;
        vendor_mobile=name.getEditText().getText().toString();
        vendor_password=pass.getEditText().getText().toString();


        if( (vendor_mobile.isEmpty()) || (vendor_mobile.length()!=10))
        {
            name.setError(  Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_mobile)+"</font>"));
            valid = false;
        }
        else
        {
            name.setError(null);
        }


        if( (vendor_password.isEmpty()) || (vendor_password.trim().length()<4))
        {
            pass.setError(  Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_pass)+"</font>"));
            valid = false;
        }
        else
        {
            pass.setError(null);
        }



        return  valid;
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Vendor_Login.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {

            if (!validate()) {
                onLoginFailed();
                return;
            }
            else
            {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Vendor_Login.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                p = mBuilder.create();
                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                p.setCancelable(false);
                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                p.setCanceledOnTouchOutside(false);
                p.show();

                data_Login();
            }
        } else {
            SweetAlertDialog ff=new SweetAlertDialog(Vendor_Login.this,SweetAlertDialog.WARNING_TYPE);
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

    private void data_Login()
    {
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.getvendor_login(vendor_mobile,vendor_password);
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                if(response.body().get(0).getError().equalsIgnoreCase("0"))
                {
                    Log.i("URL:",response.toString());
                    p.dismiss();
                    sessionManager.createUserLoginSession(response.body().get(0).getVendor_id(),vendor_mobile,type);

                    Intent intent=new Intent(Vendor_Login.this, Otp_Verify.class);
                    intent.putExtra("mobile",vendor_mobile);
                    intent.putExtra("type","vendor");

                    startActivity(intent);
                    finish();
                }

                else if (response.body().get(0).getError().equalsIgnoreCase("2")){
                    Snackbar snackbar=Snackbar.make(layout,response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Intent intent=new Intent(Vendor_Login.this, Otp_Verify.class);
                    intent.putExtra("mobile",vendor_mobile);
                    intent.putExtra("type","vendor");
                    startActivity(intent);
                }

                else
                {

                    SweetAlertDialog ff=new SweetAlertDialog(Vendor_Login.this,SweetAlertDialog.ERROR_TYPE);
                    ff.setTitleText(getResources().getString(R.string.failed));
                    ff.setContentText(response.body().get(0).getMsg());
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            sweetAlertDialog.dismissWithAnimation();
                            p.dismiss();
                        }
                    });
                    ff.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
                p.dismiss();
                Log.d("failure",t.getMessage());
            }
        });

    }
}
