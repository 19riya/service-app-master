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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.hrproject.Activities.Registration_Activity;
import com.hrproject.Activities.Vendor.Vendor_Registration;
import com.hrproject.BuildConfig;
import com.hrproject.Fragments.Home_User;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Vendor_Verification extends Fragment {
    Spinner type;
    String id_value_spin;

    FloatingActionButton previous_tab , save_next;
    String Simage,Simage2,Simage3="0",status="0";
    GlobalVariables globalVariables;
    private int CAMREA_CODE=1;
    File mPhotoFile;
    File mPhotoFile2;
    File mPhotoFile3;
    FileUtils fileUtils;
    Switch long_root_status;

    public ImageView image_front,image_back , image_police_Verify;


    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_vendor__verification, container, false);

      context=  getActivity();
        image_front=v.findViewById(R.id.image_front_vendor);
        image_back=v.findViewById(R.id.image_back_vendor);
        image_police_Verify=v.findViewById(R.id.image_police_Verify);
        long_root_status=v.findViewById(R.id.long_root_status);

        fileUtils=new FileUtils();
        globalVariables=GlobalVariables.getInstance();
        save_next=v.findViewById(R.id.verificationVendor_save);

        previous_tab= v.findViewById(R.id.previous_verification);
        previous_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                ((Vendor_Registration)getActivity()).tabLayout.getTabAt(0).select();

             //   ((Vendor_Registration)getActivity()).viewpager.setCurrentItem(0);
            }
        });

        long_root_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (long_root_status.isChecked()){
                    status="1";
                    System.out.println("check:"+status);
                }
                else {
                    status="0";
                    System.out.println("check:"+status);
                }

            }
        });



        type=v.findViewById(R.id.spiner_vendor);

        ArrayList<String> list=new ArrayList<>();
        list.add(getResources().getString(R.string.err_idType));
        list.add(getResources().getString(R.string.adhaar));
        list.add(getResources().getString(R.string.driving));
        list.add(getResources().getString(R.string.passport));

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                id_value_spin = type.getSelectedItem().toString();
                Log.d("spinner_Value1:", id_value_spin);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        image_front.setOnClickListener(new View.OnClickListener() {
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

        image_back.setOnClickListener(new View.OnClickListener() {
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

        image_police_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (permissionAlreadyGranted()) {
                    selectImage3();
                    //       Toast.makeText(getActivity(), "Permission is already granted!", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestPermission();


            }
        });

        save_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkNet();
            }
        });

        return  v;
    }// en donD=Create View

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
                data_set();
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

    public boolean validate()
    {
         boolean valid=true;

        if(Simage==null)
        {
            Snackbar snackbar = Snackbar.make(getView(), getResources().getString(R.string.err_front_image),Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.action), null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
            valid=false;
        }
        if (Simage2==null)
        {
            Snackbar snackbar = Snackbar.make(getView(), getResources().getString(R.string.err_back_image),Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.action), null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
            valid=false;
        }

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


        /*if(Simage3==null)
        {
            Snackbar snackbar = Snackbar.make(getView(), getResources().getString(R.string.err_police_verImage),Snackbar.LENGTH_LONG).setAction(R.string.action, null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
            valid=false;
        }*/


        return  valid;
    }

    public void selectImage()
    {
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

    public void selectImage3(){
        LinearLayout camerall,galleryll;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        mPhotoFile3 = photoFile;
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 100);
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
                startActivityForResult(pickPhoto, 111);
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
                image_front.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);

               /* Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Vendor_Registration)getActivity()).image_front);*/
            }
            else if (requestCode == 1) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(getActivity(), selectedImage));
                    System.out.println("gallery:" + mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    image_front.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Vendor_Registration)getActivity()).image_front);*/

                /*
                Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Registration_Activity)getActivity()).user_image);*/
            }

            globalVariables.setVenfrontImg_Url(mPhotoFile);

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
                image_back.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage2 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage2);

               /* Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Vendor_Registration)getActivity()).image_back);*/
            }
            else if (requestCode == 11) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile2 = new File(fileUtils.getRealPathFromUri(getActivity(), selectedImage));
                    System.out.println("gallery:" + mPhotoFile2);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile2), null, options);
                    image_back.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage2 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            globalVariables.setVenbackImg_Url(mPhotoFile2);
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile3), null, options);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                image_police_Verify.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage3 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage3);


               /* Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Vendor_Registration)getActivity()).image_police_Verify);*/

            }
            else if (requestCode == 111) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile3 = new File(fileUtils.getRealPathFromUri(getActivity(), selectedImage));
                    System.out.println("gallery:" + mPhotoFile3);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile3), null, options);
                    image_police_Verify.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage3 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage3);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            globalVariables.setVenPoliceImg_Url(mPhotoFile3);
        }

    }
    private void data_set()
    {
        globalVariables.setLong_status(status);
        globalVariables.setVendor_id_type(id_value_spin);
        globalVariables.setVendor_front_image(Simage);
        globalVariables.setVendor_back_image(Simage2);
        globalVariables.setVendor_police_verify(Simage3);

        if (Simage!=null && Simage2!=null )
        {
            ((Vendor_Registration)getActivity()).tabLayout.getTabAt(2).select();
        }
        else {
            Snackbar snackbar=Snackbar.make(getView(),getResources().getString(R.string.upload_image),Snackbar.LENGTH_LONG);
            snackbar.show();
        }

       // ((Vendor_Registration) getActivity()).viewpager.setCurrentItem(2);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globalVariables.getVenfrontImg_Url() !=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getVenfrontImg_Url()), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image_front.setImageBitmap(bitmap);
        }
        if (globalVariables.getVenbackImg_Url() !=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getVenbackImg_Url()), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image_back.setImageBitmap(bitmap);
        }
        if (globalVariables.getVenPoliceImg_Url() !=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getVenPoliceImg_Url()), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image_police_Verify.setImageBitmap(bitmap);
        }


    }
}
