package com.hrproject.Activities.Vendor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.hrproject.GetterSetter.profile_get_set;
import com.hrproject.HelperClasses.TouchImageView;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vendor_Show_Profile extends AppCompatActivity {
    UserSessionManager session;
    String vendor_id;

    TextView vendor_name,email,mobile,dob,address,experience,skill_categories,long_root;
    TextView skill_sub_categories,skill_description,govt_id_type;

    String svendor_name,semail,smobile,sdob,saddress,sexperience,sskill_categories;
    String sskill_ssub_categories,sskill_description,sgovt_id_type;
    String long_status;

    CircularImageView profile_image;
    ImageView edit_profile;
   ImageView certificate_image,id_front_image,id_back_image;


   LinearLayout ven_skillDescrp_layout,ven_expImage_layout,ven_exp_layout;

    String sprofile_image,scertificate_image,sid_front_image,sid_back_image;
    AlertDialog p;
    int image=R.drawable.man;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_show_profile);

        session=new UserSessionManager(Vendor_Show_Profile.this);
        HashMap<String,String> data=session.getUserDetails();
        vendor_id=data.get(session.KEY_ID);

        System.out.println("vendor_id::"+vendor_id);

        ven_skillDescrp_layout=findViewById(R.id.ven_skillDescrp_layout);
        ven_expImage_layout=findViewById(R.id.ven_expImage_layout);
        ven_exp_layout=findViewById(R.id.ven_exp_layout);

        long_root=findViewById(R.id.long_root);
        vendor_name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        dob=findViewById(R.id.dob);
        address=findViewById(R.id.address1);
        experience=findViewById(R.id.experience);
        skill_categories=findViewById(R.id.skill_categories);
        skill_sub_categories=findViewById(R.id.skill_sub_categories);
        skill_description=findViewById(R.id.skill_description);
        govt_id_type=findViewById(R.id.id_type);
        profile_image=findViewById(R.id.vendor_image);
        certificate_image=findViewById(R.id.vendor_experience_image);
        id_back_image=findViewById(R.id.id_back_image);
        id_front_image=findViewById(R.id.id_front_image);
        edit_profile=findViewById(R.id.edit_vendor_profile);

        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                (Vendor_Show_Profile.this);
        View mView = getLayoutInflater().inflate(R.layout.loader, null);
        mBuilder.setView(mView);
        p = mBuilder.create();
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCancelable(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        p.setCanceledOnTouchOutside(false);
        p.show();

        Toolbar toolbar=findViewById(R.id.tool_ven_profile);
        toolbar.setTitle(getResources().getString(R.string.tool_profile));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { finish(); }
        });



        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Vendor_Show_Profile.this,Edit_Vendor_Profile.class);
                intent.putExtra("name",svendor_name);
                intent.putExtra("dob",sdob);
                intent.putExtra("address",saddress);
                intent.putExtra("experience",sexperience);
                intent.putExtra("skill_cat",sskill_categories);
                intent.putExtra("descrp",sskill_description);
                intent.putExtra("skill_sub_cat",sskill_ssub_categories);
                intent.putExtra("profile_image",sprofile_image);
                intent.putExtra("long_status",long_status);
                startActivity(intent);
            }
        });
    }

    @Override
    public  void onResume(){
        super.onResume();

        SHOW_VENDOR_PROFILE();
    }

    private void SHOW_VENDOR_PROFILE()
    {
       System.out.println("vendor_id"+vendor_id);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<profile_get_set>> call=apiInterface.show_vendor_profile(vendor_id);

        call.enqueue(new Callback<ArrayList<profile_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<profile_get_set>> call, Response<ArrayList<profile_get_set>> response) {

                Log.d("URL::",response.toString());
                Log.d("new URL::",response.body().toString());
                p.dismiss();


                svendor_name=response.body().get(0).getName();
                semail=response.body().get(0).getEmail();
                smobile=response.body().get(0).getMobile();
                sdob=response.body().get(0).getDob();
                sexperience=response.body().get(0).getExperiance();
                sskill_categories=response.body().get(0).getSkill_categories();
                sskill_description=response.body().get(0).getSkill_description();
                sskill_ssub_categories=response.body().get(0).getSkill_sub_categories();
                sgovt_id_type=response.body().get(0).getGov_id_type();
                saddress=response.body().get(0).getAddress();
                sprofile_image=response.body().get(0).getProfile_image();
                scertificate_image=response.body().get(0).getExperiance_certificate_image();
                sid_front_image=response.body().get(0).getGov_id_front_image();
                sid_back_image=response.body().get(0).getGov_id_back_image();
                long_status=response.body().get(0).getLong_status();


                if (sexperience.isEmpty()){
                    ven_exp_layout.setVisibility(View.GONE);
                }
                else{
                    ven_exp_layout.setVisibility(View.VISIBLE);

                }

                if (scertificate_image=="0"){
                    certificate_image.setVisibility(View.GONE);
                }
                else{
                    certificate_image.setVisibility(View.VISIBLE);

                }

                if (sskill_description.isEmpty()){
                    ven_skillDescrp_layout.setVisibility(View.GONE);
                }
                else{
                    ven_skillDescrp_layout.setVisibility(View.VISIBLE);

                }

                profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Vendor_Show_Profile.this);
                        View mView = getLayoutInflater().inflate(R.layout.pager_main, null);

                        ImageView proceed = (ImageView) mView.findViewById(R.id.zoom_certificate);

                        Picasso.with(Vendor_Show_Profile.this).load(sprofile_image).placeholder(R.drawable.man).into(proceed);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                    }
                });


                certificate_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Vendor_Show_Profile.this);
                    View mView = getLayoutInflater().inflate(R.layout.pager_main, null);

                    ImageView proceed = (ImageView) mView.findViewById(R.id.zoom_certificate);

                    Picasso.with(Vendor_Show_Profile.this).load(response.body().get(0).getExperiance_certificate_image()).placeholder(R.drawable.man).into(proceed);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    }
                });


                id_front_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Vendor_Show_Profile.this);
                        View mView = getLayoutInflater().inflate(R.layout.pager_main, null);

                        ImageView proceed = (ImageView) mView.findViewById(R.id.zoom_certificate);

                        Picasso.with(Vendor_Show_Profile.this).load(response.body().get(0).getGov_id_front_image()).placeholder(R.drawable.man).into(proceed);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                    }
                });

                id_back_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Vendor_Show_Profile.this);
                        View mView = getLayoutInflater().inflate(R.layout.pager_main, null);

                        ImageView proceed = mView.findViewById(R.id.zoom_certificate);
                        Picasso.with(Vendor_Show_Profile.this).load(response.body().get(0).getGov_id_back_image()).placeholder(R.drawable.man).into(proceed);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                    }
                });


                if (long_status.equals("1")){
                    System.out.println("check_status:"+"yes");
                    long_root.setText("Yes");
                }
                else {
                    long_root.setText("No");
                }


                vendor_name.setText(svendor_name);
                email.setText(semail);
                mobile.setText(smobile);
                dob.setText(sdob);
                experience.setText(sexperience);
                skill_categories.setText(sskill_categories);
                skill_sub_categories.setText(sskill_ssub_categories);
                skill_description.setText(sskill_description);
                address.setText(saddress);
                govt_id_type.setText(sgovt_id_type);

                Picasso.with(Vendor_Show_Profile.this).load(sprofile_image)
                        .placeholder(R.drawable.man).into(profile_image);

                Picasso.with(Vendor_Show_Profile.this).load(scertificate_image)
                        .placeholder(R.drawable.man).into(certificate_image);

                Picasso.with(Vendor_Show_Profile.this).load(sid_front_image)
                        .placeholder(R.drawable.man).into(id_front_image);

                Picasso.with(Vendor_Show_Profile.this).load(sid_back_image)
                        .placeholder(R.drawable.man).into(id_back_image);

            }

            @Override
            public void onFailure(Call<ArrayList<profile_get_set>> call, Throwable t) {
                Log.i("FAILURE_ERROR....",t.getMessage());
                p.dismiss();
            }
        });
    }


}
