package com.hrproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hrproject.Activities.Choose_Option;
import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.Activities.user.ProductList_Description;
import com.hrproject.Activities.user.User_Welcome;
import com.hrproject.Adapters.homePagerAdapter;
import com.hrproject.Fragments.Home_User;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.GetterSetter.Slider_Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.onesignal.OneSignal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String currentVersion="";
    UserSessionManager sessionManager;
    String type;
    LinearLayout layout;
    GlobalVariables globalVariables;
    AlertDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager=new UserSessionManager(MainActivity.this);

        updateResources(this,sessionManager.getlanguage());
        setContentView(R.layout.activity_main);

        layout=findViewById(R.id.layout_main);

        HashMap<String,String> data=sessionManager.getUserDetails();
        type=data.get(sessionManager.KEY_TYPE);
        System.out.println("key_type:: "+ type);
        globalVariables=GlobalVariables.getInstance();

        ConnectionDetector cd1 = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent1 = cd1.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent1) {
            Log.i("check:","present");
         //   slider();
            OFFER_JSON();

        }
        else {
            SweetAlertDialog ff = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            ff.setTitleText(getResources().getString(R.string.failed));
            ff.setContentText(getResources().getString(R.string.internet_connection));
            ff.setCanceledOnTouchOutside(false);
            ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }

   //     FirebaseApp.initializeApp(this);
        if (sessionManager.isLoggedIn()) {

        getLocation();
        }
    /*    if (sessionManager.isLoggedIn()){
            GPSTracker gps = new GPSTracker(MainActivity.this);

            // Check if GPS enabled

            if(gps.canGetLocation()) {

                if(type.equalsIgnoreCase("user"))
                {
                    Log.i("check:","main_acticity");
                    Intent i = new Intent(MainActivity.this, User_Welcome.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Staring Login Activity
                    MainActivity.this.startActivity(i);
                    finish();
                }

                else if(type.equalsIgnoreCase("vendor"))
                {
                    Intent i = new Intent(MainActivity.this, Vendor_Welcome.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("notifyStatus","0");
                    // Staring Login Activity
                    MainActivity.this.startActivity(i);
                    finish();
                }
                // \n is for new line
            }
            else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.

                Log.i("location_alert:","main_acticity");
                //   gps.showSettingsAlert();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.gps_alert));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.gps_mess));

                // On pressing the Settings button.
                alertDialog.setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        Log.i("check:","GPSTracker_ok");
                    }
                });

                // On pressing the cancel button
                alertDialog.setNegativeButton(getResources().getString(R.string.cancel_changes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Log.i("check:","GPSTracker_cancel");
                    }
                });

                // Showing Alert Message
                Log.i("check:","track");
                alertDialog.show();
            }
        }
*/

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            Boolean isInternetPresent = cd.isConnectingToInternet();
            // check for Internet status
            if (isInternetPresent) {
                goto_activity();

            } else {
                Snackbar snackbar=Snackbar.make(layout,""+getResources().getString(R.string.internet_connection),Snackbar.LENGTH_LONG);
                snackbar.show();
            }

    }// end onCreate method

   public void getLocation(){
        //    Handler handler=new Handler();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GPSTracker gps = new GPSTracker(MainActivity.this);
                    // Check if GPS enabled
                    if (gps.canGetLocation()) {
                        if (type.equalsIgnoreCase("user")) {
                                            Log.i("check:", "main_acticity");
                                            Intent i = new Intent(MainActivity.this, User_Welcome.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            // Add new Flag to start new Activity
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            // Staring Login Activity
                                            MainActivity.this.startActivity(i);
                                            finish();
                                        }
                        else if (type.equalsIgnoreCase("vendor")) {
                                            Intent i = new Intent(MainActivity.this, Vendor_Welcome.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            // Add new Flag to start new Activity
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            i.putExtra("notifyStatus", "0");
                                            // Staring Login Activity
                                            MainActivity.this.startActivity(i);
                                            finish();
                                        }
                        // \n is for new line
                    }
                    else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.

                        Log.i("location_alert:", "main_acticity");
                        //   gps.showSettingsAlert();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                        // Setting Dialog Title
                        alertDialog.setTitle(getResources().getString(R.string.gps_alert));
                        // Setting Dialog Message
                        alertDialog.setMessage(getResources().getString(R.string.gps_mess));
                                        // On pressing the Settings button.
                        alertDialog.setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Log.i("check:", "GPSTracker_ok");
                            }
                        });

                        // On pressing the cancel button
                        alertDialog.setNegativeButton(getResources().getString(R.string.cancel_changes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Log.i("check:", "GPSTracker_cancel"); }
                        });

                        // Showing Alert Message
                        Log.i("check:", "track");
                        alertDialog.show();

                        getLocation();
                    }
                }
                },3000);


    }// end background task


    private void OFFER_JSON()
    {
        ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Offers_get_set>> call=apiInterface.monthly_subscription();

        call.enqueue(new Callback<ArrayList<Offers_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<Offers_get_set>> call, Response<ArrayList<Offers_get_set>> response) {
                Log.d("URL..",response.toString());
//                Log.d("new URL....",response.body().toString());
             //   p.dismiss();

                try{
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                       globalVariables.setOffer_list(response.body());
                    }
                }

                catch (Exception ex){
                    Log.e("Error4::",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Offers_get_set>> call, Throwable t) {
              Log.i("FAILURE ERROR::",t.getMessage());
            }
        });
    }



    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            Log.i("pack",MainActivity.this.getPackageName() );

            String newVersion = null;
            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (org.jsoup.nodes.Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (org.jsoup.nodes.Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);


            Log.d("normal1234","Update");
            Log.i("check", onlineVersion+" -please update- "+currentVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {

                    Log.i("check", "please update");

                    //show dialog
                    final String appPackageName = getPackageName(); // getPackageName() from MainActivity.java or Activity object
                    /* */
                    String[] arr=onlineVersion.split("\\.");

                    Log.i("sdf",arr[1]);

                    int Num=Integer.parseInt(arr[1]);

                    Log.d("Num", String.valueOf(Num));
                    if(Num%2==0)
                    {

                        Log.d("FORCE","Update");
                        Forcedupdate();
                    }
                    else
                    {
                        Log.d("normal","Update");
                        Normalupdate();
                    }
                }
                else
                {
                    Log.d("enfer","Update");

                    goto_activity();
                }
            }
            else if(onlineVersion==null)
            {
                goto_activity();
            }
            //  goto_activity();
        }
    }

    public void Normalupdate()
    {
        SweetAlertDialog ff=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.SUCCESS_TYPE);
        ff.setTitleText(getResources().getString(R.string.update_avail));
        ff.setContentText(getResources().getString(R.string.update_massage));
        ff.setCanceledOnTouchOutside(false);
        ff.setConfirmText(getResources().getString(R.string.update_now));
        ff.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                }
            }
        });

        ff.setCancelButton(getResources().getString(R.string.update_not), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();

                goto_activity();
            }
        });
        ff.show();



       /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("There is a new Version Available for this app. Kindly update for latest features.")
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                        }

                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.dismiss();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setCanceledOnTouchOutside(true);
        alert.show();*/


    }

    public void Forcedupdate()
    {
        Intent ii = new Intent(MainActivity.this, Update_Activity.class);
        startActivity(ii);
        finish();
    }

    public void goto_activity()
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

    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

}
