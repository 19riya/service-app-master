package com.hrproject;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.google.android.material.snackbar.Snackbar;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {
    UserSessionManager session;
    EditText old_pass,new_pass,confirm_pass;
    Button update,cancel;
    String vendor_id,type;
    LinearLayout layout;
    Toolbar toolbar;
    ImageView re_show_hide,old_show_hide,new_show_hide;
    String inputOld,inputNew;
    AlertDialog p;
    SweetAlertDialog ff;
    int pass_value=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);

        toolbar=findViewById(R.id.change_pass_tool);
        old_pass=findViewById(R.id.current_pass);
        new_pass=findViewById(R.id.new_pass);
        confirm_pass=findViewById(R.id.re_type_new_pass);
        re_show_hide=findViewById(R.id.re_show_hide);
        old_show_hide=findViewById(R.id.old_show_hide);
        new_show_hide=findViewById(R.id.new_show_hide);
        layout=findViewById(R.id.pass_layout);

        cancel=findViewById(R.id.cancel_change);
        update=findViewById(R.id.save_changes);

        session = new UserSessionManager(ChangePassword.this);
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        type=user.get(session.KEY_TYPE);
        vendor_id = user.get(session.KEY_ID);

        toolbar.setTitle(getResources().getString(R.string.title_change_password));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {@Override
        public void onClick(View v)
        {
            finish();
        }
        });


        re_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass_value==0){
                    confirm_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    re_show_hide.setImageDrawable(ContextCompat.getDrawable(ChangePassword.this,R.drawable.ic_remove_red_eye_black_24dp));
                    pass_value=1;
                }

                else{
                    confirm_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    re_show_hide.setImageDrawable(ContextCompat.getDrawable(ChangePassword.this,R.drawable.ic_visibility_off_grey_900_24dp));
                    pass_value=0;
                }
            }
        });

        old_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass_value==0){
                    old_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    old_show_hide.setImageDrawable(ContextCompat.getDrawable(ChangePassword.this,R.drawable.ic_remove_red_eye_black_24dp));
                    pass_value=1;
                }

                else{

                    old_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    old_show_hide.setImageDrawable(ContextCompat.getDrawable(ChangePassword.this,R.drawable.ic_visibility_off_grey_900_24dp));
                    pass_value=0;
                }
            }
        });

        new_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass_value==0){
                    new_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    new_show_hide.setImageDrawable(ContextCompat.getDrawable(ChangePassword.this,R.drawable.ic_remove_red_eye_black_24dp));
                    pass_value=1;
                }

                else{

                    new_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    new_show_hide.setImageDrawable(ContextCompat.getDrawable(ChangePassword.this,R.drawable.ic_visibility_off_grey_900_24dp));
                    pass_value=0;
                }
            }
        });

        // CANCEL Button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chngePass();
            }
        });
    }

    private void chngePass() {
        ConnectionDetector cd = new ConnectionDetector(ChangePassword.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            if (!validate())
                onLoginFailed();
            else {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                        (ChangePassword.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                p = mBuilder.create();
                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                p.setCancelable(false);
                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                p.setCanceledOnTouchOutside(false);
                p.show();
                PASS_CHANGE();
            }
        }
        else
        {
            SweetAlertDialog ff=new SweetAlertDialog(ChangePassword.this,SweetAlertDialog.WARNING_TYPE);
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

    private void PASS_CHANGE() {
        Log.d("user_id",vendor_id);
        Log.d("old_password",old_pass.getText().toString());
        Log.d("new_password",new_pass.getText().toString());
        Log.d("type",type);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=apiInterface.change_password(vendor_id,inputOld,inputNew,type);


        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {
                Log.d("URL::",response.toString());

                Log.d("new URL::",response.body().toString());
                p.dismiss();

                if (response.body().get(0).getError().equalsIgnoreCase("1")){
                    Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else {
                    ff=new SweetAlertDialog(ChangePassword.this,SweetAlertDialog.SUCCESS_TYPE);
                    ff.setTitleText(getResources().getString(R.string.congo));
                    ff.setContentText(response.body().get(0).getMsg());
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            sweetAlertDialog.dismissWithAnimation();
                            //  session.createUserLoginSession(user_name,user_password,type);
                           finish();
                        }
                    });
                    ff.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                            if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP)
                            { }
                                return true;
                        }
                    });
                    ff.show();
                    Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                    snackbar.show();                    }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.i("Failure Error::",t.getMessage());
          }
        });

    }

   /* @Override
    public void onBackPressed() {
        if (ff != null && ff.isShowing()) {

        } else {
            super.onBackPressed();
        }
    }*/

    private void onLoginFailed() { }

    private boolean validate() {
        boolean valid = true;
         inputOld = old_pass.getText().toString().trim();
         inputNew = new_pass.getText().toString().trim();

        if (inputOld.isEmpty()) {
            old_pass.requestFocus();
            old_pass.setError(getResources().getString(R.string.reg_password));
            valid=false;
        }
        else
            old_pass.setError(null);

        if (inputNew.isEmpty()) {
            new_pass.requestFocus();
            new_pass.setError(getResources().getString(R.string.reg_password));
            valid=false;
        }
        else
            new_pass.setError(null);

        if (inputOld.equals(inputNew)) {
            new_pass.requestFocus();
            new_pass.setError(getResources().getString(R.string.err_new_pass));
            valid=false;
        }
        else
            new_pass.setError(null);

        if (!new_pass.getText().toString().equals(confirm_pass.getText().toString())){
            confirm_pass.setError(getResources().getString(R.string.err_confrm_pas));
            valid=false;
        }
        else confirm_pass.setError(null);
        return valid;
    }



}