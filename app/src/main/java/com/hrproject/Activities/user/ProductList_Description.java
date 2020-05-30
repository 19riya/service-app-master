package com.hrproject.Activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

// import com.hrproject.;
import com.androidadvance.topsnackbar.TSnackbar;
import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
/*
import com.hrproject.FirebaseClasses.MyFirebaseMessagingService;
*/
import com.hrproject.Activities.Choose_Option;
import com.hrproject.Activities.Forgot_Password;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.GetterSetter.profile_get_set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.ExpandableHeightGridView;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.MainActivity;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.hrproject.R;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


 //import com.hrproject.TSnackbar;


public class ProductList_Description extends AppCompatActivity {
    static String sub_category_id = "";
    static String category_id = "";
    static String sub_category_name = "",vendor_id="";
    static String category_name = "";
    SeekBar range;
    ExpandableHeightGridView listView;
    static TextView details;
    String address;
    static All_offer adater;
    static String number,ven_image,default_add="",user_id,order,from_dob_value,to_dob_value="0";
    TextView road_no;
    static TextView full_address;
    LinearLayout offer_layout;
    static TextView service_name,order_no1,vendor_name,vendor_mobile,user_otp;
    static ImageView cancel_popup;
    LinearLayout address_layout;
    static CoordinatorLayout layout;
    static String service_status="";
    androidx.appcompat.app.AlertDialog alertDialog;



    EditText from_dob,to_dob;

    TextView home_address,other_address;
    TextView enter_location_service;
    String home_add,other_add;
    LinearLayout getAddress;


    ViewFlipper vf;
    static String res;
    final int MIN = 0;
    final int MAX = 10;
    static String service,mobile,otp,unique_id;

    static String code1="0";
    public ArrayList<Offers_get_set> data = new ArrayList<>();

    DatePickerDialog mDatePicker;
    int mYear,mMonth,mDay;
    static int value;
    static Context context;
    UserSessionManager session;
    int[] images = {R.drawable.air_conditioning, R.drawable.bike_repair, R.drawable.cleaning_off, R.drawable.laptop_repair,
            R.drawable.car_repair};
    static double latitude,longitude;
    Button confirm;
    static Button call_user,chat;
    static ImageView vendor_image;
    static UserSessionManager sessionManager;

    static androidx.appcompat.app.AlertDialog p;
    TextView offer,range_value;
    String title, description;
    static String code_limit;
    static GlobalVariables globalVariables;
    String enter_location="0";
static CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list__description);

        context = this;
        session = new UserSessionManager(context);
        sessionManager=new UserSessionManager(context);
        HashMap<String, String> data = session.getUserDetails();

        globalVariables = GlobalVariables.getInstance();

        user_id = data.get(session.KEY_ID);
        System.out.println("user_id:::" + user_id);

        sub_category_id = getIntent().getExtras().getString("Sub_category_id");
        category_id = getIntent().getExtras().getString("category_id");
        sub_category_name = getIntent().getExtras().getString("sub_category_name");
        category_name = getIntent().getExtras().getString("category_name");

        System.out.println("category_id" + category_id);
        System.out.println("sub_category_id" + sub_category_id);
        System.out.println("latitutde" + (latitude));
        System.out.println("longitude" + (longitude));
        System.out.println("sub_category_name" + sub_category_name);
        System.out.println("category_name" + category_name);

        layout = findViewById(R.id.linear_layout);
        road_no = findViewById(R.id.road_no);

        range = findViewById(R.id.range_selection);
        address_layout = findViewById(R.id.address_layout);
        full_address = findViewById(R.id.user_address);
        vf = findViewById(R.id.view_flipper);
        confirm = findViewById(R.id.confirm_book);
        range_value=findViewById(R.id.range_value);
        offer_layout=findViewById(R.id.offer_layout);
        from_dob=findViewById(R.id.service_from_date);
        to_dob=findViewById(R.id.service_upto_date);

   //     svp=findViewById(R.id.smoolider);

        offer = findViewById(R.id.offer_text);
        offer.setSelected(true);

        from_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(from_dob);
            }
        });

        to_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate1(to_dob);
            }
        });

        findViewById(R.id.vendor_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  globalVariables.setUser_address(full_address.getText().toString());
                Intent intent=new Intent(context,TopVendorList.class);
                intent.putExtra("user_address",full_address.getText().toString());
                intent.putExtra("Sub_category_id", sub_category_id);
                intent.putExtra("category_id", category_id);
                intent.putExtra("sub_category_name", sub_category_name);
                intent.putExtra("category_name", category_name);
                startActivity(intent);
            }
        });

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
        String formattedDate = df.format(c);

        System.out.println("Current date => " + formattedDate);


      /*  SimpleDateFormat formatIn = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        Date instance = null;
        try {
            instance = formatIn.parse("23/May/2014");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        System.out.println("formatOut:"+formatOut.format(instance));*/


        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        System.out.println("formatOut_date:"+date);

      /*  String month_number = String.valueOf(instance.getMonth()); //DEPRECETDE USE CALENDAR

        String[] product_unit=formattedDate.split("-");

        String month_name=product_unit[1];

        new DateFormatSymbols().getMonths()[month_name-1];
*/
        from_dob.setText(date);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("WELCOME");
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                        (ProductList_Description.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                p = mBuilder.create();
                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                p.setCancelable(false);
                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                p.setCanceledOnTouchOutside(false);
                p.show();
                checknet();
            }
        });

        for (int i = 0; i < images.length; i++) {
            ImageView slider = new ImageView(this);
            slider.setImageResource(images[i]);
            vf.addView(slider);
        }

        vf.setAutoStart(true);
        vf.setFlipInterval(3000);

        ConnectionDetector cd = new ConnectionDetector(ProductList_Description.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            GPSTracker gps = new GPSTracker(context);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                System.out.println("latitude:"+latitude);
                System.out.println("longitude:"+longitude);

                // \n is for new line
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                if (addresses.size() > 0) {
                    address = addresses.get(0).getAddressLine(0);
                    // If any additional address line present than only, check with
                    // max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    System.out.println("check_address" + address);

                    Log.d("check_cityName", city);

                    Log.d("check_state", state);
                    Log.d("check_country", country);
                    Log.d("check_postalCode", postalCode);
                    Log.d("check_knownName", knownName);

                    // road_no.setText(""+A);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



            default_add = getIntent().getExtras().getString("address");

            full_address.setText("" + default_add);

      //      globalVariables.setUser_address(default_add);

            COUPONS_JSON();
        }
        else {
            SweetAlertDialog ff = new SweetAlertDialog(ProductList_Description.this, SweetAlertDialog.WARNING_TYPE);
            ff.setTitleText(getResources().getString(R.string.failed));
            ff.setContentText(getResources().getString(R.string.internet_connection));
            ff.setCanceledOnTouchOutside(false);
            ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                //    p.dismiss();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }

        address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setLocation();
               /* Intent intent = new Intent(context, Set_Service_Location.class);
                intent.putExtra("category_id", category_id);
                intent.putExtra("sub_category_name", sub_category_name);
                intent.putExtra("category_name", category_name);
                intent.putExtra("sub_category_id", sub_category_id);
                startActivity(intent);*/
            }
        });

        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = Math.round((progress * (MAX - MIN)) / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                range_value.setText(getResources().getString(R.string.range_value) +" "+value+getResources().getString(R.string.user_range) );
            }
        });
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
                 //       get_address.setVisibility(View.VISIBLE);

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


    private static void REQUEST_JSON() {

       // CountDownTimer timer;
        System.out.println("address_global" + globalVariables.getUser_address());
        System.out.println("vendor_id" + vendor_id);
        System.out.println("category_id" + category_id);
        System.out.println("sub_category_id" + sub_category_id);
        System.out.println("latitutde" + (latitude));
        System.out.println("longitude" + (longitude));
        System.out.println("progress_in_km" + (value));
        System.out.println("user_id" + user_id);
        System.out.println("code:"+code1);
        System.out.println("address" + default_add);
        System.out.println("from_dob_value" + from_dob_value);
        System.out.println("to_dob_value" + to_dob_value);

        System.out.println("ful_address" +full_address.getText().toString() );


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call = apiInterface.user_request(user_id, category_id, sub_category_id,
                full_address.getText().toString(),
                String.valueOf(longitude), String.valueOf(latitude),
                String.valueOf(value), "0", code1, from_dob_value, to_dob_value);

        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {

                Log.d("URL::", response.toString());
                p.dismiss();
                try {
                    if (response.body().get(0).getError().equalsIgnoreCase("0")) {

                        Snackbar snackbar = Snackbar.make(layout, "" + response.body().get(0).getMsg(), Snackbar.LENGTH_LONG);
                        snackbar.show();

                        order = response.body().get(0).getUnique_id();
                        System.out.println("order_no::" + order);
//                        order_no1.setText(getResources().getString(R.string.order_no)+" #000"+order);


                        timer=new CountDownTimer(60000, 1000) {

                            public void onTick(long millisUntilFinished) {

                                BackgrndTask task = new BackgrndTask();
                                task.execute();

                                System.out.println("seconds remaining: "+millisUntilFinished / 1000);
                                //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                                // here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                SweetAlertDialog ff = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                                ff.setTitleText(context.getResources().getString(R.string.alert));
                                ff.setContentText("Your request is cancelled");
                                ff.setConfirmButton(context.getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        p.dismiss();
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                                ff.show();
                                timer.cancel();


                                //   mTextField.setText("done!");
                            }

                        }.start();


                    } else {
                        Snackbar snackbar = Snackbar.make(layout, "" + response.body().get(0).getMsg(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (Exception ex) {
                    Log.d("ERROR::::", ex.getMessage());
                   p.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.d("FAILURE ERROR:::", t.getMessage());
            }
        });
    }// end place request

    private static void SHOW_REQUEST(String order1) {
        Log.i("unique_id", order1);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Address_get_set>> call = apiInterface.accepted_vendor_information(order1);

        call.enqueue(new Callback<ArrayList<Address_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {

                Log.d("URL::", response.toString());

                try {
                    System.out.println("check:"+"try");
                    System.out.println("response:"+response.body().get(0).getError().equalsIgnoreCase("0"));
                    if (response.body().get(0).getError().equalsIgnoreCase("0")) {

                        System.out.println("normal_vendor_id:"+response.body().get(0).getVendor_id());

                        globalVariables.setAccepted_vendor_id(response.body().get(0).getVendor_id());
                        System.out.println("global_vendor_id::" + globalVariables.getAccepted_vendor_id());


                        System.out.println("welcome->");
                        System.out.println("sub_category_name::" + sub_category_name);
                        unique_id = order1;



                        System.out.println("order_no:::" + order1);

                        globalVariables.setService_otp(response.body().get(0).getService_otp());

                 //       otp = "<b>" + context.getResources().getString(R.string.generated_otp) + "</b>" + response.body().get(0).getService_otp();
                        number = "<b>" + context.getResources().getString(R.string.popup_ven_mobile) + ": " + "</b>" + "\n +91 " + response.body().get(0).getVendor_mobile();

                        mobile = response.body().get(0).getVendor_mobile();
                        ven_image = response.body().get(0).getVendor_image();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View mView = inflater.inflate(R.layout.bottom_sheet_dialog, null);
                        mBuilder.setView(mView);

                        service_name = mView.findViewById(R.id.service_name);
                        order_no1 = mView.findViewById(R.id.vendor_order_no1);
                        cancel_popup=mView.findViewById(R.id.cancel_popup);
                        vendor_name = mView.findViewById(R.id.vendor_name);
                        vendor_image = mView.findViewById(R.id.vendor_image_user);
                        vendor_mobile = mView.findViewById(R.id.vendor_mobile);
                        call_user = mView.findViewById(R.id.call_vendor);
                        chat = mView.findViewById(R.id.chat_vendor);
                  //      user_otp = mView.findViewById(R.id.user_otp);

                        AlertDialog dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);

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

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

                        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        dialog.show();

                        cancel_popup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        service = "<b>" + context.getResources().getString(R.string.popup_services) + ": " + "</b> \n" + category_name + "|" + sub_category_name;
                        service_name.setText(Html.fromHtml(service));

                        System.out.println("unique_id:" + unique_id);

                        globalVariables.setUnique_id(unique_id);
                        order_no1.setText(context.getResources().getString(R.string.order_no) + " #000" + unique_id);

                     //   user_otp.setText(Html.fromHtml(otp));
                        System.out.println("otp:" + otp);

                        vendor_mobile.setText(Html.fromHtml(number));
                        System.out.println("number:" + number);

                        System.out.println("mobile_no:" + mobile);

                        vendor_name.setText(response.body().get(0).getVendor_name());

                        System.out.println("list_description employee_name:"+response.body().get(0).getVendor_name());
                        sessionManager.setChatId(order1,service_status,response.body().get(0).getVendor_name());

                        Picasso.with(context).load(ven_image)
                                .placeholder(R.drawable.man).into(vendor_image);

                        call_user.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobile, null));
                                context.startActivity(intent);
                            }
                        });

                        chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                globalVariables.setUnique_id(unique_id);
                                Intent intent=new Intent(context,Messaging_User_Activity.class);
                                intent.putExtra("notifyStatus","0");
                          //      intent.putExtra("unique_id",unique_id);
                                context.startActivity(intent);

//                                intent.putExtra("vendor_id",response.body().get(0).getVendor_id());

                            /*    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                smsIntent.setType("vnd.android-dir/mms-sms");
                                smsIntent.setData(Uri.parse("sms:" + number));
                                context.startActivity(smsIntent);             */



                            }
                        });
                    } else {
                        Snackbar snackbar = Snackbar.make(layout, "" + response.body().get(0).getMsg(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (Exception ex) {
                    Log.d("ERROR:", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                Log.d("FAILURE ERROR:::", t.getMessage());
            }
        });
    }

    private static class BackgrndTask extends AsyncTask<String, String, String> {

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


                            System.out.println("handler:"+response.body().get(0).getStatus());
                            if (response.body().get(0).getStatus().equalsIgnoreCase("0")){
                                //      handler.postDelayed(this, 1000);

                            }
                            else if (response.body().get(0).getStatus().equalsIgnoreCase("2")) {
                                System.out.println("Status:"+"REJECT");
                                //    handler.postDelayed(this, 1000);
                            }
                            else if (response.body().get(0).getStatus().equalsIgnoreCase("3")){
                                System.out.println("Status:"+"RUNNING");
                                //    handler.postDelayed(this, 1000);
                            }
                            else if (response.body().get(0).getStatus().equalsIgnoreCase("4")){
                                System.out.println("Status:"+"COMPLETED");
                                //    handler.postDelayed(this, 1000);
                            }
                            else if (response.body().get(0).getStatus().equalsIgnoreCase("5")){
                                System.out.println("Status:"+"CANCELLED");
                                //    handler.postDelayed(this, 1000);
                            }
                            else if (response.body().get(0).getStatus().equalsIgnoreCase("1")){
                                System.out.println("Status:"+"ACCEPTED");
                                service_status="1";
                                SHOW_REQUEST(order);
                                timer.cancel();
                            }
/*                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                          //  timer.cancel();

                                }
                            },1000);
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("handler:"+response.body().get(0).getStatus());
                                    if (response.body().get(0).getStatus().equalsIgnoreCase("0")){
                                        //      handler.postDelayed(this, 1000);
                                        BackgrndTask task=new BackgrndTask();
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
                                        SHOW_REQUEST(order);
                                    }
                                }
                                },1000);
                                        Intent i= new Intent(MainActivity.this, Choose_Option.class);
                                        startActivity(i);
                                        finishAffinity();
                                    }
                                    else
                                    {
                                        SweetAlertDialog ff = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                                        ff.setTitleText(context.getResources().getString(R.string.alert));
                                        ff.setContentText("YOUR REQUEST IS CANCELLED");
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
                            };
                            th.start();*/
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

    }// end background task

    private void COUPONS_JSON() {

        System.out.println("sub_category_id:" + sub_category_id);
        System.out.println("sub_category_id:"+ sub_category_id);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call = apiInterface.coupons(sub_category_id);

        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {
                Log.d("URL:", response.toString());

                if (response.body().get(0).getError().equalsIgnoreCase("0")) {
                    Log.d("welcome:", "world");

                    //     p.dismiss();
                    data = response.body().get(0).getResult();
                    System.out.println("data:" + data.size());

                    if (data.size() > 0) {
                        offer_layout.setVisibility(View.VISIBLE);
                        title = data.get(0).getTitle();
                        System.out.println("title:" + title);
                        description = data.get(0).getDescription();
                        System.out.println("description:" + description);
                    }

                    System.out.println("title_text:" + title);

                    String text="<b>" + title + ": " + "</b>" + description;
                    offer.setText(Html.fromHtml(text));
                    System.out.println("offer:" + offer);

                    offer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            globalVariables.setOffers_get_sets(data);
                          /*  BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ProductList_Description.this);
                            View sheetView = getLayoutInflater().inflate(R.layout.particular_categ_offer, null);
                            mBottomSheetDialog.setContentView(sheetView);

                            GlobalVariables globalVariables = GlobalVariables.getInstance();
                            System.out.println("dataOffer->" + globalVariables.getOffers_get_sets());

                            listView = sheetView.findViewById(R.id.offer_list);
                            listView.setExpanded(true);
                            adater = new All_offer(ProductList_Description.this, globalVariables.getOffers_get_sets());
                            listView.setAdapter(adater);

                            mBottomSheetDialog.show();*/

                            EditProfile_Fragment fragment = new EditProfile_Fragment();
                            fragment.show(getSupportFragmentManager(), "dd");
                        }
                    });
                } else {
                    System.out.println("resonse:" + response.body().get(0).getMsg());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                Log.i("FAILURE_ERROR:", t.getMessage());

            }
        });
    }

    @SuppressLint("ValidFragment")
    public static class EditProfile_Fragment extends BottomSheetDialogFragment {
        ExpandableHeightGridView listView;
        CoordinatorLayout bottom_layout;
        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View inflatedView = View.inflate(getContext(), R.layout.particular_categ_offer, null);
            dialog.setContentView(inflatedView);

            BottomSheetDialog d = (BottomSheetDialog) getDialog();
            View sheet=d.findViewById(R.id.design_bottom_sheet);
            sheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

            GlobalVariables globalVariables = GlobalVariables.getInstance();
            System.out.println("dataOffer->" + globalVariables.getOffers_get_sets());


            listView = inflatedView.findViewById(R.id.offer_list);
            bottom_layout = inflatedView.findViewById(R.id.bottom_layout);

            listView.setExpanded(true);
            adater = new All_offer(getActivity(), globalVariables.getOffers_get_sets(),bottom_layout);
            listView.setAdapter(adater);

        }

    } // end bottomsheet dialog

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(ProductList_Description.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            if (!validate()) {
                p.dismiss();
                onLoginFailed();
                return;
            } else {

                if (globalVariables.getVendor_id()==null){
                    vendor_id="";
                }
                else {
                    vendor_id=globalVariables.getVendor_id();
                }

                REQUEST_JSON();
            }
        } else {
            SweetAlertDialog ff = new SweetAlertDialog(ProductList_Description.this, SweetAlertDialog.WARNING_TYPE);
            ff.setTitleText(getResources().getString(R.string.failed));
            ff.setContentText(getResources().getString(R.string.internet_connection));
            ff.setCanceledOnTouchOutside(false);
            ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    p.dismiss();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }
    }

    private boolean validate() {
        boolean valid = true;
        from_dob_value=from_dob.getText().toString();
        to_dob_value=to_dob.getText().toString();



        if (globalVariables.getUser_address()==null){
            System.out.println("default_address:"+default_add);
            if (default_add==null)
            {
                Snackbar.make(findViewById(R.id.linear_layout),"Please enter your default address",Snackbar.LENGTH_LONG).show();
                valid=false;
            }
            else {
                full_address.setText(default_add);
            }

        }
        else {
            full_address.setText(globalVariables.getUser_address());
        }



        if (value == 0) {

            displaySnackbar(getResources().getString(R.string.select_range),layout);
            valid=false;
        }

        else if (from_dob_value.isEmpty()){
            displaySnackbar(getResources().getString(R.string.service_date1),layout);
            valid=false;
        }
       /* else if (to_dob_value.isEmpty()){
            displaySnackbar(getResources().getString(R.string.service_date2),layout);
            valid=false;
        }*/

        return valid;
    }// end validation

    private void onLoginFailed() {
    }

    private static class All_offer extends BaseAdapter {
        ArrayList<Offers_get_set> datalist;
        Context context;
        CoordinatorLayout bottom_layout;

        public All_offer(FragmentActivity particular_categ_offer, ArrayList<Offers_get_set> data, CoordinatorLayout bottom_layout) {
            this.datalist = data;
            this.context = particular_categ_offer;
            this.bottom_layout=bottom_layout;
        }

        public void UpdateData(ArrayList<Offers_get_set> data){
            this.datalist = data;
        }

        @Override
        public int getCount() {
            return datalist.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView code, max_use, max_discount, validity, fees,coupon_description,apply_code;
            LinearLayout lay_details;
            TextView remove_code;
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(R.layout.all_offer, null);

            lay_details = v.findViewById(R.id.details_layout);
            apply_code = v.findViewById(R.id.apply_code);
            remove_code=v.findViewById(R.id.remove_code);
            code = v.findViewById(R.id.code);
            max_discount = v.findViewById(R.id.short_descrp);
            max_use = v.findViewById(R.id.offer_maxUse);
            validity = v.findViewById(R.id.offer_validity);
            details = v.findViewById(R.id.offer_details);
            fees = v.findViewById(R.id.offer_fees);
            coupon_description = v.findViewById(R.id.offer_descrp);

            remove_code.setVisibility(View.GONE);
            String offer_id = datalist.get(i).getOffer_id();
            code.setText(datalist.get(i).getCode());

            validity.setText(context.getResources().getString(R.string.validity_date) + " " + datalist.get(i).getDate());
            System.out.println("validity:" + datalist.get(i).getDate());

            code_limit=datalist.get(i).getMax_use();
            System.out.println("code_limit"+code_limit);

            max_discount.setText(context.getResources().getString(R.string.offer_descp1) + " " + datalist.get(i).getMax_discount() +
                    " " + context.getResources().getString(R.string.offer_descrp2) + " " + datalist.get(i).getCode());

            details.setText(context.getResources().getString(R.string.details));

            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("details","click");

                    details.setVisibility(View.GONE);
                    lay_details.setVisibility(View.VISIBLE);
                    fees.setText("$ " + datalist.get(i).getFees());
                    coupon_description.setText(datalist.get(i).getDescription());
                    max_use.setText(context.getResources().getString(R.string.offer_max_use1) + " " + datalist.get(i).getMax_use()
                            + " " + context.getResources().getString(R.string.offer_max_use2));
                }
            });

            apply_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // checked = true;

                    Log.i("apply_code","click");
                    CHECK_USAGE(bottom_layout,context, offer_id,apply_code,datalist,i);
                }
            });

            System.out.println("status:"+!(code1.equalsIgnoreCase("0")));
            if (!(code1.equalsIgnoreCase("0"))){
                apply_code.setVisibility(View.GONE);
                remove_code.setVisibility(View.VISIBLE);
            }

            remove_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("remove_code:"+"click");
                    code1="0";
                    System.out.println("code:"+code1);
                    apply_code.setVisibility(View.VISIBLE);
                    remove_code.setVisibility(View.GONE);
                }
            });

            return v;
        }
    } //end offer_list adapter

    private static void CHECK_USAGE(CoordinatorLayout bottom_layout, Context context, String offer_id,
                                    TextView apply_code, ArrayList<Offers_get_set> datalist, int i) {
        System.out.println("offer_id:" + offer_id);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Offers_get_set>> call = apiInterface.max_use_offer(offer_id,user_id);

        call.enqueue(new Callback<ArrayList<Offers_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Offers_get_set>> call, Response<ArrayList<Offers_get_set>> response) {
                Log.d("URL:", response.toString());

                if (response.body().get(0).getError().equalsIgnoreCase("0")) {
                    code1 = response.body().get(0).getCode();
                    System.out.println("code:"+code1);

               /*     apply_code.setVisibility(View.GONE);
                    remove_code.setVisibility(View.VISIBLE);*/


                    datalist.get(i).setMsg(response.body().get(0).getMsg());
                    String msg=response.body().get(0).getMsg();
                    displaySnackbar(msg,bottom_layout);

                    adater.UpdateData(datalist);
                    adater.notifyDataSetChanged();
                }
                else {
                    Log.i("response:","exceed_limit");
                    code1="0";
                    datalist.get(i).setMsg(response.body().get(0).getMsg());
                    adater.UpdateData(datalist);
                    adater.notifyDataSetChanged();
                    System.out.println("code_limit"+code_limit);

                    String test=context.getResources().getString(R.string.limit_code)+" "+code_limit + " " +
                            context.getResources().getString(R.string.offer_max_use2);

                    displaySnackbar(test,bottom_layout);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Offers_get_set>> call, Throwable t) {
                Log.i("FAILURE:",t.getMessage());
            }
        });
    }// end max. use

    private static void displaySnackbar(String msg, CoordinatorLayout bottom_layout) {
        int duration = 3000;
        final TSnackbar snackbar = TSnackbar.make(bottom_layout, msg, TSnackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.WHITE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    snackbar.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, duration);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        View snackbarView = snackbar.getView();
        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
        params.height = 150;
        snackbarView.setLayoutParams(params);
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setMaxLines(1);
        snackbar.show();
    }

    public void pickDate(final TextView dialog)
    {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mDatePicker = new DatePickerDialog(ProductList_Description.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                selectedmonth = selectedmonth + 1;
                dialog.setText( selectedyear + "-" + selectedmonth + "-" + selectedday);

                mDay= selectedday;
                mMonth= selectedmonth;
                mYear= selectedyear;

            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        //      mDatePicker.setTitle(getResources().getString(R.string.title_birth));
        mDatePicker.show();

    }


    public void pickDate1(final TextView dialog)
    {
        String getfromdate = from_dob.getText().toString().trim();
        String getfrom[] = getfromdate.split("-");

        mDay= Integer.parseInt(getfrom[2]);
        mMonth = Integer.parseInt(getfrom[1]);
        mYear = Integer.parseInt(getfrom[0]);
        System.out.println("mMonth:"+mMonth);
        System.out.println("getfromdate:"+getfromdate);

        mDatePicker = new DatePickerDialog(ProductList_Description.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                selectedmonth = selectedmonth  ;
                dialog.setText( selectedyear + "-" + selectedmonth + "-" + selectedday);

                mMonth=selectedmonth;
            }
        }, mYear, mMonth, mDay);

        Calendar c1= Calendar.getInstance();


        System.out.println("mMonth:"+mMonth);
        c1.set(mYear,mMonth,mDay);

        mDatePicker.getDatePicker().setMinDate(c1.getTimeInMillis());
        System.out.println("mMonth:"+ c1.getTimeInMillis());
        //   mDatePicker.setTitle(getResources().getString(R.string.title_birth));
        mDatePicker.show();
    }

    private void setLocation() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProductList_Description.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_set__service__location, null);

        builder.setCancelable(false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        //  alertDialog=new androidx.appcompat.app.AlertDialog(new ContextThemeWrapper(this,R.style.DialogSlideAnim));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnim;
        alertDialog.getWindow().setGravity(Gravity.TOP);
        alertDialog.show();


        LinearLayout home_layout, other_layout, get_address, fetch_location, layout;
        //    TextView home_address,other_address;
        //   String user_id,home_add,other_add,category_is,sub_category_id,sub_category_name,category_name;
        ImageView back_button;
        androidx.appcompat.app.AlertDialog p;

        back_button = dialogView.findViewById(R.id.back_button);
        home_address = dialogView.findViewById(R.id.home_address);
        enter_location_service=dialogView.findViewById(R.id.enter_location_service);
        fetch_location = dialogView.findViewById(R.id.fetch_current_location);
        other_address = dialogView.findViewById(R.id.other_address);
        home_layout = dialogView.findViewById(R.id.home_address_layout);
        other_layout = dialogView.findViewById(R.id.other_address_layout);
        get_address = dialogView.findViewById(R.id.get_address);
        layout = dialogView.findViewById(R.id.layout);


      /*  enter_location_service.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enter_location=enter_location_service.getText().toString();
            }
        });*/


        VectorMasterView heartVector = dialogView.findViewById(R.id.current_location_icon);
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


        VectorMasterView heartVector1 = dialogView.findViewById(R.id.home_location_icon);
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


        VectorMasterView heartVector2 = dialogView.findViewById(R.id.other_location_icon);
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

        checknet1();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //      finish();
            }
        });

        home_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full_address.setText("" + home_add);

                //          globalVariables.setUser_address(home_add);
                default_add=home_add;


                getLocationFromAddress(home_add);


                alertDialog.dismiss();
                /*Intent intent=new Intent(ProductList_Description.this,ProductList_Description.class);
                intent.putExtra("address",home_add);
                intent.putExtra("category_id",category_is);
                intent.putExtra("sub_category_name",sub_category_name);
                intent.putExtra("category_name",category_name);
                intent.putExtra("Sub_category_id",sub_category_id);
                startActivity(intent);
                finish();*/
            }
        });

        other_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full_address.setText("" + other_add);
                default_add=other_add;

                //        globalVariables.setUser_address(other_add);

                getLocationFromAddress(other_add);
                alertDialog.dismiss();
            }
        });

        fetch_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductList_Description.this, VMapsActivity.class);
                intent.putExtra("Sub_category_id", sub_category_id);
                intent.putExtra("category_id", category_id);
                intent.putExtra("sub_category_name", sub_category_name);
                intent.putExtra("category_name", category_name);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }


    public void getLocationFromAddress(String strAddress) {
        System.out.println("welcome");
        Geocoder coder = new Geocoder(this);
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

    private void checknet1() {
        ConnectionDetector cd = new ConnectionDetector(ProductList_Description.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                    (ProductList_Description.this);
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
            SweetAlertDialog ff=new SweetAlertDialog(ProductList_Description.this,SweetAlertDialog.WARNING_TYPE);
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



 /*   public static void check_status()
    {
        Thread th= new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(3000);
                }
                catch(Exception e) { }

                if (!new UserSessionManager(MainActivity.this).isLoggedIn())
                {
                    Intent i= new Intent(MainActivity.this, Choose_Option.class);
                    startActivity(i);
                    finishAffinity();
                }
                else
                {   }
            }
        };
        th.start();

    }*/
}