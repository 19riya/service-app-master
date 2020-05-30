package com.hrproject.Activities.user;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.hrproject.GetterSetter.profile_get_set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Set_Service_Location extends AppCompatActivity {
    LinearLayout home_layout,other_layout,get_address,fetch_location,layout;
    TextView home_address,other_address;
    String user_id,home_add,other_add,category_is,sub_category_id,sub_category_name,category_name;
    ImageView back_button;
    UserSessionManager session;
    AlertDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set__service__location);

        session=new UserSessionManager(Set_Service_Location.this);
        HashMap<String,String> data=session.getUserDetails();

        user_id=data.get(session.KEY_ID);
        System.out.println("user_id:::"+user_id);


        category_is=getIntent().getExtras().getString("category_id");
        sub_category_id=getIntent().getExtras().getString("sub_category_id");
        sub_category_name=getIntent().getExtras().getString("sub_category_name");
        category_name=getIntent().getExtras().getString("category_name");

        System.out.println("category_id"+category_is);
        System.out.println("sub_category_id"+sub_category_id);
        System.out.println("sub_category_name"+sub_category_name);
        System.out.println("category_name"+category_name);

        back_button=findViewById(R.id.back_button);
        home_address=findViewById(R.id.home_address);
        fetch_location=findViewById(R.id.fetch_current_location);
        other_address=findViewById(R.id.other_address);
        home_layout=findViewById(R.id.home_address_layout);
        other_layout=findViewById(R.id.other_address_layout);
        get_address=findViewById(R.id.get_address);
        layout=findViewById(R.id.layout);

        VectorMasterView heartVector =findViewById(R.id.current_location_icon);
// find the correct path using name
        PathModel outline = heartVector.getPathModelByName("outline");
        outline.setStrokeColor(Color.parseColor("#000000"));
        outline.setTrimPathEnd(0.0f);

        // initialise valueAnimator and pass start and end float values
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                // set trim end value and update view
                outline.setTrimPathEnd((Float) valueAnimator.getAnimatedValue());
                heartVector.update();
            }
        });
        valueAnimator.start();


        VectorMasterView heartVector1 =findViewById(R.id.home_location_icon);
// find the correct path using name
        PathModel outline1 = heartVector1.getPathModelByName("outline");
        outline1.setStrokeColor(Color.parseColor("#000000"));
        outline1.setTrimPathEnd(0.0f);

        // initialise valueAnimator and pass start and end float values
        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator1.setDuration(1000);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator1) {

                // set trim end value and update view
                outline1.setTrimPathEnd((Float) valueAnimator1.getAnimatedValue());
                heartVector1.update();
            }
        });
        valueAnimator1.start();


        VectorMasterView heartVector2 =findViewById(R.id.other_location_icon);
// find the correct path using name
        PathModel outline2 = heartVector2.getPathModelByName("outline");
        outline2.setStrokeColor(Color.parseColor("#000000"));
        outline2.setTrimPathEnd(0.0f);

        // initialise valueAnimator and pass start and end float values
        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator2.setDuration(1000);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {

                // set trim end value and update view
                outline2.setTrimPathEnd((Float) valueAnimator2.getAnimatedValue());
                heartVector2.update();
            }
        });
        valueAnimator2.start();

        checknet();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        home_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Set_Service_Location.this,ProductList_Description.class);
                intent.putExtra("address",home_add);
                intent.putExtra("category_id",category_is);
                intent.putExtra("sub_category_name",sub_category_name);
                intent.putExtra("category_name",category_name);
                intent.putExtra("Sub_category_id",sub_category_id);
                startActivity(intent);
                finish();
            }
        });

        other_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Set_Service_Location.this,ProductList_Description.class);
                intent.putExtra("address",other_add);
                intent.putExtra("category_id",category_is);
                intent.putExtra("sub_category_name",sub_category_name);
                intent.putExtra("category_name",category_name);
                intent.putExtra("Sub_category_id",sub_category_id);
                startActivity(intent);
                finish();
            }
        });

        fetch_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Set_Service_Location.this, VMapsActivity.class);

                intent.putExtra("category_id",category_is);
                intent.putExtra("Sub_category_id",sub_category_id);
                intent.putExtra("sub_category_name",sub_category_name);
                intent.putExtra("category_name",category_name);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(Set_Service_Location.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (Set_Service_Location.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();

            ADDRESS_JSON();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Set_Service_Location.this,SweetAlertDialog.WARNING_TYPE);
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

    private void ADDRESS_JSON(){
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<profile_get_set>> call=apiInterface.show_user_address("3");

        p.dismiss();
        call.enqueue(new Callback<ArrayList<profile_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<profile_get_set>> call, Response<ArrayList<profile_get_set>> response) {

                Log.d("URL::",response.toString());
                p.dismiss();
                try {
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                        get_address.setVisibility(View.VISIBLE);

                        home_add=response.body().get(0).getAddress();
                        other_add=response.body().get(0).getSecond_address();

                        System.out.println("home_address::"+home_add);
                        System.out.println("other_address::"+other_add);

                        home_address.setText(""+home_add);
                        other_address.setText(""+other_add);

                  }
                    else{
                        Snackbar snackbar=Snackbar.make(layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                catch (Exception ex){
                    Log.d("ERROR::::",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<profile_get_set>> call, Throwable t) {
                p.dismiss();
                Log.d("FAILURE ERROR:::",t.getMessage());
            }
        });

    }
}
