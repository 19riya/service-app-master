package com.hrproject.Activities.user;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.hrproject.BuildConfig;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class Edit_User_Show_Profile extends AppCompatActivity {

    CircularImageView user_image;
    EditText description,dob,address1,address2,name;
    String user_id,Simage,user_name,user_mobile,dob_value,add2="0",descrp="0";
    Context context;
    UserSessionManager session;
    DatePickerDialog mDatePicker;
    int mYear,mMonth,mDay;
    LinearLayout linear_layout;
    GlobalVariables globalVariables;
    RelativeLayout upload_image;
    AlertDialog p;
    private int CAMREA_CODE=1;

    FileUtils fileUtils;
    File mPhotoFile;

    Bitmap bitmap;
    private LruCache<String, Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        context=this;
        user_image=findViewById(R.id.edit_user_image);
        name=findViewById(R.id.edit_name);
        dob=findViewById(R.id.edit_dob);
        upload_image=findViewById(R.id.upload_user_image);
        address1=findViewById(R.id.edit_address1);
        address2=findViewById(R.id.edit_address2);
        description=findViewById(R.id.edit_description);
        linear_layout=findViewById(R.id.user_edit_layout);

        globalVariables=GlobalVariables.getInstance();
        session=new UserSessionManager(Edit_User_Show_Profile.this);
        HashMap<String,String> data=session.getUserDetails();

        fileUtils=new FileUtils();

        user_id=data.get(session.KEY_ID);
        user_mobile=data.get(session.KEY_NAME);

        Intent intent=getIntent();
        user_name=intent.getExtras().getString("name");
        name.setText(user_name);

        System.out.println("descrp:"+getIntent().getExtras().getString("descrp"));

        address1.setText(getIntent().getExtras().getString("address1"));
        dob.setText(getIntent().getExtras().getString("dob"));
        if (getIntent().getExtras().getString("address2").isEmpty()){
            address2.setText("");
            add2="0";
        }
        else {
            add2=getIntent().getExtras().getString("address2");
            address2.setText(getIntent().getExtras().getString("address2"));
        }

        if (getIntent().getExtras().getString("descrp").isEmpty() || getIntent().getExtras().getString("descrp")=="0"){
            description.setText("");
            descrp="0";
        }
        else {
            descrp=getIntent().getExtras().getString("descrp");
            description.setText(getIntent().getExtras().getString("descrp"));
        }


       // description.setText(getIntent().getExtras().getString("descrp"));

        String get_image=getIntent().getExtras().getString("image");
        System.out.println("image_url"+Simage);

        Picasso.with(Edit_User_Show_Profile.this).load(get_image)
                .placeholder(R.drawable.man).into(user_image);
   //    user_image.se(getIntent().getExtras().getString("image"));

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(get_image);
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
                System.out.println("image is" + Simage);
            }
        });

        thread.start();

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(dob);
            }
        });

        System.out.println("image_string:"+Simage);
        Toolbar toolbar=findViewById(R.id.tool_edit_profile);
        toolbar.setTitle(getResources().getString(R.string.tool_edit));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { finish(); }
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

        findViewById(R.id.submit_change_user_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_details();
            }
        });


    }// end onCreate()

    private String stringtoBitmap(Bitmap bitmap1)
    {
        //  try {
        System.out.println("check:" + "try");


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        System.out.println("S image is" + Simage);
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



    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(Edit_User_Show_Profile.this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Edit_User_Show_Profile.this, Manifest.permission.CAMERA)) {

        }
        ActivityCompat.requestPermissions(Edit_User_Show_Profile.this, new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_User_Show_Profile.this);
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

        mDatePicker = new DatePickerDialog(Edit_User_Show_Profile.this, new DatePickerDialog.OnDateSetListener() {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_User_Show_Profile.this);

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.picimage, null);

        // Typeface typeface=Typeface.createFromAsset(getActivity(), "fontawesome-webfont.ttf");

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
              //  startActivityForResult(cameraIntent, 4);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {
                Uri selectedImage = data.getData();
                System.out.println("path:"+selectedImage);
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(Edit_User_Show_Profile.this,selectedImage));
                    System.out.println("gallery:"+mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    user_image.setImageBitmap(bitmap);

                    System.out.println("image_bitmap:"+bitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }


/*                Uri image = data.getData();
                user_image.setImageURI(image);

                Bitmap imageB = ((BitmapDrawable) user_image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);*/
            }

        //for camera
        if (requestCode == 4) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                //         addBitmapToMemoryCache(,bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            user_image.setImageBitmap(bitmap);

            System.out.println("image_bitmap:"+bitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            Log.i("S image is", Simage);

              /*  Bitmap image = (Bitmap) data.getExtras().get("data");
                System.out.println("data:"+image);
                user_image.setImageBitmap(image);

                Bitmap imageB = ((BitmapDrawable) user_image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);
*/
        }
        }

    }


    private void check_details() {
        ConnectionDetector cd = new ConnectionDetector(Edit_User_Show_Profile.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            if (!validate()) {
                onLoginFailed();
                return;
            }
            else {
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder
                        (Edit_User_Show_Profile.this);
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
                SweetAlertDialog ff=new SweetAlertDialog(Edit_User_Show_Profile.this,SweetAlertDialog.WARNING_TYPE);
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

    private void data_set() {

        if (Simage!=null){
            globalVariables.setName(user_name);
            globalVariables.setDob(dob_value);
            globalVariables.setAddres1(address1.getText().toString());
            globalVariables.setAddres2(add2);
            globalVariables.setUser_image(Simage);
            globalVariables.setUser_descrp(descrp);

            EDIT_USER_PROFILE();
        }
        else {
            p.dismiss();
            Snackbar snackbar=Snackbar.make(linear_layout,getResources().getString(R.string.upload_image),Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void EDIT_USER_PROFILE() {
        System.out.println("user_id:"+user_id);
        System.out.println("user_image:"+globalVariables.getUser_image());
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<profile_get_set>> call=apiInterface.update_user_profile(user_id,
               globalVariables.getDob(),
               globalVariables.getAddres1(),
               add2,
               descrp,
               globalVariables.getUser_image(),
               user_name);

        call.enqueue(new Callback<ArrayList<profile_get_set>>() {
            @Override

            public void onResponse(Call<ArrayList<profile_get_set>> call, Response<ArrayList<profile_get_set>> response) {

                Log.d("URL::",response.toString());
                Log.d("new URL::",response.body().toString());
                p.dismiss();

                try
                {
                   if (response.body().get(0).getError().equalsIgnoreCase("0")){
                       Intent intent=new Intent(Edit_User_Show_Profile.this, User_Show_Profile.class);
                       startActivity(intent);
                       finish();
                   }
                   else
                   {
                       String msg=response.body().get(0).getMsg();
                       Snackbar snackbar=Snackbar.make(linear_layout,""+msg,Snackbar.LENGTH_LONG);
                       snackbar.show();
                   }
               }
               catch (Exception ex){
                   Log.e("ERROR::",ex.getMessage());
               }
            }

            @Override
            public void onFailure(Call<ArrayList<profile_get_set>> call, Throwable t) {
                p.dismiss();
                Log.i("FAILURE ERROR::",t.getMessage());
            }
        });

    }

    private boolean validate() {
        boolean valid=true;
        dob_value=dob.getText().toString();
        add2=address2.getText().toString();
        descrp=description.getText().toString();

        if (name.getText().toString().length()==0){
            name.setError(getResources().getString(R.string.err_name));
            valid=false;
        }
        else
            name.setError(null);

        if (dob_value.isEmpty()){
            dob.setError(getResources().getString(R.string.err_birth));
            valid=false;
        }
        else
            dob.setError(null);

        if (address1.getText().toString().length()==0){
            address1.setError(getResources().getString(R.string.err_address));
            valid=false;
        }
        else
            address1.setError(null);

      /*  if (description.getText().toString().length()==0){
            description.setError(getResources().getString(R.string.err_user_descr));
            valid=false;
        }
        else
            description.setError(null);
*/
       /* if (address2.getText().toString().length()==0){
            address2.setError((getResources().getString(R.string.err_address2)));
            valid=false;
        }
        else address2.setError(null);*/

        return valid;
    }

    private void onLoginFailed() {
    }

}
