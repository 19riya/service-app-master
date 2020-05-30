package com.hrproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrproject.Activities.Vendor.Vendor_Show_Profile;
import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.ChangePassword;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vendor_Profile extends Fragment {
    UserSessionManager session;
    String vendor_id;
    Context context;
    AlertDialog alertDialog;
    String status_hindi="0",status_english="0";
    AlertDialog p;
/*
    public static Vendor_Profile newInstance() {

        return new Vendor_Profile();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        session = new UserSessionManager(getActivity());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.setLocale(new Locale(session.getlanguage()));
        res.updateConfiguration(conf, dm);


        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_vendor__profile, container, false);
        context=getActivity();

        view.findViewById(R.id.vendor_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.vendor_change_language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });


        view.findViewById(R.id.vshare_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checknet();
            }
        });

        //  System.out.println("chat_id:"+session.getChatId());

        HashMap<String,String> data=session.getUserDetails();
        vendor_id=data.get(session.KEY_ID);


        view.findViewById(R.id.logout_vendor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE);
                ff.setTitle(getResources().getString(R.string.alert));
                ff.setContentText(getResources().getString(R.string.sure_exit));
                ff.setCanceledOnTouchOutside(false);
                ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        session.logoutUser();
                        sweetAlertDialog.dismissWithAnimation();
                        ((Vendor_Welcome)getActivity()).finish();

                    }
                });

                ff.setCancelButton(getResources().getString(R.string.cancel_changes), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                    }
                });
                ff.show();
            }
        });

        view.findViewById(R.id.myProfileLayout).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(getActivity(), Vendor_Show_Profile.class);
                startActivity(i);
            }
        });
        return view;
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        System.out.println("status:::"+isInternetPresent);
        // check for Internet status
        if (isInternetPresent) {

            loader();

            SHARE_APP();
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

    private void SHARE_APP() {
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=apiInterface.share_app();
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response) {
                p.dismiss();
                Log.d("URL:",response.toString());
                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, ""+getResources().getString(R.string.app_name));
                        String shareMessage=response.body().get(0).getMsg();
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));

/*
                        String shareMessage= "\nLet me recommend you this application\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
*/

                    } catch(Exception e) {
                        //e.toString();
                    }

                }
                else {
                    Log.e("Share_app_else:",response.body().get(0).getMsg());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.i("Share_app_failure:",t.getMessage());
            }
        });
    }

    private void loader() {
        androidx.appcompat.app.AlertDialog.Builder mBuilder =
                new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.loader, null);
        mBuilder.setView(mView);
        p = mBuilder.create();
        p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p.setCancelable(false);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        p.setCanceledOnTouchOutside(false);
        p.show();
    }

    private void changeLanguage() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_change_language, null);


        builder.setCancelable(false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        //  alertDialog=new androidx.appcompat.app.AlertDialog(new ContextThemeWrapper(this,R.style.DialogSlideAnim));

        alertDialog.show();

        CheckedTextView hindi_language=dialogView.findViewById(R.id.hindi_language);
        CheckedTextView english_language=dialogView.findViewById(R.id.english_language);
        TextView submit_language=dialogView.findViewById(R.id.submit_language);
        ImageView hindi_checked=dialogView.findViewById(R.id.hindi_checked);
        ImageView english_checked=dialogView.findViewById(R.id.english_checked);

        hindi_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hindi_language.isChecked()){
                    status_hindi="0";
                    hindi_language.setChecked(false);
                    hindi_checked.setVisibility(View.GONE);
                    english_checked.setVisibility(View.GONE);

                }
                else {
                    hindi_checked.setVisibility(View.VISIBLE);
                    english_checked.setVisibility(View.GONE);
                    status_hindi="1";
                    status_english="0";

                    session.setLanguage("hi");
                }
            }
        });

        english_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hindi_language.isChecked()){
                    hindi_checked.setVisibility(View.GONE);
                    english_checked.setVisibility(View.GONE);
                    status_english="0";

                }
                else {
                    hindi_checked.setVisibility(View.GONE);
                    english_checked.setVisibility(View.VISIBLE);
                    status_english="1";
                    status_hindi="0";
                    session.setLanguage("en");
                }
            }
        });


        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        submit_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateResources(context,session.getlanguage());

                Log.d("Check Update", String.valueOf(updateResources(context,session.getlanguage())));

                Intent i = getContext()
                        .getPackageManager()
                        .getLaunchIntentForPackage(getContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                alertDialog.dismiss();
            }
        });

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
