package com.hrproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Update_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_);
        AlertDialog.Builder builder = new AlertDialog.Builder(Update_Activity.this);
        builder.setMessage(getResources().getString(R.string.update_massage))
                .setTitle(getResources().getString(R.string.update_avail))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Update_Activity.this.getPackageName())));
                            finish();
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Update_Activity.this.getPackageName())));
                            finish();
                        }
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }
}
