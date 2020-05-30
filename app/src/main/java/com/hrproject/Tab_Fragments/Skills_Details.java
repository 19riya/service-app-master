package com.hrproject.Tab_Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;


import com.hrproject.Activities.Otp_Verify;
import com.hrproject.Activities.Registration_Activity;
import com.hrproject.Activities.Vendor.Vendor_Login;
import com.hrproject.Activities.Vendor.Vendor_Registration;
import com.hrproject.Activities.user.User_Login;
import com.hrproject.BuildConfig;
import com.hrproject.Fragments.Home_User;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import org.jsoup.helper.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Skills_Details extends Fragment
{
    LinearLayout linear_checkbox;
  //  Spinner skills_spinner;
LinearLayout skills_spinner;
    ArrayList<String> category_name=new ArrayList<>();
    ArrayList<String> category_id=new ArrayList<>();
    UserSessionManager sessionManager;
    private int CAMREA_CODE=1;

    String cb_value;

    String Simage="0";
    String main_cat_id;
   // TabLayout tabLayout;
    String skills_description_value="0",experiance_value="0";
    TextInputLayout lskill_descrp,lexperience;

    public TextInputEditText skills_description,experiance_Decription;
    public ImageView image_experiance_;

    FloatingActionButton previous_skills;
    FloatingActionButton save_skills;
    CoordinatorLayout layout;

    LinearLayout expr_layout;

    Context context;
    GlobalVariables globalVariables;
    String chek_seperate_value="";
    File mPhotoFile;
    FileUtils fileUtils;

    ArrayList<String> checkbox_list;

    AlertDialog p;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_skills__details, container, false);
       context =  getActivity();

       sessionManager=new UserSessionManager(context);

        expr_layout=v.findViewById(R.id.expr_layout);
        linear_checkbox=v.findViewById(R.id.linear_checkbox);
        skills_spinner=v.findViewById(R.id.skills_spinner);
        layout=v.findViewById(R.id.skill_layout);
        lskill_descrp=v.findViewById(R.id.layoutvendor_skillsDesLayout);
        lexperience=v.findViewById(R.id.layoutvendor_experianceDesLayout);
        image_experiance_=v.findViewById(R.id.image_experiance_vendor);

        fileUtils=new FileUtils();

        ConnectionDetector cd = new ConnectionDetector(context);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent)
        {
            loadCategory();
        }
        else
        {
            SweetAlertDialog ff=new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
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



        skills_description=v.findViewById(R.id.vendor_skillsDesLayout);
        experiance_Decription=v.findViewById(R.id.vendor_experianceDesLayout);
     //   loadCategory();

        globalVariables=GlobalVariables.getInstance();

        previous_skills=v.findViewById(R.id.previous_skills);
        save_skills=v.findViewById(R.id.save_skills);

        experiance_Decription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                experiance_value=experiance_Decription.getText().toString();

                if (experiance_value.equalsIgnoreCase("0")){
                    expr_layout.setVisibility(View.GONE);
                }
                else {
                    expr_layout.setVisibility(View.VISIBLE);
                }
            }
        });


        previous_skills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ((Vendor_Registration)getActivity()).tabLayout.getTabAt(1).select();

            }
        });

        image_experiance_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted()) {
                    selectImage();
                    //       Toast.makeText(getActivity(), "Permission is already granted!", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestPermission();

            }
        });



        save_skills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkNet();
            }
        });

        return v;
    }

    private void submit_vendor_Json()
    {
        Log.d("final:",main_cat_id);
        Log.d("final1:",chek_seperate_value);
        Log.d("experiance_value:",experiance_value);

        ApiInterface a=ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.getVendor_registration(
                globalVariables.getVendor_name(),
                globalVariables.getVendor_mail(),
                globalVariables.getVendor_mobile(),
                globalVariables.getVendor_dob(),
                globalVariables.getVendor_password(),
                globalVariables.getVendor_address(),
                globalVariables.getVendor_description(),
                globalVariables.getVendor_id_type(),
                globalVariables.getVendor_front_image(),
                globalVariables.getVendor_back_image(),
                globalVariables.getVendor_police_verify(),
                skills_description_value,experiance_value,Simage,main_cat_id,chek_seperate_value,
                globalVariables.getVendor_image(),
                globalVariables.getLong_status());

        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("Urel",response.toString());
                p.dismiss();
                if(response.body().get(0).getError().equalsIgnoreCase("0"))
                {
                    p.dismiss();
                    SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE);
                    ff.setTitleText(getResources().getString(R.string.success));
                    ff.setContentText(response.body().get(0).getMsg());
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            Intent i=new Intent(getActivity(), Vendor_Login.class);
                            startActivity(i);
                            ((Vendor_Registration)getActivity()).finish();
                            sweetAlertDialog.dismissWithAnimation(); }
                    });
                    ff.show();




                }
                else
                {
                    p.dismiss();
                    SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE);
                    ff.setTitleText(getResources().getString(R.string.internet_connection));
                    ff.setContentText(response.body().get(0).getMsg());
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        { sweetAlertDialog.dismissWithAnimation(); }
                    });
                    ff.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t) {
                Log.i("FAILURE ERROR::",t.getMessage());
                p.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globalVariables.getVenExperImg_Url() !=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getVenExperImg_Url()), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image_experiance_.setImageBitmap(bitmap);

            if (globalVariables.getVendor_skill_description() !=null)
            {
                skills_description.setText(globalVariables.getVendor_skill_description());
            }

            if (globalVariables.getVendor_experience() !=null){
                experiance_Decription.setText(globalVariables.getVendor_experience());
            }



        }
    }

    public  boolean validate()
    {
        boolean valid=true;
/*

        if(Simage==null)
        {
            Snackbar snackbar=Snackbar.make(layout,""+getResources().getString(R.string.err_certi_image),Snackbar.LENGTH_LONG);
            snackbar.show();

            valid=false;
        }
*/

        skills_description_value=skills_description.getText().toString();

       /* if(skills_description_value.isEmpty() || (skills_description_value.trim().length()<4) )
        {
            lskill_descrp.setError(Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_descr)+"</font>"));
            valid=false;
        }
        else
        {
            globalVariables.setVendor_skill_description(skills_description_value);
            lskill_descrp.setError(null);
        }


*/

        if(experiance_value.isEmpty())
        {
            lexperience.setError(Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_exper)+" </font>"));
            valid=false;
        }
        else
        {
            globalVariables.setVendor_experience(experiance_value);
            lexperience.setError(null);
        }

        if (chek_seperate_value.length()==0){
            Snackbar snackbar=Snackbar.make(layout,getResources().getString(R.string.select_subCatg),Snackbar.LENGTH_LONG);
            snackbar.show();
            valid=false;
        }

        return  valid;
    }

    private void checkNet()
    {
        ConnectionDetector cd = new ConnectionDetector(context);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent)
        {
            if(!validate())
            {
                onLoginFailed();
                return;
            }
            else
            {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                p = mBuilder.create();
                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                p.setCancelable(false);
                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                p.setCanceledOnTouchOutside(false);
                p.show();

                submit_vendor_Json();
            }

        }
        else
        {
            SweetAlertDialog ff=new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
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

    private void onLoginFailed()
    {
    }

    public void selectImage()
    {
        LinearLayout camerall,galleryll;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.picimage, null);

        final ImageView close= dialogView.findViewById(R.id.iconclose);

        camerall =  dialogView.findViewById(R.id.camerall);
        galleryll=  dialogView.findViewById(R.id.galleryll);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(600, 400);
        dialog.show();

        camerall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = fileUtils.createImageFile(getActivity());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                        System.out.println("photo_URI:"+photoURI);
                        mPhotoFile = photoFile;
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 4);
                    }
                }
            }
        });

        galleryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pickPhoto, 1);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void loadCategory()
    {
        System.out.println("Language_type:"+sessionManager.getlanguage());
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Get_Set>> call=a.category_list(sessionManager.getlanguage());
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("response",response.toString());
            //    Log.d("response2",response.body().toString());

//                p.dismiss();
                ArrayList<String> checkbox_list1=new ArrayList<>();

                skills_spinner.setOrientation(LinearLayout.VERTICAL);
                for(int i=0;i<response.body().size();i++)
                {
                    CheckBox cb = new CheckBox(getActivity());
                    System.out.println("category_name"+response.body().get(i).getCategory_name());
                    cb.setText(response.body().get(i).getCategory_name());
                    cb.setTextSize(18);
                    cb.setTextColor(Color.BLACK);


                    int finalI = i;

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                        {
                            String cb_value1="";
                            if(b)
                            {
                                cb_value1=response.body().get(finalI).getCategory_id();
                                checkbox_list1.add(cb_value1);
                                Log.d("VALUEIB",cb_value1);
                                main_cat_id= StringUtil.join(checkbox_list1,",");
                                Log.d("VALUEIB12",main_cat_id);
                                loadCheckboxes(main_cat_id);
                            }
                            else
                            {
                                checkbox_list1.remove(response.body().get(finalI).getCategory_id());
                                Log.d("VALUE:", String.valueOf(checkbox_list1));
                                main_cat_id= StringUtil.join(checkbox_list1,",");
                                Log.d("VALUEIB22remove",main_cat_id);

                                chek_seperate_value= StringUtil.join(checkbox_list,",");
                                System.out.println("subCategory1"+chek_seperate_value);
                                System.out.println("subCategory"+cb_value);
                                loadCheckboxes(main_cat_id);
                                //   linear_checkbox.removeAllViews();
                            }

                        }
                    });
                    skills_spinner.addView(cb);



                }

                /*Log.d("response_spi",response.toString());
                Log.d("response2_spin",response.body().toString());
                if(response.body().size()>0)
                {
                    //p.dismiss();
                    for(int i=0;i<response.body().size();i++)
                    {
                        category_name.add(response.body().get(i).getCategory_name());
                        category_id.add(response.body().get(i).getCategory_id());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, category_name);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    skills_spinner.setAdapter(adapter);
                    skills_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                          String  spinner_1st_value = skills_spinner.getSelectedItem().toString();
                            Log.d("spinner_Value1:", spinner_1st_value);

                            main_cat_id=category_id.get(position);
                            Log.d("Id_load",main_cat_id);

                            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                            View mView = getLayoutInflater().inflate(R.layout.loader, null);
                            mBuilder.setView(mView);
                            p = mBuilder.create();
                            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            p.setCancelable(false);
                            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            p.setCanceledOnTouchOutside(false);
                            p.show();

                            loadCheckboxes(main_cat_id);

                            linear_checkbox.removeAllViews();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }*/
            }// end on Response
            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
                Log.i("Load Category Failure:",t.getMessage());
            }
        });
    }

    private void loadCheckboxes(String fuel_ids_value)
    {
        System.out.println("language_type:"+sessionManager.getlanguage());

        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Get_Set>> call=a.sub_category_list(fuel_ids_value,sessionManager.getlanguage());
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("response",response.toString());
                Log.d("response2",response.body().toString());

//                p.dismiss();
                checkbox_list=new ArrayList<>();


                linear_checkbox.removeAllViews();
                linear_checkbox.setOrientation(LinearLayout.VERTICAL);
                for(int i=0;i<response.body().size();i++)
                {
                    CheckBox cb = new CheckBox(getActivity());
                    //   linear_checkbox.removeView(cb);
                    Log.d("category_name",response.body().get(i).getSub_category());
                    cb.setText(response.body().get(i).getSub_category());
                    cb.setTextSize(18);
                    cb.setTextColor(Color.BLACK);


                    int finalI = i;

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                        {
                            if(b)
                            {
                                cb_value=response.body().get(finalI).getSub_category_id();
                                checkbox_list.add(cb_value);
                                Log.d("VALUEIB",cb_value);
                                chek_seperate_value= StringUtil.join(checkbox_list,",");
                                Log.d("VALUEIB22",chek_seperate_value);
                            }
                            else
                            {
                                checkbox_list.remove(response.body().get(finalI).getSub_category_id());
                                chek_seperate_value= StringUtil.join(checkbox_list,",");
                                Log.d("VALUEIB22remove",chek_seperate_value);
                            }
                        }
                    });

                    linear_checkbox.addView(cb);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {

                p.dismiss();
                Log.d("error",t.getMessage());
            }
        });
    } // end load Checkoboxes


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 4) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                image_experiance_.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);

               /* Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Vendor_Registration)getActivity()).image_experiance_);*/
            }
            else if (requestCode == 1) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(getActivity(),selectedImage));
                    System.out.println("gallery:"+mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    image_experiance_.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            globalVariables.setVenExperImg_Url(mPhotoFile);
        }
    }


    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMREA_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //    Toast.makeText(getActivity(), "Permission granted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(getActivity(), "Permission is denied!", Toast.LENGTH_SHORT).show();
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.CAMERA );
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
        builder.setNegativeButton(getResources().getString(R.string.cancel_changes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
}
