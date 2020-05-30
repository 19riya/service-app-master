package com.hrproject.Adapters;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hrproject.GetterSetter.Slider_Get_Set;
import com.hrproject.R;
import com.santalu.autoviewpager.AutoViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class homePagerAdapter extends PagerAdapter
{
    private LayoutInflater inflater;
    AppCompatActivity activity;
    int[] imgarr;

    ArrayList<Slider_Get_Set> body;


    public homePagerAdapter(FragmentActivity activity, ArrayList<Slider_Get_Set> body)
    {
        this.activity= (AppCompatActivity) activity;
        this.body=body;
    }

    @Override
    public int getCount() {
        return body.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        inflater = (LayoutInflater)   activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_pager, container,
                false);

        ImageView img = viewLayout.findViewById(R.id.img);
      /*  img.setImageResource(imgarr[position]);*/

       // Glide.with(activity).load(body.get(position).getSlider_image()).into(img);

        Picasso.with(activity).load(body.get(position).getSlider_image()).placeholder(R.drawable.ucbanner)
                .into(img);

        System.out.println("slide_show"+body.get(position).getSlider_image());
        //Toast.makeText(activity, "b" +body.size(), Toast.LENGTH_SHORT).show();

        ((AutoViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);

    }


}

