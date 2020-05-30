package com.hrproject.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hrproject.GetterSetter.Offers_get_set;
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


public class User_Inbox extends Fragment {
     RecyclerView recycler_list;
     LinearLayout notify_layout;
     AlertDialog p;
     String type;
    UserSessionManager session;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_user__inbox, container, false);

        recycler_list=v.findViewById(R.id.user_notification_list);
        notify_layout=v.findViewById(R.id.user_notify_layout);

        session=new UserSessionManager(getActivity());
        HashMap<String,String > data=session.getUserDetails();
        type=data.get(session.KEY_TYPE);

        layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recycler_list.setLayoutManager(layoutManager);

        checknet();


        return v;
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        //    System.out.println("status:::"+isInternetPresent);
        // check for Internet status
        if (isInternetPresent) {

            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
            NOTIFY_LIST();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
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


    private void NOTIFY_LIST() {
        System.out.println("type:"+type);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Offers_get_set>> call=apiInterface.send_notification(type);

        call.enqueue(new Callback<ArrayList<Offers_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Offers_get_set>> call, Response<ArrayList<Offers_get_set>> response) {
                Log.d("URL:",response.toString());

                p.dismiss();
                System.out.println("size:"+response.body().size());
                if (response.body().size()>0){
                    if (response.body().get(0).getError().equalsIgnoreCase("0"))
                    {
                        notify_layout.setVisibility(View.GONE);
                        ArrayList<Offers_get_set> datalist=new ArrayList<>(response.body());
                        User_Notification_List_Adapter adapter_list=new User_Notification_List_Adapter(getActivity(),datalist);
                        recycler_list.setAdapter(adapter_list);

                    }

                }
                else {
                    notify_layout.setVisibility(View.VISIBLE);
                    recycler_list.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Offers_get_set>> call, Throwable t) {
                p.dismiss();
            }
        });


    }


    private class User_Notification_List_Adapter extends RecyclerView.Adapter<User_Notification_List_Adapter.MyViewHolder> {
        ArrayList<Offers_get_set> data;
        Context context;

        public User_Notification_List_Adapter(FragmentActivity activity, ArrayList<Offers_get_set> datalist) {
            this.context=activity;
            this.data=datalist;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_user_notification__list__adapter,parent,false);

            return new User_Notification_List_Adapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull User_Notification_List_Adapter.MyViewHolder holder, int position) {

            String text="<b>" + data.get(position).getTitle()+": "+"</b>" + data.get(position).getDescription();
            holder.notify_descrp.setText(Html.fromHtml(text));

            holder.notify_date.setText(data.get(position).getDays()+" "+getResources().getString(R.string.days_ago));
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
            public TextView notify_descrp,notify_date;


            public MyViewHolder(View view) {
                super(view);
                notify_descrp=view.findViewById(R.id.user_notify_descrp);
                notify_date=view.findViewById(R.id.notify_user_date);

            }
        }
    }
}
