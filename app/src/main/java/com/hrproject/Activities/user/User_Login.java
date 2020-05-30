package com.hrproject.Activities.user;

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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hrproject.Activities.Forgot_Password;
import com.hrproject.Activities.Otp_Verify;
import com.hrproject.Activities.Registration_Activity;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Login extends AppCompatActivity
{
    TextInputLayout name,pass;
    LinearLayout login_btn;
    String user_name,user_password,type;
    AlertDialog progress;
    LinearLayout layout;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);

        name=findViewById(R.id.name);
        pass=findViewById(R.id.pass);
        login_btn=findViewById(R.id.login_btn);
        layout=findViewById(R.id.login_layout);

        type="user";
        session=new UserSessionManager(User_Login.this);

        Toolbar toolbar=findViewById(R.id.tool_login);
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




        findViewById(R.id.forgot_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(User_Login.this, Forgot_Password.class);
               i.putExtra("type","user");
                startActivity(i);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checknet();

            }
        });

        findViewById(R.id.signup_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(User_Login.this, Registration_Activity.class));
            }
        });

    }// end  oncreate mehod


    private void onLoginFailed(){}

    public  boolean validate()
    {
        boolean valid=true;
         user_name=name.getEditText().getText().toString();
         user_password=pass.getEditText().getText().toString();

        if( (user_name.isEmpty()) || (user_name.length()!=10))
        {
            name.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_mobile)+"</font>"));
            valid = false;
        }
        else
        {
            name.setError(null);
        }

        if( (user_password.isEmpty()) || (user_password.trim().length()<4))
        {
            pass.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_pass)+"</font>"));
            valid = false;
        }
        else
        {
            pass.setError(null);
        }

        return  valid;
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(User_Login.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {

            if (!validate()) {
                onLoginFailed();
                return;
            }
            else
            {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(User_Login.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                progress = mBuilder.create();
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progress.setCancelable(false);
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progress.setCanceledOnTouchOutside(false);
                progress.show();

                data_Login();
            }
        }
        else {
            SweetAlertDialog ff=new SweetAlertDialog(User_Login.this,SweetAlertDialog.WARNING_TYPE);
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
        System.out.println("user_name:"+user_name);
        System.out.println("user_password:"+user_password);
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.getuser_login(user_name,user_password);
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("URL:",response.toString());
                if(response.body().get(0).getError().equalsIgnoreCase("0"))
                {
                    session.createUserLoginSession(response.body().get(0).getUser_id(),user_name,type);

                    Log.d("user_id",response.body().get(0).getUser_id());
                    Log.d("user_name",user_name);
                    Log.d("type",type);

                    Intent intent=new Intent(User_Login.this,Otp_Verify.class);
                    intent.putExtra("mobile",user_name);
                    intent.putExtra("type","user");
                    startActivity(intent);
                    finish();
                }

                else if (response.body().get(0).getError().equalsIgnoreCase("2")){
                    Snackbar snackbar=Snackbar.make(layout,response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Intent intent=new Intent(User_Login.this, Otp_Verify.class);
                    intent.putExtra("mobile",user_name);
                    intent.putExtra("type","user");
                    startActivity(intent);
                }

                else
                {
                    SweetAlertDialog ff=new SweetAlertDialog(User_Login.this,SweetAlertDialog.ERROR_TYPE);
                    ff.setTitleText(getResources().getString(R.string.failed));
                    ff.setContentText(response.body().get(0).getMsg());
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(R.string.ok, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    ff.show();
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
                progress.dismiss();
                Log.d("failure",t.getMessage());
            }
        });
    }
}
