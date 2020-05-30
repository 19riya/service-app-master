package com.hrproject.Activities.user;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hrproject.GetterSetter.profile_get_set;
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

public class User_Show_Profile extends AppCompatActivity
{
    CircularImageView user_image;
    TextView user_name,user_email,user_mobile,user_dob,user_address1,user_address2;
    ImageView edit_information;
    AlertDialog p;
    String name,image,email,address1,address2,dob,mobile,user_id,descrp;
    UserSessionManager session;
    LinearLayout layout_address2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__show__profile);

        session=new UserSessionManager(User_Show_Profile.this);
        HashMap<String,String> data=session.getUserDetails();
        user_id=data.get(session.KEY_ID);

        System.out.println("user_id:::"+user_id);

        layout_address2=findViewById(R.id.layout_address2);
        user_image=findViewById(R.id.user_image);
        user_name=findViewById(R.id.name);
        user_email=findViewById(R.id.email);
        user_mobile=findViewById(R.id.mobile);
        user_dob=findViewById(R.id.dob);
        user_address1=findViewById(R.id.address1);
        user_address2=findViewById(R.id.address2);
        edit_information=findViewById(R.id.edit_user_profile);

        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                (User_Show_Profile.this);
        View mView = getLayoutInflater().inflate(R.layout.loader, null);
        mBuilder.setView(mView);
        p = mBuilder.create();
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCancelable(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        p.setCanceledOnTouchOutside(false);
        p.show();

        edit_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(User_Show_Profile.this,Edit_User_Show_Profile.class);
                intent.putExtra("name",name);
                intent.putExtra("dob",dob);
                intent.putExtra("address1",address1);
                intent.putExtra("address2",address2);
                intent.putExtra("descrp",descrp);
                intent.putExtra("image",image);
                startActivity(intent);
            }
        });

        Toolbar toolbar=findViewById(R.id.tool_profile);
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

    }// end oncreate method

    @Override
    public void onResume(){
        super.onResume();

        showProfileJson();

    }

    private void showProfileJson()
    {

        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<profile_get_set>> call=a.show_user_profile(user_id);


        call.enqueue(new Callback<ArrayList<profile_get_set>>()
        {
            @Override
            public void onResponse(Call<ArrayList<profile_get_set>> call, Response<ArrayList<profile_get_set>> response)
            {
                Log.d("URL:",response.toString());
                p.dismiss();
                name=response.body().get(0).getName();
                image=response.body().get(0).getProfile_image();
                email=response.body().get(0).getEmail();
                dob=response.body().get(0).getDob();
                mobile=response.body().get(0).getMobile();
                address1=response.body().get(0).getAddress();
                address2=response.body().get(0).getSecond_address();
                descrp=response.body().get(0).getDescription();

                System.out.println("name:"+name);
                System.out.println("dob:"+dob);
                System.out.println("address1:"+address1);
                System.out.println("address2:"+address2);
                System.out.println("image:"+image);
                System.out.println("email:"+email);

                System.out.println("user_descrp:"+descrp);

                if (address2.isEmpty()){
                    layout_address2.setVisibility(View.GONE);
                }
                else {
                    layout_address2.setVisibility(View.VISIBLE);
                }
                user_name.setText(name);
                user_address1.setText(address1);
                user_address2.setText(address2);
                user_dob.setText(dob);
                user_email.setText(email);
                user_mobile.setText(mobile);

                Picasso.with(User_Show_Profile.this).load(image)
                        .placeholder(R.drawable.man).into(user_image);

                System.out.println("user_profile_image:"+image);

            }

            @Override
            public void onFailure(Call<ArrayList<profile_get_set>> call, Throwable t)
            {
                p.dismiss();
                Log.d("eerror",t.getMessage());
            }
        });
    }
}
