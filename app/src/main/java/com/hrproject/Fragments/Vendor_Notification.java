package com.hrproject.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hrproject.Activities.Vendor.Accept_Rquest_Order2;
import com.hrproject.Activities.Vendor.Messaging_Vendor_Activity;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class Vendor_Notification extends Fragment
{
    TextView order_no1,service,phone,name,address1,start_date,end_date;
    String request_address;
    static String number;
    String user_name;
    String service_detail;
    String vendor_id;
    String user_image;
    String str_start_date;
    String str_end_date;
    static String order;

    ImageView back;

    LinearLayout notification;
    LinearLayout request_received;

    UserSessionManager session;
    CircleImageView image;

    Context context;
    AlertDialog dialog;
    androidx.appcompat.app.AlertDialog p;

    GlobalVariables globalVariables;

    String status="";
    public static String user_id;
    LinearLayout notify_layout;

    RecyclerView notification_list;
    String type;
    LinearLayoutManager layoutManager;
    double latitude,longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_vendor__notification, container, false);

        session=new UserSessionManager(getActivity());
        HashMap<String,String>  data=session.getUserDetails();
        vendor_id=data.get(session.KEY_ID);
        type=data.get(session.KEY_TYPE);
        context=getActivity();

        System.out.println("welcome.."+getArguments().getString("notifyStatus"));
        String test=getArguments().getString("notifyStatus");

        notify_layout=v.findViewById(R.id.notify_layout);
        notification_list=v.findViewById(R.id.notification_list);

        globalVariables=GlobalVariables.getInstance();

        Activity activity = getActivity();
        Log.i("activity..", String.valueOf(activity));

        checknet();
        System.out.println("check");

        layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        notification_list.setLayoutManager(layoutManager);

        if (test.equalsIgnoreCase("notificationcome")){
            System.out.println("Welcome to activity..");

            if(activity != null){
                System.out.println("Hello..");

                REQUEST_RECEIVED();
            }

            System.out.println("vendor_id:::"+vendor_id);

      }
      else {
            System.out.println("welcome:::"+"else");
      }

        return v;
    }

    public void REQUEST_RECEIVED(){
        System.out.println("welcome to api");
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                (getActivity());
        View mView = getLayoutInflater().inflate(R.layout.loader, null);
        mBuilder.setView(mView);
        p = mBuilder.create();
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCancelable(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        p.setCanceledOnTouchOutside(false);
        p.show();
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.send_service_request();

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {

                Log.d("URL::",response.toString());
             p.dismiss();
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                        System.out.println("requested_address::"+response.body().get(0).getRequested_address());


                     //   str_start_date=response.body().get(0).getFrom_service_date();
                     //   str_end_date=response.body().get(0).getTo_service_date();
                        request_address=response.body().get(0).getRequested_address();
                        order=response.body().get(0).getUnique_id();



                        number=response.body().get(0).getUser_mobile();
                        user_name=response.body().get(0).getUser_name();
                        System.out.println("number::::"+number);
                        System.out.println("requetsed name::"+user_name);
                        user_image=response.body().get(0).getUser_image();
                        service_detail =response.body().get(0).getCategory_name() + "|"
                                + response.body().get(0).getSubcategory()+" ";


                        System.out.println("category:"+response.body().get(0).getCategory_name());
                        System.out.println("sub_category:"+response.body().get(0).getSubcategory());
                      //  service_detail=response.body().get(0).getCategory_name();

                        getLocationFromAddress(request_address);

                        BackgroundTask task=new BackgroundTask();
                        task.execute();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        View mView = getLayoutInflater().inflate(R.layout.activity_order__details, null);

                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        dialog.show();
                        Log.i("received","request accepted");

                        request_received=mView.findViewById(R.id.request_received);
                        image=mView.findViewById(R.id.user_image_vendor);
                        back=mView.findViewById(R.id.back_button);
                        order_no1=mView.findViewById(R.id.order_no1);
                        notification=mView.findViewById(R.id.notification_generate);
                        address1=mView.findViewById(R.id.address_to_vendor1);
                        name=mView.findViewById(R.id.user_name);
                        phone=mView.findViewById(R.id.user_mobile);
                        service=mView.findViewById(R.id.service_info);
                        start_date=mView.findViewById(R.id.service_ven_from_date);
                        end_date=mView.findViewById(R.id.service_ven_to_date);

                        System.out.println("str_to_date"+response.body().get(0).getTo_service_date());

                        str_start_date = context.getResources().getString(R.string.service_from_date) +" "+ response.body().get(0).getFrom_service_date();
                        if (response.body().get(0).getTo_service_date().equals("")){

                           end_date.setVisibility(View.GONE);
                        }
                        else {
                            end_date.setVisibility(View.VISIBLE);
                            str_end_date=context.getResources().getString(R.string.service_to_date) +" "+ response.body().get(0).getTo_service_date();
                            end_date.setText(""+str_end_date);
                        }


                        System.out.println("str_start_date"+str_start_date);
                        System.out.println("str_end_date"+str_end_date);
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        mView.findViewById(R.id.accept_request).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("accept","request accepted");
                                dialog.dismiss();
                                notification.setVisibility(View.GONE);

                                checknet_request();
                            }
                        });

                        mView.findViewById(R.id.reject_request).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        start_date.setText(""+str_start_date);



                        service.setText(service_detail);

                   //     service.setText(""+service_detail);
                        address1.setText(request_address);
                        order_no1.setText(" #000"+order);
                        name.setText(user_name);
                        phone.setText("+91-xxxxxxxxxx");
                      //  phone.setText("+91 "+number);
                     //   service.setText(""+service_detail);
                        Picasso.with(getContext()).load(user_image).placeholder(R.drawable.cleaning_off).into(image);
                    }
                    else{
                        Log.i("error>>",response.body().get(0).getMsg());
                    }
                        }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.d("FAILURE ERROR::",t.getMessage());

                p.dismiss();
            }
        });
    }

    @SuppressLint("ValidFragment")
    public static class AcceptFragment extends BottomSheetDialogFragment {
        Button call_user,message;
        ImageView cancel_popup;
        TextView order_no;
      //  String number;
        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View inflatedView = View.inflate(getContext(), R.layout.activity_order2_detail, null);
            dialog.setContentView(inflatedView);
            setCancelable(false);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                       /* getActivity().finish();
                        dialog.dismiss();*/
                    }
                    return true;
                }
            });


            cancel_popup=inflatedView.findViewById(R.id.cancel_popup);
            call_user=inflatedView.findViewById(R.id.call_user);
            message=inflatedView.findViewById(R.id.chat_user);
            order_no=inflatedView.findViewById(R.id.order_no2);

            order_no.setText(getResources().getString(R.string.order_no)+"#000"+order);

            cancel_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            call_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                    startActivity(intent);
                }
            });

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(),Messaging_Vendor_Activity.class);
                    intent.putExtra("notifyStatus","0");
                    intent.putExtra("unique_id",order);
                    startActivity(intent);

                }
            });


        }

    } // end bottomsheet dialog

    private class BackgroundTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            System.out.println("order_id:"+order);
            ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Address_get_set>> call=apiInterface.check_request_status(order);
            call.enqueue(new Callback<ArrayList<Address_get_set>>() {
                @Override
                public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                    Log.d("URL::",response.toString());

                       if (response.body().get(0).getError().equalsIgnoreCase("0")){
                            System.out.println("SUCCESS:::"+response.body().get(0).getMsg());

                            System.out.println("statusCheck:"+response.body().get(0).getStatus());
                        //    System.out.println("user_id:"+user_id);

                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("status:"+response.body().get(0).getStatus());
                                    if (response.body().get(0).getStatus().equalsIgnoreCase("1")){
                                   //    ACCEPT_REQUEST();

                                        status="1";
                                        System.out.println("vendor_notification employee_name:"+user_name);
                                        globalVariables.setEmployee_name(user_name);
                                        session.setChatId(order,status,user_name);

                                        AcceptFragment completeProcess=new AcceptFragment();
                                        completeProcess.show(getChildFragmentManager(),"dd");

                                       /* Intent intent=new Intent(context, Accept_Rquest_Order2.class);
                                        intent.putExtra("order",order);
                                        intent.putExtra("phone",number);
                                        context.startActivity(intent);*/
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("0")){
                                        System.out.println("status:"+"re-run");
                                        //  handler.postDelayed(this,1000);
                                        BackgroundTask task=new BackgroundTask();
                                        task.execute();
                                    }
                                    else {
                                        SweetAlertDialog ff = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                                        ff.setTitleText(context.getResources().getString(R.string.alert));
                                        ff.setContentText("Service request is cancelled");
                                        ff.setCanceledOnTouchOutside(false);
                                        ff.setConfirmButton(context.getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                p.dismiss();
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        });
                                        ff.show();
                                    }
                                }
                            },1000);
                        }

                       status=response.toString();
                }

                @Override
                public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                }
            });
            return status;
        }
    }

    private void ACCEPT_REQUEST()
    {
        System.out.println("order:"+order);
        System.out.println("number:"+number);
        System.out.println("vendor_id:"+vendor_id);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call=apiInterface.accept_user_request(order,vendor_id);

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                Log.d("URL::",response.toString());
                p.dismiss();

                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    Log.e("welcome","welcome");

                    user_id=response.body().get(0).getUser_id();
                    System.out.println("normal_user_id:"+user_id);

                    globalVariables.setAccepted_user_id(user_id);
                    globalVariables.setUnique_id(order);
                    System.out.println("global_user_id:"+ globalVariables.getUnique_id());
                    System.out.println("global_user_id:"+globalVariables.getAccepted_user_id());

    /*                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.activity_order2_detail, null);
                    mBuilder.setView(mView);
                    androidx.appcompat.app.AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCanceledOnTouchOutside(false);

                    call_user=mView.findViewById(R.id.call_user);
                    message=mView.findViewById(R.id.chat_user);
                    order_no=mView.findViewById(R.id.order_no2);

                    order_no.setText(getResources().getString(R.string.order_no)+"#000"+order);
                    call_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                            startActivity(intent);
                        }
                    });

                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.setData(Uri.parse("sms:" + number));
                            startActivity(smsIntent);
                        }
                    });
                    Log.i("welcome", "welcome");
    */            }
            }
            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.i("FAILURE ERROR:",t.getMessage());
                p.dismiss();
            }
        });
    }// end acccept request

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        //    System.out.println("status:::"+isInternetPresent);
        // check for Internet status
        if (isInternetPresent) {

            System.out.println("check connection");
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
        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Offers_get_set>> call=apiInterface.send_notification(type);

        call.enqueue(new Callback<ArrayList<Offers_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Offers_get_set>> call, Response<ArrayList<Offers_get_set>> response) {
                Log.d("URL:",response.toString());

                //      p.dismiss();
                if (response.body().get(0).getError().equalsIgnoreCase("0"))
                {
                    System.out.println("hello");
                    notify_layout.setVisibility(View.GONE);
                    ArrayList<Offers_get_set> datalist=new ArrayList<>(response.body());
                    Notification_List_Adapter adapter_list=new Notification_List_Adapter(getActivity(),datalist);
                    notification_list.setAdapter(adapter_list);

                }

                else {
                    notify_layout.setVisibility(View.VISIBLE);
                    notification_list.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Offers_get_set>> call, Throwable t) {
                //p.dismiss();
                Log.i("notify_list_falure:",t.getMessage());
            }
        });


    }// end response

    public void getLocationFromAddress(String strAddress) {
        System.out.println("welcome");
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        //  GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            System.out.println("address:"+address);
            if (address == null) {
                //   return null;
            }
            Address location = address.get(0);
            latitude=location.getLatitude();
            longitude=location.getLongitude();

            globalVariables.setLatitude(String.valueOf(latitude));
            globalVariables.setLongitude(String.valueOf(longitude));

            System.out.println("latitude:"+latitude);
            System.out.println("longitude:"+longitude);


         /*   p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));*/

            //  return p1;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Notification_List_Adapter extends RecyclerView.Adapter<Notification_List_Adapter.MyViewHolder> {
        ArrayList<Offers_get_set> data;
        Context context;
        public Notification_List_Adapter(FragmentActivity activity, ArrayList<Offers_get_set> datalist) {
            this.context=activity;
            this.data=datalist;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_notification__list__adapter,parent,false);

            return new Notification_List_Adapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Notification_List_Adapter.MyViewHolder holder, int position) {

            String text="<b>" + data.get(position).getTitle()+": "+"</b>" + data.get(position).getDescription();
            holder.notify_descrp.setText(Html.fromHtml(text));

            holder.notify_date.setText(data.get(position).getDays()+" "+getResources().getString(R.string.days_ago));

          //  holder.itemView.setBackgroundColor(Color.parseColor("#ff4500"));

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
                notify_descrp=view.findViewById(R.id.notify_descrp);
                notify_date=view.findViewById(R.id.notify_date);
            }
        }
    }


    private void checknet_request() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent){
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (getActivity());
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
            ACCEPT_REQUEST();
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

     /*   private static class NotifyStatus extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            System.out.println("unique_id:"+order);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Address_get_set>> call = apiInterface.check_request_status(order);
            call.enqueue(new Callback<ArrayList<Address_get_set>>() {
                @Override
                public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                    Log.d("URL::", response.toString());
                    res=response.toString();

                    try {
                        if (response.body().get(0).getError().equalsIgnoreCase("0")) {

                            System.out.println("SUCCESS:::" + response.body().get(0).getMsg());
                            System.out.println("response:"+response.body().get(0).getStatus());

                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("handler:"+response.body().get(0).getStatus());
                                    if (response.body().get(0).getStatus().equalsIgnoreCase("0")){
                                        //      handler.postDelayed(this, 1000);
                                        NotifyStatus task=new NotifyStatus();
                                        task.execute();
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("2")) {
                                        System.out.println("Status:"+"REJECT");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("3")){
                                        System.out.println("Status:"+"RUNNING");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("4")){
                                        System.out.println("Status:"+"COMPLETED");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("5")){
                                        System.out.println("Status:"+"CANCELLED");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("1")){
                                        System.out.println("Status:"+"ACCEPTED");
                                        //  SHOW_REQUEST(order);

                                    }
                                }
                            },1000);

                        }
                    } catch (Exception ex) {

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {

                }
            });
            return res;
        }

    }// end background task*/
    /*
    private void Show_popup() {
      AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.activity_order__details, null);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        dialog.show();
        Log.i("received","request accepted");

        request_received=mView.findViewById(R.id.request_received);
        image=mView.findViewById(R.id.user_image_vendor);
        back=mView.findViewById(R.id.back_button);
        order_no1=mView.findViewById(R.id.order_no1);
        notification=mView.findViewById(R.id.notification_generate);
        address1=mView.findViewById(R.id.address_to_vendor1);
        name=mView.findViewById(R.id.user_name);
        phone=mView.findViewById(R.id.user_mobile);
        service=mView.findViewById(R.id.service_info);

        mView.findViewById(R.id.accept_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("accept","request accepted");
                dialog.dismiss();
                notification.setVisibility(View.GONE);

                ACCEPT_REQUEST();
             //   String status="1";
                Intent intent=new Intent(context, Accept_Rquest_Order2.class);
                intent.putExtra("order",order);
                intent.putExtra("phone",number);
                context.startActivity(intent);

                BackgroundTask task=new BackgroundTask();
                task.execute();

            }
        });

        mView.findViewById(R.id.reject_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
*/
}
