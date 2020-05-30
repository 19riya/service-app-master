package com.hrproject.Activities.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
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

public class Rating_Review extends AppCompatActivity {
    Toolbar rating_toolbar;
    GlobalVariables globalVariables;
    AlertDialog p;
    LinearLayout heading_english,heading_hindi;
    TextView vendor_name;
    RatingBar user_rating;
    EditText user_review;
    UserSessionManager sessionManager;
    String rating="",review="",unique_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating__review);

        globalVariables=GlobalVariables.getInstance();
        sessionManager=new UserSessionManager(Rating_Review.this);

        HashMap<String,String> data=sessionManager.getChatId();
        unique_id=data.get(sessionManager.KEY_CHATID);


        heading_english=findViewById(R.id.heading_english);
        heading_hindi=findViewById(R.id.heading_hindi);
        user_rating=findViewById(R.id.upd_user_rating);
        user_review=findViewById(R.id.upd_user_review);
        vendor_name=findViewById(R.id.vendor_name);
        rating_toolbar=findViewById(R.id.rating_toolbar);
        rating_toolbar.setTitle(""+getResources().getString(R.string.review));
        rating_toolbar.setNavigationIcon(R.drawable.back_black_arrow);
        rating_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (sessionManager.getlanguage().equalsIgnoreCase("en")){
            heading_hindi.setVisibility(View.GONE);
            heading_english.setVisibility(View.VISIBLE);
        }
        else if (sessionManager.getlanguage().equalsIgnoreCase("hi")){
            heading_hindi.setVisibility(View.VISIBLE);
            heading_english.setVisibility(View.GONE);
        }

        findViewById(R.id.submit_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checknet();
            }
        });

        findViewById(R.id.user_cancel_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setChatId("","","");
                globalVariables.setUnique_id("");
                startActivity(new Intent(Rating_Review.this,User_Welcome.class));
                finish();
            }
        });

        System.out.println("rating_review employee_name:"+data.get(sessionManager.KEY_SNAME));
        vendor_name.setText("Mr. "+data.get(sessionManager.KEY_SNAME));
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Rating_Review.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {

            if (!validate()) {
                onLoginFailed();
                return;
            }
            else
            {
               LOADER();
                SUBMIT_REVIEW();
            }
        }
        else {
            SweetAlertDialog ff=new SweetAlertDialog(Rating_Review.this,SweetAlertDialog.WARNING_TYPE);
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

    private void SUBMIT_REVIEW() {
        System.out.println("rating:"+rating);
        System.out.println("review:"+review);
        System.out.println("unique_id:"+unique_id);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.upd_user_rating_review(unique_id
                                                ,rating,review);
        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL:",response.toString());
                p.dismiss();

                try{
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                        user_review.setText("");
                        user_rating.setRating(0);
                        sessionManager.setChatId("","","");
                        globalVariables.setUnique_id("");
                        Toast.makeText(Rating_Review.this
                                , ""+response.body().get(0).getMsg()
                                , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Rating_Review.this,User_Welcome.class));
                        finish();
                    }
                }
                catch (Exception ex){
                    Log.d("userRatingReview_error:",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                p.dismiss();
                Log.d("userRatngRevew_failure:",t.getMessage());
            }
        });

    }

    private void onLoginFailed() {

    }

    private void LOADER() {
        AlertDialog.Builder mBuilder =
                new AlertDialog.Builder(Rating_Review.this);
        View mView = getLayoutInflater().inflate(R.layout.loader, null);
        mBuilder.setView(mView);
        p = mBuilder.create();
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCancelable(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCanceledOnTouchOutside(false);
        p.show();
    }

    private boolean validate() {
        rating=""+user_rating.getRating();
        review=user_review.getText().toString();

        boolean valid=true;

        if (review.isEmpty()){
            user_review.setError(Html.fromHtml("<font color='#ff0000' >"+""+
                    getResources().getString(R.string.err_review)+"</font>"));
            valid=false;
        }
        else {
            user_review.setError(null);
        }

        if (rating.isEmpty()){
            Snackbar.make(findViewById(R.id.upd_review_layout)
                    ,"Please give rating to vendor"
                    ,Snackbar.LENGTH_LONG).show();
            valid=false;
        }
        else {
         //   valid=true;
        }


        return valid;
    }
}
