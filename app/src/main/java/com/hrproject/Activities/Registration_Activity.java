package com.hrproject.Activities;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.R;
import com.hrproject.Tab_Fragments.Personal_Details;
import com.hrproject.Tab_Fragments.Vendor_Verification;
import com.hrproject.Tab_Fragments.Verification_Details;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Registration_Activity extends AppCompatActivity
{
    public TabLayout tabLayoutRegister;
    private Personal_Details fragmentOne;
    private Verification_Details fragmentTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_);

        Toolbar toolbar=findViewById(R.id.tooluser_register);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            { finish(); }

        });



        tabLayoutRegister=findViewById(R.id.tabLayoutRegister);

        bindWidgetsWithAnEvent();
        setupTabLayout();

        LinearLayout tabStrip = ((LinearLayout)tabLayoutRegister.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }


    }// end oncreate method

    private void setupTabLayout() {
        fragmentOne = new Personal_Details();
        fragmentTwo = new Verification_Details();
        tabLayoutRegister.addTab(tabLayoutRegister.newTab().setText( getResources().getString(R.string.tab1)),true);
        tabLayoutRegister.addTab(tabLayoutRegister.newTab().setText( getResources().getString(R.string.tab2)));
    }


    private void bindWidgetsWithAnEvent()
    {
        tabLayoutRegister.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
        //        checkNet();

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(fragmentOne);
                break;
            case 1 :
                replaceFragment(fragmentTwo);
                break;
        }
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.user_frame_registration, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

   /* public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position) {
                case 0:
                    return new Personal_Details();
                default:
                    return new Verification_Details();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab1);
                default:
                    return getResources().getString(R.string.tab2);
            }
        }
    }*/

}
