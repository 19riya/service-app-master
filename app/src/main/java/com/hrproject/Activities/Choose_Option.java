package com.hrproject.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hrproject.Activities.Vendor.Vendor_Login;
import com.hrproject.Activities.user.User_Login;
import com.hrproject.HelperClasses.Image_Utility;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;

import java.util.Locale;

public class Choose_Option extends AppCompatActivity {
    Context context;
    LinearLayout language;
    UserSessionManager session;
    String[] planets;
    TextView  languageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(Choose_Option.this);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(session.getlanguage())); // API 17+ only.
        }
        // Use conf.locale = new Locale(...) if targeting lower versions*/

        conf.setLocale(new Locale(session.getlanguage()));
        res.updateConfiguration(conf, dm);
        setContentView(R.layout.activity_choose__option);
        context = this;

        language = findViewById(R.id.choose_language);
        languageText = findViewById(R.id.languageText);

        planets = res.getStringArray(R.array.list_items);
        languageText.setText(res.getString(R.string.language));

        System.out.println("check:"+Image_Utility.checkPermission(context));


        if (Image_Utility.checkPermission(context)) {
            findViewById(R.id.layout_vendor).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Choose_Option.this, Vendor_Login.class));
                }
            });
            findViewById(R.id.layout_user).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Choose_Option.this, User_Login.class));
                }
            });
        }

        System.out.println("WELCOME");

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.select_language));
                builder.setIcon(R.drawable.ic_translate_black_24dp);
                builder.setItems(planets, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            session.setLanguage("en");
                            updateResources(context,session.getlanguage());

                            Log.d("Check Update", String.valueOf(updateResources(context,session.getlanguage())));
                            Intent i = getBaseContext()
                                    .getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                        if (which == 1) {
                           session.setLanguage("hi");
                            updateResources(context,session.getlanguage());

                            Log.d("Check Update", String.valueOf(updateResources(context,session.getlanguage())));
                            Intent i = getBaseContext()
                                    .getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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
