package com.hrproject.Activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hrproject.Fragments.Vendor_Notification;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopVendorList extends AppCompatActivity {
    RecyclerView top_vendor_list;
    AlertDialog p;
    UserSessionManager sessionManager;
    String user_id,category_id,subCatg_id,sub_category_name,category_name,user_address;
    GlobalVariables globalVariables;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_vendor_list);


        globalVariables=GlobalVariables.getInstance();

        sessionManager=new UserSessionManager(TopVendorList.this);
        HashMap<String,String> data=sessionManager.getUserDetails();
        user_id=data.get(sessionManager.KEY_ID);
        category_id=getIntent().getExtras().getString("category_id");
        subCatg_id=getIntent().getExtras().getString("Sub_category_id");
        user_address=getIntent().getExtras().getString("user_address");

        sub_category_name = getIntent().getExtras().getString("sub_category_name");
        category_name = getIntent().getExtras().getString("category_name");

        System.out.println("user_id:"+user_id);
        System.out.println("category_id:"+category_id);
        System.out.println("subCatg_id:"+subCatg_id);


        top_vendor_list=findViewById(R.id.top_vendor_list);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(TopVendorList.this,RecyclerView.VERTICAL,false);
        top_vendor_list.setLayoutManager(layoutManager);

        checknet();
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(TopVendorList.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {

            loader();

            VENDOR_LIST();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(TopVendorList.this,SweetAlertDialog.WARNING_TYPE);
            ff.setTitleText(getResources().getString(R.string.failed));
            ff.setContentText(getResources().getString(R.string.internet_connection));
            ff.setCanceledOnTouchOutside(false);
            ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog)
                {
                    p.dismiss();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }


    }

    private void VENDOR_LIST() {
        System.out.println("user_idApi:"+user_id);
        System.out.println("category_idApi:"+category_id);
        System.out.println("subCatg_idApi:"+subCatg_id);


        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=apiInterface.vendor_list(user_id,category_id,subCatg_id);

        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {
                p.dismiss();
                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    top_vendor_list.setAdapter(new VendorList(TopVendorList.this,response.body()));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.i("VENDOR_LIST_FAILURE:",t.getMessage());
            }
        });
    }

    private class VendorList extends RecyclerView.Adapter<VendorList.MyViewHolder> {
        ArrayList<Get_Set> data;
        Context context;

        public VendorList(TopVendorList activity, ArrayList<Get_Set> body) {
            this.context=activity;
            this.data=body;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public VendorList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vendor_list_dapter,parent,false);

            return new VendorList.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VendorList.MyViewHolder holder, int position) {
            holder.list_venDecrp.setText(data.get(position).getVendor_description());
            holder.list_venExpr.setText(Html.fromHtml("<b>"+getResources().getString(R.string.ven_experience)+": "+ "</b>"
                    +data.get(position).getVendor_experiance()));

            holder.list_venName.setText(data.get(position).getVendor_name());
            holder.list_venRating.setText(data.get(position).getVendor_rating());

            Picasso.with(TopVendorList.this).load(data.get(position).getVendor_image())
                    .placeholder(R.drawable.man).into(holder.list_venImage);

            holder.select_vendor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(TopVendorList.this);
                    builder.setCancelable(false);
                    if (sessionManager.getlanguage().equalsIgnoreCase("en")){
                        builder.setMessage(""+getResources().getString(R.string.confirm_man) + data.get(position).getVendor_name()+"?");
                    }
                    else if (sessionManager.getlanguage().equalsIgnoreCase("hi")){
                        builder.setMessage(""+getResources().getString(R.string.confirm_man));
                    }
                    builder.setPositiveButton(""+getResources().getString(R.string.chat_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            globalVariables.setVendor_id(data.get(position).getVendor_id());
                            globalVariables.setUser_address(user_address);
                            Intent intent=new Intent(TopVendorList.this,ProductList_Description.class);
                            intent.putExtra("Sub_category_id", subCatg_id);
                            intent.putExtra("category_id", category_id);
                            intent.putExtra("sub_category_name", sub_category_name);
                            intent.putExtra("category_name", category_name);
                            startActivity(intent);

                      //      Toast.makeText(TopVendorList.this, "Item added to shopping cart successfully.Please check your shopping cart", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNeutralButton(""+getResources().getString(R.string.chat_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView list_venDecrp,list_venName,list_venExpr,list_venRating,select_vendor;
            public ImageView list_venImage;


            public MyViewHolder(View view) {
                super(view);
                select_vendor=view.findViewById(R.id.select_vendor);
                list_venImage=view.findViewById(R.id.list_venImage);
                list_venName=view.findViewById(R.id.list_venName);
                list_venDecrp=view.findViewById(R.id.list_venDecrp);
                list_venExpr=view.findViewById(R.id.list_venExpr);
                list_venRating=view.findViewById(R.id.list_venRating);
            }
        }
    }

    private void loader() {
         androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (TopVendorList.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
    }

}
