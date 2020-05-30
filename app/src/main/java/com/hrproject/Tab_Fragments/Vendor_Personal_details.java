package com.hrproject.Tab_Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hrproject.Activities.Registration_Activity;
import com.hrproject.Activities.Vendor.Vendor_Registration;
import com.hrproject.BuildConfig;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.HelperClasses.ConnectionDetector;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Vendor_Personal_details extends Fragment {

    RelativeLayout upload_image_layout;

    TextInputEditText firstName,email,phoneNumber,password,address,vendor_dateBirth;
    public CircularImageView vendor_image;

    Context context;
    String fname,dob_value,email_value,phone,pass,cpass,add1;
    String Simage;

    GlobalVariables globalVariables;
    TextInputLayout lname,lemail,lmobile,lpass,ladd1;
    TextInputLayout cPassword;
    TextInputLayout lvendor_dateBirth;
    private int CAMREA_CODE=1;

    DatePickerDialog mDatePicker;
    int mYear,mMonth,mDay;
    CoordinatorLayout layout;
    File mPhotoFile;
    FileUtils fileUtils;

    FloatingActionButton btn_save_vendor;


    public Vendor_Personal_details() {
        // Required empty public constructor
    }
    public void pickDate(final TextView dialog)
    {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                selectedmonth = selectedmonth + 1;
                dialog.setText( selectedyear + "-" + selectedmonth + "-" + selectedday);

                mDay= selectedday;
                mMonth= selectedmonth;
                mYear= selectedyear;

            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDatePicker.setTitle(getResources().getString(R.string.reg_birth));
        mDatePicker.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_vendor__personal_details, container, false);
        context=getActivity();

        fileUtils=new FileUtils();
        upload_image_layout= v.findViewById(R.id.upload_image_layout);
        vendor_image=v.findViewById(R.id.vendorImage_update);

        vendor_dateBirth=v.findViewById(R.id.vendor_dob);
        globalVariables=GlobalVariables.getInstance();

        firstName=v.findViewById(R.id.vendorfirstName);
        layout=v.findViewById(R.id.ven_layout);
        email=v.findViewById(R.id.vendorEmail);
        phoneNumber=v.findViewById(R.id.VendorphoneNumber);
        password=v.findViewById(R.id.Vendorpassword);
        cPassword=v.findViewById(R.id.VendorConfPassword);
        address=v.findViewById(R.id.vendor_address);
        lvendor_dateBirth=v.findViewById(R.id.layoutvendor_dob);
        lname=v.findViewById(R.id.layoutvendorfirstName);
        lemail=v.findViewById(R.id.layoutvendorEmail);
        ladd1=v.findViewById(R.id.layoutvendor_address);
        lpass=v.findViewById(R.id.layoutVendorpassword);
        lmobile=v.findViewById(R.id.layoutVendorphoneNumber);

        btn_save_vendor=v.findViewById(R.id.btn_save_vendor);

        upload_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (permissionAlreadyGranted()) {
                        selectImage();
                        // Toast.makeText(getActivity(), "Permission is already granted!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    requestPermission();
                }
            }
        });

        vendor_dateBirth .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(vendor_dateBirth);
            }
        });

        btn_save_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkNet();
            }
        });

        return  v;
    }// en doncerate view


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

    public void onLoginFailed()
    {
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validate()
    {
        boolean valid = true;

        // firstName,dob,email,phoneNumber,password,cPassword,address1,address2

        fname =  firstName.getText().toString();
        dob_value =  vendor_dateBirth.getText().toString();
        email_value =  email.getText().toString();
        phone =  phoneNumber.getText().toString();
        pass =  password.getText().toString();
        cpass =  cPassword.getEditText().getText().toString();
        add1 =  address.getText().toString();

        if (fname.isEmpty() ||(fname.trim().length()==0) || (fname.length()<3) )
        {
            lname.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_ven_name)+"</font>"));
            valid = false;
        }
        else
        {   lname.setError(null); }

        if (dob_value.isEmpty())
        {
            lvendor_dateBirth.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_birth)+"</font>"));
            valid = false;
        }
        else
        {   lvendor_dateBirth.setError(null); }

        if (  (email_value.isEmpty()) || (!emailValidator(email_value))) {
            lemail.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_email)+"</font>"));
            valid = false;
        }
        else
        {   lemail.setError(null); }

        if ((phone.isEmpty()) || (phone.length()!=10))
        {
            lmobile.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_mobile)+"</font>"));
            valid = false;
        }
        else
        {   lmobile.setError(null); }



        if (pass.isEmpty() || (pass.trim().length()<4))
        {
            lpass.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_pass)+"</font>"));
            valid = false;
        }
        else
        {   lpass.setError(null);
        }

        if (cpass.isEmpty())
        {
            cPassword.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_confirm_pass)+"</font>"));
            valid = false;
        }

        else
        {   cPassword.setError(null); }



        if ( (add1.isEmpty()) || (add1.trim().length()==0) ||(add1.length()<4) )
        {
            ladd1.setError(Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_address)+"</font>"));
            valid=false;
        }
        else
        {
            ladd1.setError(null);
        }

        return valid;
    } // end Validate MEthod


    private void data_set()
    {
        //String fname,dob_value,email_value,phone,pass,cpass,add1,add2;


        if(Simage!=null)
        {
            if (pass.equalsIgnoreCase(cpass))
            {
               globalVariables.setVendor_name(fname);
               globalVariables.setVendor_dob(dob_value);
               globalVariables.setVendor_mail(email_value);
               globalVariables.setVendor_mobile(phone);
               globalVariables.setVendor_password(pass);
               globalVariables.setVendor_address(add1);
               globalVariables.setVendor_image(Simage);
               globalVariables.setVenImg_Url(mPhotoFile);


                /*Vendor_Verification fragment = new Vendor_Verification();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_registration, fragment);
                ft.commit();
*/
                ((Vendor_Registration)getActivity()).tabLayout.getTabAt(1).select();


          //      ((Vendor_Registration) getActivity()).viewpager.setCurrentItem(1);
            } else {
                Snackbar snackbar=Snackbar.make(layout,""+getResources().getString(R.string.reg_confirm_pass),Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        else
        {
            Snackbar snackbar=Snackbar.make(layout,""+getResources().getString(R.string.upload_image),Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void selectImage(){
        LinearLayout camerall,galleryll;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.picimage, null);

        // Typeface typeface=Typeface.createFromAsset(getActivity(), "fontawesome-webfont.ttf");

        final ImageView close=(ImageView) dialogView.findViewById(R.id.iconclose);

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

               vendor_image.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);

                /*Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(((Vendor_Registration)getActivity()).vendor_image);*/
            }

            else if (requestCode == 1) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(getActivity(),selectedImage));
                    System.out.println("gallery:"+mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    vendor_image.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.i("S image is", Simage);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    if (globalVariables.getVenImg_Url() !=null) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(globalVariables.getVenImg_Url()), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        vendor_image.setImageBitmap(bitmap);

        firstName.setText(globalVariables.getVendor_name());
        phoneNumber.setText(globalVariables.getVendor_mobile());
        password.setText(globalVariables.getVendor_password());
        vendor_dateBirth.setText(globalVariables.getVendor_dob());
        email.setText(globalVariables.getVendor_mail());
        address.setText(globalVariables.getVendor_address());

    }
    }
}
