package com.hrproject.Fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.hrproject.Activities.user.Rating_Review;
import com.hrproject.Activities.user.Sub_Product_List;
import com.hrproject.Activities.user.User_Offer_Description;
import com.hrproject.Adapters.homePagerAdapter;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.GetterSetter.Slider_Get_Set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.ExpandableHeightGridView;
import com.hrproject.HelperClasses.GPSTracker;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.santalu.autoviewpager.AutoViewPager;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home_User extends Fragment
{
    ExpandableHeightGridView home_grid_view;
    AutoViewPager home_viewPager;
    CircleIndicator defaultCircleIndicator;
    TextView location_text;
    AutoCompleteTextView autoCompleteTextView;
    AlertDialog p;
    String user_id,main_cat_id;
    LinearLayout layout;
    ArrayList<String> category_name=new ArrayList<>();
    ArrayList<String> category_id=new ArrayList<>();
    ArrayList<Offers_get_set> datalist=new ArrayList<>();

    String address;
    UserSessionManager session;
    private int LOCATION_CODE=2;
    GlobalVariables globalVariables;
    double latitude,longitude;
    RecyclerView recyclerView;
    Subscription_LIST adapter;
    androidx.appcompat.widget.SearchView searching;
    homeListAdapter category_adapter;
    String cate_name,cate_id;
    String unique_id="",status="";
    ArrayList<Get_Set> arrayListName=new ArrayList<>();
    ArrayList<Get_Set> filter_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home__user, container, false);

        location_text=v.findViewById(R.id.location_text);
        home_viewPager=v.findViewById(R.id.home_viewPager);
        layout=v.findViewById(R.id.layout_home);
       // searching=v.findViewById(R.id.search_txt);
       autoCompleteTextView=v.findViewById(R.id.search_txt);
        recyclerView=v.findViewById(R.id.scroll_list);
        defaultCircleIndicator=v.findViewById(R.id.defaultCircleIndicator);

        globalVariables=GlobalVariables.getInstance();

        session=new UserSessionManager(getActivity());
        HashMap<String,String> data=session.getUserDetails();
        user_id=data.get(session.KEY_ID);

        HashMap<String,String> service_status=session.getChatId();
        unique_id=service_status.get(session.KEY_CHATID);
        status=service_status.get(session.KEY_Status);

        System.out.println("unique_id:"+unique_id);
        System.out.println("unique_id:"+status);

        if(unique_id.equalsIgnoreCase("")){

        }
        else {
            if (status.equalsIgnoreCase("4")){
                startActivity(new Intent(getActivity(), Rating_Review.class));
            }
            else {

            }
        }

        System.out.println("user_id"+user_id);
        home_grid_view=v.findViewById(R.id.home_grid_view);
        home_grid_view.setExpanded(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);


        VectorMasterView heartVector =v.findViewById(R.id.heart_vector);

// find the correct path using name
        PathModel outline = heartVector.getPathModelByName("outline");

        outline.setStrokeColor(Color.parseColor("#ffffff"));
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


      /*  searching.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("text:"+newText);
                category_adapter.getFilter().filter(newText);

               // category_adapter.notifyDataSetChanged();
                return false;
            }
        });
      */
      checknet();
        return v;
    } // end onCreate View

    private void checknet() {
        // check for Internet status
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            Objects.requireNonNull(p.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();

            slider();
            category_list_fetching();
            //search_Category_Json();
            /*OFFER_JSON();*/

            if (permissionAlreadyGranted()) {
                System.out.println("welcome, permission granted!!");
                getLocation();
            }

            requestPermission();
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
                    p.dismiss();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            ff.show();
        }

    }

    private void slider()
    {
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Slider_Get_Set>> call=a.view_slider("1");
        call.enqueue(new Callback<ArrayList<Slider_Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Slider_Get_Set>> call, Response<ArrayList<Slider_Get_Set>> response)
            {
                //     p.dismiss();
                Log.d("url",response.toString()) ;
                globalVariables.setViewPager(response.body());

                home_viewPager.setCurrentItem(0);
                home_viewPager.setAdapter(new homePagerAdapter(getActivity(),globalVariables.getViewPager()));
                defaultCircleIndicator.setViewPager(home_viewPager);

            }// end onresponse
            @Override
            public void onFailure(Call<ArrayList<Slider_Get_Set>> call, Throwable t)
            {
                //   p.dismiss();
                Log.d("message_errror1",t.getMessage());
            }
        });
    }

    public class Subscription_LIST extends RecyclerView.Adapter<Subscription_LIST.MyViewHolder>
    {
        Context c;
        ArrayList<Offers_get_set> data;

        public Subscription_LIST(FragmentActivity item_accept_reject_list, ArrayList<Offers_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public Subscription_LIST.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_user_offer_list, viewGroup, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final Subscription_LIST.MyViewHolder myViewHolder, final int i) {
            //    GetSet movie = data.get(i);
            Picasso.with(c).load(data.get(i).getImage()).placeholder(R.drawable.man).into(myViewHolder.image);
            String fees=data.get(i).getFees();
            String validity=data.get(i).getValidity();
            String offer_image=data.get(i).getImage();
            String title=data.get(i).getTitle();
            String descrp=data.get(i).getDescription();

            System.out.println("fees:"+fees);
            System.out.println("validity:"+validity);
            System.out.println("offer_image:"+offer_image);
            System.out.println("title:"+title);
            System.out.println("descrp:"+descrp);

            myViewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), User_Offer_Description.class);
                    intent.putExtra("fees",fees);
                    intent.putExtra("validity",validity);
                    intent.putExtra("offer_image",offer_image);
                    intent.putExtra("descrp",descrp);
                    intent.putExtra("title",title);
                    startActivity(intent);
                }
            });
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
            public ImageView image;

            public MyViewHolder(View view) {
                super(view);
                image=view.findViewById(R.id.offer_image);

            }
        }
    }

    private void getLocation() {
        GPSTracker gps = new GPSTracker(getActivity());

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if(addresses.size()>0)
                {
                    address = addresses.get(0).getAddressLine(0);
                    // If any additional address line present than only, check with
                    // max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    Log.d("check_cityName",city);
                    Log.d("check_address",address);
                    Log.d("check_state",state);
                    Log.d("check_country",country);
                    Log.d("check_postalCode",postalCode);
                    Log.d("check_knownName",knownName);

                    location_text.setText(""+address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateLocationJsnon();

            // \n is for new line
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }


    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION );

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )) {

        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_CODE) {


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //    Toast.makeText(getActivity(), "Permission granted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(getActivity(), "Permission is denied!", Toast.LENGTH_SHORT).show();
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION );
                if (! showRationale) {
                    openSettingsDialog();
                }
            }
        }
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.required_permission));
        builder.setMessage(getResources().getString(R.string.permission_explain));
        builder.setPositiveButton(getResources().getString(R.string.to_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void category_list_fetching() {
        System.out.println("Language_type:"+session.getlanguage());
    ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
    retrofit2.Call<ArrayList<Get_Set>> call=a.category_list(session.getlanguage());

    call.enqueue(new Callback<ArrayList<Get_Set>>() {
        @Override
        public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
        {
            Log.d("url",response.toString()) ;
            p.dismiss();

           try{
               if(response.body().size()>0)
               {
                   category_adapter=new homeListAdapter(getActivity(),response.body());
                   home_grid_view.setAdapter(category_adapter);

                   arrayListName=response.body();
                   autoCompleteTextView.setThreshold(0);
                   autoCompleteTextView.addTextChangedListener(new TextWatcher() {

                       @Override
                       public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                           // When user changed the Text
                           System.out.println("text_changed");
                         //  category_adapter.getFilter().filter(cs);

                           if (cs.length()>0)
                           {
                               filter_list=new ArrayList<>();
                               for (Get_Set data:arrayListName) {
                                   if (data.getCategory_name().toLowerCase().contains(cs.toString())
                                           || data.getCategory_name().toUpperCase().contains(cs.toString())
                                   || data.getSub_categories().toUpperCase().contains(cs.toString())
                                   || data.getSub_categories().toLowerCase().contains(cs.toString())){

                                       System.out.println("filter:"+"filter_list");
                                       filter_list.add(data);
                                   }

                                   else {
                                       System.out.println("filter:"+"else");
                                       Log.i("text:",cs.toString());
                                   }
                               }

                               if (filter_list.size()>0){
                                   System.out.println("filter:"+"size_if");
                                   category_adapter=new homeListAdapter(getActivity(),filter_list);
                                   home_grid_view.setAdapter(category_adapter);
                               }

                               else {
                                   System.out.println("filter:"+"size_else");
                                   category_adapter=new homeListAdapter(getActivity(),arrayListName);
                                   home_grid_view.setAdapter(category_adapter);
                               }
                           }

                           else {
                               System.out.println("text:"+"filter_list");
                               category_adapter=new homeListAdapter(getActivity(),arrayListName);
                               home_grid_view.setAdapter(category_adapter);
                           }
                       }

                       @Override
                       public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

                       @Override
                       public void afterTextChanged(Editable text) {
                         //  category_adapter.getFilter().filter(text);

                       }
                   });

/*                   home_viewPager.setCurrentItem(0);
                   home_viewPager.setAdapter(new homePagerAdapter(getActivity(),globalVariables.getViewPager()));
                   defaultCircleIndicator.setViewPager(home_viewPager);*/

                   datalist=new ArrayList<>(globalVariables.getOffer_list());
                   adapter=new Home_User.Subscription_LIST( getActivity(),datalist);
                   recyclerView.setAdapter(adapter);
               }
           }
            catch (Exception ex){
                Log.i("error2:",ex.getMessage());

            }
        }// end onresponse

        @Override
        public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
        {
            //category_list_fetching();
            home_viewPager.setCurrentItem(0);
            home_viewPager.setAdapter(new homePagerAdapter(getActivity(),globalVariables.getViewPager()));
            defaultCircleIndicator.setViewPager(home_viewPager);

            datalist=new ArrayList<>(globalVariables.getOffer_list());
            adapter=new Home_User.Subscription_LIST( getActivity(),datalist);
            recyclerView.setAdapter(adapter);

            category_list_fetching();

       //     p.dismiss();
            Log.d("message_errror",t.getMessage());
        }
    });
}

    public class homeListAdapter extends BaseAdapter  {
        ArrayList<Get_Set> body;
        FragmentActivity activity;

        public homeListAdapter(FragmentActivity activity, ArrayList<Get_Set> body) {
            this.activity = activity;
            this.body = body;
        }

        @Override
        public int getCount() {
            return body.size();
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
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_category_layout, null, true);

            TextView list_name =  view.findViewById(R.id.list_name);
            ImageView list_image  = view.findViewById(R.id.list_image);
            TextView Sublist_name  = view.findViewById(R.id.Sublist_name);

            list_name.setText("" + body.get(i).getCategory_name());
            Glide.with(activity).load(body.get(i).getCategory_image()).into(list_image);
         //   Sublist_name.setText(""+body.get(i).getSub_categories());

            String name = body.get(i).getSub_categories();
            System.out.println("sub_cat:"+name);

            String repl = name.replaceAll(",", "|");
            Sublist_name.setText("" + repl);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, Sub_Product_List.class);
                    intent.putExtra("category_name", body.get(i).getCategory_name());
                    intent.putExtra("category_id", body.get(i).getCategory_id());
                    startActivity(intent);

                }
            });

            return view;
        }

     /*   @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    body = (ArrayList<Get_Set>) results.values;
                    category_adapter.notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<Get_Set> FilteredArrayNames = new ArrayList<Get_Set>();

                    // perform your search here using the searchConstraint String.

                    if (arrayListNames == null) {
                        arrayListNames = new ArrayList<>(body); // saves the original data in mOriginalValues
                    }

                    *//********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********//*
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = arrayListNames.size();
                        results.values = arrayListNames;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        System.out.println("data:"+constraint);
                        for (int i = 0; i < body.size(); i++) {
                            String dataNames = body.get(i).getCategory_name();
                            System.out.println("dataNames:" + dataNames);
                            if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrayNames.add(new Get_Set(arrayListNames.get(i).getCategory_name()
                                        ,arrayListNames.get(i).getSub_categories()
                                ,arrayListNames.get(i).getCategory_image()));
                            }
                        }

                        results.count = FilteredArrayNames.size();
                        results.values = FilteredArrayNames;
                        Log.e("VALUES", results.values.toString());
                    }
                    return results;
                }
            };

            return filter;
        }*/


        }

    private void updateLocationJsnon()
    {
        System.out.println("user_id"+user_id);
        System.out.println("latitutde"+String.valueOf(latitude));
        System.out.println("Longitude"+String.valueOf(longitude));
        System.out.println("Address"+address);

        ApiInterface a=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.update_user_location(user_id,String.valueOf(latitude),String.valueOf(longitude),address);

        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.i("response:",response.toString());
                p.dismiss();
                try{
                    if(response.body().get(0).getError().equalsIgnoreCase("0"))
                    {
                        Snackbar snackbar=Snackbar.make(layout.findViewById(R.id.layout_home),""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                }
                catch (Exception ex){

                    Log.i("error1:",ex.getMessage());
                    //    updateLocationJsnon();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {

                Log.i("FAILURE_ERROR1:",t.getMessage());
                p.dismiss();
            }
        });

    }

   /*     public Filter getFilter() {
            return myFilter;
        }

        Filter myFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                Get_Set customer = (Get_Set) resultValue;
                return customer.getCategory_name();
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                System.out.println("char_text:"+charSequence);
                if (charSequence != null) {
                    suggestions.clear();
                    for (Get_Set cust : tempCustomer) {
                        System.out.println("category_name:"+cust.getCategory_name());
                        if (cust.getCategory_name().toLowerCase().contains(suggestions.toString().toLowerCase())) {
                            suggestions.add(cust);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ArrayList<Get_Set> c =  (ArrayList<Get_Set> )filterResults.values ;
                if (filterResults != null && filterResults.count > 0) {
                //    suggestions.clear();
                    for (Get_Set cust : c) {
                        suggestions.add(cust);
                        notifyDataSetChanged();
                    }
                }
                else{
                    suggestions.clear();
                    notifyDataSetChanged();
                }
            }
        };*/
   // }


}
