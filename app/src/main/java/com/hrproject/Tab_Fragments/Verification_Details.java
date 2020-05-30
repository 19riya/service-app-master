package com.hrproject.Tab_Fragments;

import android.Manifest;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hrproject.Activities.Otp_Verify;
import com.hrproject.Activities.Registration_Activity;
import com.hrproject.Activities.Vendor.Vendor_Registration;
import com.hrproject.Activities.user.User_Login;
import com.hrproject.BuildConfig;
import com.hrproject.Fragments.Home_User;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Verification_Details extends Fragment
{

    FileUtils fileUils;

    Spinner select_type;
    String Simage,Simage2;
    String desc="0";
    TextInputLayout luserDesLayout;
    GlobalVariables globalVariables;

    String id_value_spin;
    FloatingActionButton previous_tab,btn_save_details;

    AlertDialog p;
    private int CAMREA_CODE=1;
    File mPhotoFile;
    File mPhotoFile2;
    Context context;
    TextInputEditText userDesLayout;
    ImageView image_1,image_2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_verification__details, container, false);

        context=getActivity();

        userDesLayout=v.findViewById(R.id.userDesLayout);
        fileUils=new FileUtils();


        select_type=v.findViewById(R.id.spiner_user);
        image_1=v.findViewById(R.id.image_1);
        image_2=v.findViewById(R.id.image_2);

        luserDesLayout=v.findViewById(R.id.layoutuserDesLayout);
        globalVariables=GlobalVariables.getInstance();
        btn_save_details= v.findViewById(R.id.btn_save_);
        previous_tab=v.findViewById(R.id.previous_tab);

        ArrayList<String> list=new ArrayList<>();

        list.add(getResources().getString(R.string.err_idType));
        list.add(getResources().getString(R.string.adhaar));
        list.add(getResources().getString(R.string.driving));
        list.add(getResources().getString(R.string.passport));


        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_type.setAdapter(adapter);

        select_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                  id_value_spin = select_type.getSelectedItem().toString();
                Log.d("spinner_Value1:", id_value_spin);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        previous_tab.setOnClickListener(new View.OnClickListener()
        {   @Override
            public void onClick(View view) {

                ((Registration_Activity)getActivity()).tabLayoutRegister.getTabAt(0).select();
                System.out.println("first_name:"+globalVariables.getName());

                // ((Registration_Activity)getActivity()).viewpagerRegister.setCurrentItem(0);
            }
        });

        image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (permissionAlreadyGranted()) {
                    selectImage();
                    //       Toast.makeText(getActivity(), "Permission is already granted!", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestPermission();

            }
        });

        image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted()) {
                    selectImage2();
                    //       Toast.makeText(getActivity(), "Permission is already granted!", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestPermission();
            }
        });

        btn_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkNet();

            }
        });

        return  v;
    }// end onceraeCreatview

    private void onLoginFailed()
    {
    }

    public boolean validate()
    {
        boolean valid = true;

        if(Simage==null)
        {
            Snackbar snackbar = Snackbar.make(getView(), getResources().getString(R.string.err_front_image),
                    Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.action), null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();

            valid=false;
        }

        if(Simage2==null)
        {
            Snackbar snackbar = Snackbar.make(getView(), getResources().getString(R.string.err_back_image)
                    ,Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.action), null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
            valid=false;
        }

         if (userDesLayout.getText().toString()!=null){
             desc=userDesLayout.getText().toString();

             globalVariables.setUser_descrp(desc);
         }
       /* if( (desc.isEmpty()) || (desc.trim().length()<4))
        {
            luserDesLayout.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_user_descr)+"</font>"));
            valid = false;
        }
        else
        {
            globalVariables.setUser_descrp(desc);
            luserDesLayout.setError(null);
        }
*/
        if(id_value_spin.equalsIgnoreCase(getResources().getString(R.string.err_idType)))
        {
            Snackbar snackbar = Snackbar.make(getView(), getResources().getString(R.string.err_idType),
                Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.action), null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
            valid=false;
        }

        return valid;
    } // end Validate MEthod

    private void checkNet() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            if (!validate()) {
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

                data_RegisterJson();
            }
        } else {
            SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
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

    public void selectImage()
    {
        LinearLayout camerall,galleryll;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.picimage, null);

        // Typeface typeface=Typeface.createFromAsset(getActivity(), "fontawesome-webfont.ttf");

        final ImageView close= dialogView.findViewById(R.id.iconclose);

        camerall = dialogView.findViewById(R.id.camerall);
        galleryll= dialogView.findViewById(R.id.galleryll);

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
                        photoFile = fileUils.createImageFile(getActivity());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                        System.out.println("photo_URI:"+photoURI);
                      //  Simage=""+photoURI;
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
/*                Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallaryIntent, 1);*/

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

    public void selectImage2(){
        LinearLayout camerall,galleryll;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = fileUils.createImageFile(getActivity());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                        System.out.println("photo_URI:"+photoURI);
                   //     Simage2=""+photoURI;
                        mPhotoFile2 = photoFile;
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 10);
                    }
                }
            }
        });

        galleryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallaryIntent, 11);*/
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pickPhoto, 11);

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
                image_1.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);

            }
            else if (requestCode == 1) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(fileUils.getRealPathFromUri(getActivity(), selectedImage));
                    System.out.println("gallery:" + mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    image_1.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

  /*              Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Registration_Activity)getActivity()).image_1);
*/
            }

            globalVariables.setFrontImg_Url(mPhotoFile);
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile2), null, options);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                image_2.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage2 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage2);

            }
            else if (requestCode == 11) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile2 = new File(fileUils.getRealPathFromUri(getActivity(), selectedImage));
                    System.out.println("gallery:" + mPhotoFile2);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile2), null, options);
                    image_2.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage2 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            globalVariables.setBackImg_Url(mPhotoFile2);
        }

    }

    private void data_RegisterJson()
    {
        globalVariables.setUser_descrp(desc);
        System.out.println("profile_image:"+Simage);
        System.out.println("profile_image1:"+Simage2);
        System.out.println("profile_image2:"+globalVariables.getUser_image());

        ApiInterface a= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Get_Set>> call=a.getUser_registration
                (
                globalVariables.getName(),
                globalVariables.getEmail(),
                globalVariables.getMobile(),
                globalVariables.getDob(),
                globalVariables.getPassword(),
                globalVariables.getAddres1(),
                globalVariables.getAddres2(),
                desc, id_value_spin,Simage,Simage2,
                globalVariables.getUser_image()
                );


        call.enqueue(new Callback<ArrayList<Get_Set>>()
        {
            @Override
            public void onResponse(Call<ArrayList<Get_Set>> call, Response<ArrayList<Get_Set>> response)
            {
                Log.d("url_st",response.toString());
                Log.d("url_st11",response.body().toString());

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
                            Intent i=new Intent(getActivity(), User_Login.class);
                            startActivity(i);
                            ((Registration_Activity)getActivity()).finish();
                            sweetAlertDialog.dismissWithAnimation(); }
                    });
                    ff.show();

                }
                else
                {
                    p.dismiss();
                    SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE);
                    ff.setTitleText(getResources().getString(R.string.failed));
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
                p.dismiss();
                Log.i("FAILURE ERROR::",t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globalVariables.getFrontImg_Url() !=null){

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getFrontImg_Url()), null, options);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image_1.setImageBitmap(bitmap);
        }

        if (globalVariables.getBackImg_Url()!=null){

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getBackImg_Url()), null, options);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image_2.setImageBitmap(bitmap);
        }

        if (globalVariables.getUser_descrp() !="0"){
            userDesLayout.setText(globalVariables.getUser_descrp());
        }

    }
}
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ConnectionDetector cd = new ConnectionDetector(getActivity());

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();
                image_1.setImageURI(image);

                Bitmap imageB = ((BitmapDrawable) image_1.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is",Simage);


                if (cd.isConnectingToInternet()) {
                    //sendImage(Simage);
                } else {
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText(getResources().getString(R.string.failed))
                            .setTitleText(getResources().getString(R.string.internet_connection))
                            .show();
                }
            }
            else
            {
            }

        }
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                image_1.setImageBitmap(image);

                Bitmap imageB = ((BitmapDrawable) image_1.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);

                if (cd.isConnectingToInternet()) {
                    //  sendImage(Simage);
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText(getResources().getString(R.string.failed))
                            .setTitleText(getResources().getString(R.string.internet_connection))
                            .show();
                }
            }
        }

        // start 2nd image2
        if (requestCode == 11)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri image = data.getData();
                image_2.setImageURI(image);

                Bitmap imageB = ((BitmapDrawable) image_2.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage2 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is",Simage2);


                if (cd.isConnectingToInternet()) {
                    //sendImage(Simage);
                } else {
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText(getResources().getString(R.string.failed))
                            .setTitleText(getResources().getString(R.string.internet_connection))
                            .show();
                }
            }
            else
            {
            }

        }
        if (requestCode == 10)
        {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                image_2.setImageBitmap(image);

                Bitmap imageB = ((BitmapDrawable) image_2.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage2 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is",Simage2);

                if (cd.isConnectingToInternet()) {
                    //  sendImage(Simage);
                } else {
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText(getResources().getString(R.string.failed))
                            .setTitleText(getResources().getString(R.string.internet_connection))
                            .show();
                }
            }
        }
    }// end on activity Result
*/
