package com.hrproject.Activities.Vendor;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hrproject.Activities.Registration_Activity;
import com.hrproject.R;
import com.hrproject.Tab_Fragments.Personal_Details;
import com.hrproject.Tab_Fragments.Skills_Details;
import com.hrproject.Tab_Fragments.Vendor_Personal_details;
import com.hrproject.Tab_Fragments.Vendor_Verification;
import com.hrproject.Tab_Fragments.Verification_Details;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class Vendor_Registration extends AppCompatActivity {
    public TabLayout tabLayout;
    private Vendor_Personal_details fragmentOne;
    private Vendor_Verification fragmentTwo;
    private Skills_Details fragmentThree;


  //  public TextInputEditText description;


    // public ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__registration);

        Toolbar toolbar = findViewById(R.id.tool_vendorRegister);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        tabLayout = findViewById(R.id.Vendor_tabLayout);

        bindWidgetsWithAnEvent();
        setupTabLayout();

        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
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
        fragmentOne = new Vendor_Personal_details();
        fragmentTwo = new Vendor_Verification();
        fragmentThree=new Skills_Details();
        tabLayout.addTab(tabLayout.newTab().setText( getResources().getString(R.string.tab1)),true);
        tabLayout.addTab(tabLayout.newTab().setText( getResources().getString(R.string.tab2)));
        tabLayout.addTab(tabLayout.newTab().setText( getResources().getString(R.string.tab3)));
    }

    private void bindWidgetsWithAnEvent()
    {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
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
            case 2:
                replaceFragment(fragmentThree);
                break;
        }
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_registration, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }




   /* public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Vendor_Personal_details();
                case 1:
                    return new Vendor_Verification();
                default:
                    return new Skills_Details();
            }
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab1);
                case 1:
                    return getResources().getString(R.string.tab2);
                default:
                    return getResources().getString(R.string.tab3);
            }
        }
    }*/
}
