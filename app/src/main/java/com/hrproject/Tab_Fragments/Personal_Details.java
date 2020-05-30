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

import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Personal_Details extends Fragment {

    RelativeLayout upload_image_layout;

    FileUtils fileUtils;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;


    FloatingActionButton saveDetails;

    DatePickerDialog mDatePicker;
    int mYear,mMonth,mDay;
    Context context;

    CoordinatorLayout layout;
    AlertDialog p;
    String Simage;
    File mPhotoFile;

    String fname,dob_value,email_value,phone,pass,cpass,add1,add2="0";
    TextInputLayout lname,lemail,lphone,lpass,ladd1,ladd2,cPassword,ldob;
    TextInputEditText firstName,dob,email,phoneNumber,password,address1,address2;
    CircularImageView user_image;


    //    String Simage;
    EditText dateBirth;
    private int CAMREA_CODE=1;

    public Personal_Details() {
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
        mDatePicker.setTitle(getResources().getString(R.string.title_birth));
        mDatePicker.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_personal__details, container, false);

        context=getActivity();
        fileUtils=new FileUtils();
        dateBirth=v.findViewById(R.id.dateBirth);

        upload_image_layout=v.findViewById(R.id.upload_image_layout);
        user_image=v.findViewById(R.id.userImage_update);
        layout=v.findViewById(R.id.user_layout);
        lname=v.findViewById(R.id.layoutfirstName);

        lemail=v.findViewById(R.id.layoutemail);
        ladd2=v.findViewById(R.id.layoutaddress2);
        ladd1=v.findViewById(R.id.layoutaddress1);
        lpass=v.findViewById(R.id.layoutpassword);
        lphone=v.findViewById(R.id.layoutphoneNumber);
        ldob=v.findViewById(R.id.dob);

        upload_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted()) {
                        selectImage();
                        return;
                    }

                    requestPermission();

            }
        });

        firstName=v.findViewById(R.id.firstName);
        dob=v.findViewById(R.id.dateBirth);
        email=v.findViewById(R.id.email);
        phoneNumber=v.findViewById(R.id.phoneNumber);
        password=v.findViewById(R.id.password);
        cPassword=v.findViewById(R.id.cPassword);
        address1=v.findViewById(R.id.address1);
        address2=v.findViewById(R.id.address2);
        saveDetails=v.findViewById(R.id.btn_save_);

        dateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(dateBirth);
            }
        });

        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkNet();
            }
        });

        return v;
    }// End onCreateView

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
        dob_value =  dob.getText().toString();
        email_value =  email.getText().toString();
        phone =  phoneNumber.getText().toString();
        pass =  password.getText().toString();
        cpass =  cPassword.getEditText().getText().toString();
        add1 = address1.getText().toString();
        add2 = address2.getText().toString();

        if (fname.isEmpty() ||(fname.trim().length()==0) || (fname.length()<3) )
        {
            lname.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_name)+"</font>"));
            valid = false;
        }
        else
        {
            lname.setError(null); }

        if (dob_value.isEmpty())
        {
            ldob.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_birth)+" </font>"));
            valid = false;
        }
        else
        {   ldob.setError(null); }

        if (  (email_value.isEmpty()) || (!emailValidator(email_value))) {
            lemail.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_email)+"</font>"));
            valid = false;
        }
        else
        {  lemail.setError(null); }

        if ((phone.isEmpty()) || (phone.length()!=10))
        {
            lphone.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_mobile)+"</font>"));
            valid = false;
        }
        else
        {  lphone.setError(null); }

        if (pass.isEmpty() || (pass.trim().length()<4))
        {
            lpass.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_pass)+"</font>"));
            valid = false;
        }
        else
        {  lpass.setError(null);
        }

        if (cpass.isEmpty())
        {
            cPassword.setError( Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_confirm_pass)+"</font>"));
            valid = false;
        }

        else
        {  cPassword.setError(null); }

        if ( (add1.isEmpty()) || (add1.trim().length()==0) ||(add1.length()<4) )
           {
               ladd1.setError(Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_address)+" </font>"));
         valid=false;
          }
           else
               {
                   ladd1.setError(null);
           }

       /* if ( (add2.isEmpty()) || (add2.trim().length()==0) ||(add2.length()<4) )
        {
            ladd2.setError(Html.fromHtml("<font color='#F34F3D' >"+getResources().getString(R.string.err_address2)+"</font>"));
            valid=false;
        }
        else
        {
            ladd2.setError(null);
        }*/
        return valid;
    } // end Validate MEthod

    private void data_set()
    {
        if(Simage!=null)
        {
            if (pass.equalsIgnoreCase(cpass)) {
                GlobalVariables glo = GlobalVariables.getInstance();
                Log.i("Test",fname);
                glo.setName(fname);
                glo.setDob(dob_value);
                glo.setEmail(email_value);
                glo.setMobile(phone);
                glo.setPassword(pass);
                glo.setAddres1(add1);
                glo.setAddres2(add2);
                glo.setUser_image(Simage);
                glo.setImg_Url(mPhotoFile);
                ((Registration_Activity)getActivity()).tabLayoutRegister.getTabAt(1).select();

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
                        System.out.println("photo_URI:" + photoURI);
                        //  Simage=" "+photoURI;
                        mPhotoFile = photoFile;
                        System.out.println("camera:" + mPhotoFile);
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
                startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);

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

                    bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile),
                            null, options);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                user_image.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                Log.i("S image is", Simage);

            }
            else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                System.out.println("path:"+selectedImage);
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(getActivity(),selectedImage));
                    System.out.println("gallery:"+mPhotoFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPhotoFile), null, options);
                    user_image.setImageBitmap(bitmap);

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

        if (GlobalVariables.getInstance().getImg_Url()!=null) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(GlobalVariables.getInstance().getImg_Url()),
                        null, options);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           user_image.setImageBitmap(bitmap);

            firstName.setText(GlobalVariables.getInstance().getName());
            dob.setText(GlobalVariables.getInstance().getDob());
            email.setText(GlobalVariables.getInstance().getEmail());
            phoneNumber.setText(GlobalVariables.getInstance().getMobile());
            password.setText(GlobalVariables.getInstance().getPassword());
            address1.setText(GlobalVariables.getInstance().getAddres1());
            address2.setText(GlobalVariables.getInstance().getAddres2());

        }

    }
} // end personal detaius fragment main
