package com.hrproject.Activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrproject.R;
import com.squareup.picasso.Picasso;

public class User_Offer_Description extends AppCompatActivity {
    TextView validity,fees,title,descrp;
   // ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__offer__description);

        validity=findViewById(R.id.offer_validity);
        fees=findViewById(R.id.offer_fees);
        descrp=findViewById(R.id.subsc_descrp);
        title=findViewById(R.id.offer_title);

        String validation="<b>" + getIntent().getExtras().getString("validity")+" "+getResources().getString(R.string.days)
                + "</b> " + getResources().getString(R.string.validity_date);


        validity.setText(Html.fromHtml(validation));
        fees.setText("\u20B9 "+getIntent().getExtras().getString("fees"));
        title.setText(getIntent().getExtras().getString("title"));
       descrp.setText(getIntent().getExtras().getString("descrp"));

/*        getResources().getString(R.string.offer_charges)*/

    //  String descrp=getIntent().getExtras().getString("descrp");

        System.out.println("title:"+title);
        System.out.println("descrp:"+descrp);
        System.out.println("image:"+getIntent().getExtras().getString("offer_image"));
    }
}
