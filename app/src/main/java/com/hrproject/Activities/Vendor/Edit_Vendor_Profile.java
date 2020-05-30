package com.hrproject.Activities.Vendor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.hrproject.BuildConfig;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.profile_get_set;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jsoup.helper.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Edit_Vendor_Profile extends AppCompatActivity {
    UserSessionManager session;
    CircularImageView user_image;
    EditText experience,dob,address,description;
    EditText name;
    String vendor_id,Simage,vendor_name,ven_exper="0",ven_descrp="0";
    Context context;
    RelativeLayout upload_image;
    AlertDialog p;
    Button submit;
    String dob_value,status;
    DatePickerDialog mDatePicker;
    int mYear,mMonth,mDay;
    GlobalVariables globalVariables;
    LinearLayout linear_layout;
    private int CAMREA_CODE=1;

    FileUtils fileUtils;
    File mPhotoFile;

    LinearLayout linear_checkbox;
    LinearLayout skills_spinner;

    String main_cat_id;
    String chek_seperate_value="";
    Bitmap bitmap;
    ArrayList<String> category_name=new ArrayList<>();
    ArrayList<String> category_id=new ArrayList<>();
    Switch edit_long_root_status;

 //   @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_edit_profile);
        context = this;

        session = new UserSessionManager(Edit_Vendor_Profile.this);
        HashMap<String, String> data = session.getUserDetails();

        vendor_id = data.get(session.KEY_ID);
        vendor_name = data.get(session.KEY_NAME);
        System.out.println("vendor_id:::" + vendor_id);

        globalVariables = GlobalVariables.getInstance();
        experience = findViewById(R.id.evendor_experience);
        dob = findViewById(R.id.evendor_dob);
        address = findViewById(R.id.evendor_address);
        upload_image = findViewById(R.id.upload_edit_image);
        skills_spinner = findViewById(R.id.evendor_skill_category);
        linear_checkbox = findViewById(R.id.evendor_sub_category);
        description = findViewById(R.id.evendor_skill_description);
        user_image = findViewById(R.id.evendor_image);
        submit = findViewById(R.id.echange_vendor_profile);
        name = findViewById(R.id.evendor_name);
        linear_layout = findViewById(R.id.vendor_edit_layout);
        edit_long_root_status=findViewById(R.id.edit_long_root_status);

        Intent intent = getIntent();
        vendor_name = intent.getExtras().getString("name");
        name.setText(vendor_name);
        dob.setText(getIntent().getExtras().getString("dob"));
        address.setText(getIntent().getExtras().getString("address"));

       // description.setText(getIntent().getExtras().getString("descrp"));

        if (getIntent().getExtras().getString("descrp")=="0"){
            ven_descrp="0";
            description.setText("");
        }
        else {
            ven_descrp=getIntent().getExtras().getString("descrp");
            description.setText(getIntent().getExtras().getString("descrp"));
        }


        if (getIntent().getExtras().getString("experience")=="0"){
            ven_exper="0";
            experience.setText("");
        }
        else {
            ven_exper=getIntent().getExtras().getString("experience");
            experience.setText(getIntent().getExtras().getString("experience"));
        }

        if (getIntent().getExtras().getString("long_status").equals("1")){
            edit_long_root_status.setChecked(true);
            status="1";
        }
        else {
            edit_long_root_status.setChecked(false);
            status="0";
        }

        String image = getIntent().getExtras().getString("profile_image");
        System.out.println("image:" + image);

        Picasso.with(Edit_Vendor_Profile.this).load(image)
                .placeholder(R.drawable.man).into(user_image);



        edit_long_root_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (edit_long_root_status.isChecked()){
                    status="1";
                    System.out.println("check:"+status);
                }
                else {
                    status="0";
                    System.out.println("check:"+status);
                }

            }
        });

        Thread thread=new Thread(new Runnable() {
       @Override
       public void run() {
           URL url = null;
           try {
               url = new URL(image);
           } catch (MalformedURLException e) {
               e.printStackTrace();
           }

           try {
               bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
           } catch (IOException e) {
               e.printStackTrace();
           }
           System.out.println("bitmap:" + bitmap);

           Simage = stringtoBitmap(bitmap);
         //  System.out.println("image is" + Simage);
       }
   });

   thread.start();

            fileUtils = new FileUtils();

            Toolbar toolbar = findViewById(R.id.tool_ven_edit_profile);
            toolbar.setTitle(getResources().getString(R.string.tool_edit));
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

            dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickDate(dob);
                }
            });

            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (permissionAlreadyGranted()) {
                        selectImage();
                        return;
                    }

                    requestPermission();

                }
            });

           checknet();

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check_details();
                }
            });


    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(context);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            loadCategory();
        } else {
            SweetAlertDialog ff = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
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

    }

    private String convertFiletoString(File mPhotoFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File photoFile = null;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            System.out.println("photo_URI:" + photoURI);
            //  Simage=" "+photoURI;
            mPhotoFile = photoFile;
            System.out.println("camera:" + mPhotoFile);

        }
//        user_image.setImageBitmap(bitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

     //   Log.i("S image is", Simage);
    return Simage;
    }


    private String stringtoBitmap(Bitmap bitmap1)
    {
      //  try {
            System.out.println("check:" + "try");


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        //    System.out.println("S image is" + Simage);
            return Simage;

       /*
        }
        catch (Exception e)
        {
            System.out.println("check:"+"catch");
            e.getMessage();
            return null;
        }*/


    }

    private void loadCategory()
    {
        System.out.println("Language_type:"+session.getlanguage());
        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Get_Set>> call=a.category_list(session.getlanguage());
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("response_spi",response.toString());
                Log.d("response2_spin",response.body().toString());
                if(response.body().size()>0)
                {
                    ArrayList<String> checkbox_list1=new ArrayList<>();

                    skills_spinner.setOrientation(LinearLayout.VERTICAL);

                    for(int i=0;i<response.body().size();i++)
                    {
                        CheckBox cb = new CheckBox(Edit_Vendor_Profile.this);
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

                                    /*chek_seperate_value= StringUtil.join(checkbox_list,",");
                                    System.out.println("subCategory1"+chek_seperate_value);
                                    System.out.println("subCategory"+cb_value);*/
                                    loadCheckboxes(main_cat_id);
                                    //   linear_checkbox.removeAllViews();
                                }

                            }
                        });
                        skills_spinner.addView(cb);
                       /* if (i%2==0){
                            System.out.println("check:"+i);
                            skills_spinner.setOrientation(LinearLayout.VERTICAL);
                            skills_spinner.addView(cb);
                        }
                        else
                        {
                            System.out.println("check:"+i);
                            skills_spinner.setOrientation(LinearLayout.HORIZONTAL);
                            skills_spinner.addView(cb);
                        }*/

                    }





                  /*  for(int i=0;i<response.body().size();i++)
                    {
                        category_name.add(response.body().get(i).getCategory_name());
                        category_id.add(response.body().get(i).getCategory_id());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(Edit_Vendor_Profile.this, android.R.layout.simple_spinner_item, category_name);
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

                            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Edit_Vendor_Profile.this);
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
                    });*/
                }
            }// end on Response
            @Override
            public void onFailure(Call<ArrayList<Get_Set>> call, Throwable t)
            {
            }
        });
    }

    private void loadCheckboxes(String fuel_ids_value)
    {
        System.out.println("language_type:"+session.getlanguage());

        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ArrayList<Get_Set>> call=a.sub_category_list(fuel_ids_value,session.getlanguage());
        call.enqueue(new Callback<ArrayList<Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("response",response.toString());
                Log.d("response2",response.body().toString());

              //  p.dismiss();

                linear_checkbox.removeAllViews();
                ArrayList<String> checkbox_list=new ArrayList<>();

                linear_checkbox.setOrientation(LinearLayout.VERTICAL);
                for(int i=0;i<response.body().size();i++)
                {
                    CheckBox cb = new CheckBox(Edit_Vendor_Profile.this);
                    System.out.println("category_name"+response.body().get(i).getSub_category());
                    cb.setText(response.body().get(i).getSub_category());
                    cb.setTextSize(18);
                    cb.setTextColor(Color.BLACK);


                    int finalI = i;

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                        {
                            if(b==true)
                            {
                                String cb_value=response.body().get(finalI).getSub_category_id();
                                checkbox_list.add(cb_value);
                                Log.d("VALUEIB",cb_value);
                                chek_seperate_value= StringUtil.join(checkbox_list,",");
                                Log.d("VALUEIB22",chek_seperate_value);
                            }
                            else
                            {
                                //        checkbox_list.remove(finalI);
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


    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(Edit_Vendor_Profile.this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Edit_Vendor_Profile.this, Manifest.permission.CAMERA)) {

        }
        ActivityCompat.requestPermissions(Edit_Vendor_Profile.this, new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Vendor_Profile.this);
        builder.setTitle(getResources().getString(R.string.required_permission));
        builder.setMessage(getResources().getString(R.string.permission_explain));
        builder.setPositiveButton(getResources().getString(R.string.to_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
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

    public void pickDate(final TextView dialog)
    {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mDatePicker = new DatePickerDialog(Edit_Vendor_Profile.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                selectedmonth = selectedmonth + 1;
                dialog.setText( selectedyear + "-" + selectedmonth + "-" + selectedday);

                mDay= selectedday;
                mMonth= selectedmonth;
                mYear= selectedyear;

            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDatePicker.setTitle(getResources().getString(R.string.title_birth));
        mDatePicker.show();

    }

    public void selectImage(){
        LinearLayout camerall,galleryll;

        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Vendor_Profile.this);

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.picimage, null);

        // Typeface typeface=Typeface.createFromAsset(getActivity(), "fontawesome-webfont.ttf");

        final ImageView close=(ImageView) dialogView.findViewById(R.id.iconclose);


        camerall = (LinearLayout) dialogView.findViewById(R.id.camerall);
        galleryll= (LinearLayout) dialogView.findViewById(R.id.galleryll);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(600, 400);
        dialog.show();

        camerall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = fileUtils.createImageFile(context);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                        System.out.println("photo_URI:" + photoURI);
                        //  Simage=" "+photoURI;
                        mPhotoFile = photoFile;
                        System.out.println("camera:" + mPhotoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 4);
                    }
                }


                //startActivityForResult(cameraIntent, 4);
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

                /*Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallaryIntent, 1);*/
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 1) {
                Uri selectedImage = data.getData();
                System.out.println("path:"+selectedImage);
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(context,selectedImage));
                    System.out.println("gallery:"+mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    user_image.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                //   Log.i("S image is", Simage);

                } catch (Exception e) {
                    e.printStackTrace();
                }


              /*  Uri image = data.getData();
                user_image.setImageURI(image);

                Bitmap imageB = ((BitmapDrawable) user_image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);*/
            }

                if (requestCode == 4) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    user_image.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                //    Log.i("S image is", Simage);


               /* Bitmap image = (Bitmap) data.getExtras().get("data");
                user_image.setImageBitmap(image);

                Bitmap imageB = ((BitmapDrawable) user_image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);*/
            }
        }
    }


    private void check_details() {
        ConnectionDetector cd = new ConnectionDetector(Edit_Vendor_Profile.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            if(!validate()) {
                onLoginFailed();
                return;
            }
            else {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                        (Edit_Vendor_Profile.this);
                View mView = getLayoutInflater().inflate(R.layout.loader, null);
                mBuilder.setView(mView);
                p = mBuilder.create();
                p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                p.setCancelable(false);
                p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                p.setCanceledOnTouchOutside(false);
                p.show();

                data_set();
            }
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(Edit_Vendor_Profile.this,SweetAlertDialog.WARNING_TYPE);
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

    public boolean validate(){
        boolean valid=true;

        ven_descrp=description.getText().toString();
        ven_exper=experience.getText().toString();

        if (name.getText().toString().length()==0){
            name.setError(getResources().getString(R.string.err_ven_name));
            valid=false;
        }
        else
            name.setError(null);

        dob_value=dob.getText().toString();

        if (dob_value.isEmpty()){
            dob.setError(getResources().getString(R.string.err_birth));
            valid=false;
        }
        else
            dob.setError(null);

        if (address.getText().toString().length()==0){
            address.setError(getResources().getString(R.string.err_address));
            valid=false;
        }
        else
            address.setError(null);

      /*  if (description.getText().toString().length()==0){
            description.setError(getResources().getString(R.string.err_descr));
            valid=false;
        }
        else
            description.setError(null);*/

       /* if (skill_category.getText().toString().length()==0){
            skill_category.setError(getResources().getString(R.string.err_skill));
            valid=false;
        }
        else
            skill_category.setError(null);

        if (sub_category.getText().length()==0){
            sub_category.setError(getResources().getString(R.string.err_subskill));
            valid=false;
        }
        else
            sub_category.setError(null);
*/
          /*  if (experience.getText().toString().length()==0){
                experience.setError(getResources().getString(R.string.err_exper));
                valid=false;
            }
            else
                experience.setError(null);*/

            if (chek_seperate_value.length()==0){
                Snackbar snackbar=Snackbar.make(linear_layout,getResources().getString(R.string.select_subCatg),Snackbar.LENGTH_LONG);
                snackbar.show();
                valid=false;
            }


        return valid;

    }

    private void data_set() {
        System.out.println("date_birth:"+dob_value);

        if (Simage!=null){
           globalVariables.setVendor_name(vendor_name);
           globalVariables.setVendor_dob(dob_value);
           globalVariables.setVendor_address(address.getText().toString());
           globalVariables.setVendor_description(ven_descrp);
           globalVariables.setExperience(ven_exper);
           globalVariables.setVendor_image(Simage);
           globalVariables.setLong_status(status);

            EDIT_VENDOR_PROFILE();
        }
        else
        {
            Snackbar snackbar=Snackbar.make(linear_layout,getResources().getString(R.string.upload_image),Snackbar.LENGTH_LONG);
            snackbar.show();
            p.dismiss();
        }
    }

    private void EDIT_VENDOR_PROFILE() {
        System.out.println("vendor_id"+vendor_id);
        System.out.println("category_id"+main_cat_id);
        System.out.println("sub_category_id"+chek_seperate_value);
       // System.out.println("vendor_image"+globalVariables.getVendor_image());
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<profile_get_set>> call=apiInterface.update_vendor_profile(vendor_id,
                globalVariables.getVendor_name(),
                globalVariables.getVendor_dob(),
                globalVariables.getVendor_address(),
                globalVariables.getVendor_image(),
                main_cat_id,
                chek_seperate_value,
                globalVariables.getVendor_description(),
                globalVariables.getExperience(),globalVariables.getLong_status());

        call.enqueue(new Callback<ArrayList<profile_get_set>>() {
            @Override
            public void onResponse(Call<ArrayList<profile_get_set>> call, Response<ArrayList<profile_get_set>> response) {
                Log.d("URL...",response.toString());

                Log.d("new URL....",response.body().toString());

                p.dismiss();
                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    Intent intent=new Intent(Edit_Vendor_Profile.this,Vendor_Show_Profile.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Snackbar snackbar=Snackbar.make(linear_layout,""+response.body().get(0).getMsg(),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<profile_get_set>> call, Throwable t)
            {
                p.dismiss();
                Log.i("FAILUR_ERROR::::",t.getMessage());
            }
        });
    }

    private void onLoginFailed() {
    }
}

