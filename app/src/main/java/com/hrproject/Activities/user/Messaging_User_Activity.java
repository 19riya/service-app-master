package com.hrproject.Activities.user;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hrproject.BuildConfig;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Msg_Get_Set;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.HelperClasses.FileUtils;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.hrproject.RetrofitConfig.ApiClient;
import com.hrproject.RetrofitConfig.ApiInterface;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.core.content.FileProvider.getUriForFile;

public class Messaging_User_Activity extends AppCompatActivity {
    EditText write_msg;
    ImageView send_msg,back,select_file,select_camera;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    CircularImageView vendor_image;
    TextView vendor_name;
    String notifyStatus="";
    String message="",vendor_id;
    GlobalVariables globalVariables;
    FileUtils fileUtils;
    File mPhotoFile;
    private int CAMREA_CODE=1;
    String Simage,str_ven_name="";
    static String employee_name="";
    TextView vendor_rating;
    Context context;
    static Context context1;
    String pdf;
    RequestBody status1,message1,vendor_id1,unique_id1,booking_status1;
    MultipartBody.Part fileParts;
    String imagePath="";
    File downlad_file;
    Uri photoURI;
    String unique_id,status="0",res,booking_status="";
    static  GlobalVariables sglobalVariables;
    static UserSessionManager sessionManager;
   // boolean submitPressed;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.chat_menu_user, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id=item.getItemId();

        if (id==R.id.action_settings){
            UserCompleteProcess completeProcess=new UserCompleteProcess();
            completeProcess.show(getSupportFragmentManager(),"dd");
            return true;
        }


/*        if (id==R.id.user_refresh){
           // loader();
            message="0";
            message1=RequestBody.create( MediaType.parse("multipart/form-data"), message);
            CHAT_JSON1();
        //    return true;
        }*/

        switch (item.getItemId()){
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ValidFragment")
    public static class UserCompleteProcess extends BottomSheetDialogFragment {
        TextView service_amount;
        String sstatus="";

        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View inflatedView = View.inflate(getContext(), R.layout.user_complete_process, null);
            dialog.setContentView(inflatedView);



            sglobalVariables=GlobalVariables.getInstance();

            service_amount=inflatedView.findViewById(R.id.service_amount);

            System.out.println("user_message amount_enter by vendor:"+sglobalVariables.getService_amt());
            service_amount.setText(sglobalVariables.getService_amt());

            String service_otp= "<b>" + getResources().getString(R.string.your_otp) + "</b> " + " "+
                    sglobalVariables.getService_otp();

            inflatedView.findViewById(R.id.serviceYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SweetAlertDialog ff=new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE);
                    ff.setTitleText(getResources().getString(R.string.response_thanks));
                    ff.setContentText(String.valueOf(Html.fromHtml(service_otp)));
                    ff.setCanceledOnTouchOutside(false);
                    ff.setConfirmButton(getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                            sstatus="3";
                            sglobalVariables.setService_status(sstatus);
                            System.out.println("user_message employee_name:"+employee_name);

                            sessionManager.setChatId(sglobalVariables.getUnique_id(),sstatus,employee_name);
                            SERVICE_STATUS();
                            sweetAlertDialog.dismissWithAnimation();
                            dialog.dismiss();}
                    });
                    ff.show();
                }
            });
            inflatedView.findViewById(R.id.serviceNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sstatus="5";
                    System.out.println("user_message employee_name:"+employee_name);
                    sessionManager.setChatId(sglobalVariables.getUnique_id(),sstatus,employee_name);
                    sglobalVariables.setService_status(sstatus);
                    SERVICE_STATUS();
                    dismiss();
                }
            });
        }

        private void SERVICE_STATUS() {
            System.out.println("service_status:"+sglobalVariables.getService_status());
            System.out.println("uniques_id:"+sglobalVariables.getUnique_id());

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Address_get_set>> call = apiInterface.service_status(sglobalVariables.getUnique_id()
                    ,sglobalVariables.getService_status());
            call.enqueue(new Callback<ArrayList<Address_get_set>>() {
                @Override
                public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                    Log.d("URL::", response.toString());

                    try {
                        if (response.body().get(0).getError().equalsIgnoreCase("0")) {

                            System.out.println("user_message employee_name:"+employee_name);
                            sessionManager.setChatId(sglobalVariables.getUnique_id(),"4",employee_name);
                            context1.startActivity(new Intent(context1, Rating_Review.class));
                            dismiss();
                            System.out.println("SUCESS:"+response.body().get(0).getMsg());
                        }
                    } catch (Exception ex) {
                        Log.i("SERVICE_STATUS_ERROR",ex.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {
                    Log.i("SERVICE_STATUS_FAILURE:",t.getMessage());
                }
            });
        }

    } // end bottomsheet dialog


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging__user_);

        sessionManager=new UserSessionManager(Messaging_User_Activity.this);
        context=this;
        globalVariables = GlobalVariables.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        write_msg=findViewById(R.id.write_msg);
        send_msg=findViewById(R.id.send_msg);
        recyclerView=findViewById(R.id.chat_list);
        back=findViewById(R.id.back_from_chat);
        vendor_image=findViewById(R.id.chat_person_image);
        vendor_name=findViewById(R.id.person_name);
        select_file=findViewById(R.id.select_file);
        select_camera=findViewById(R.id.select_camera);
        vendor_rating=findViewById(R.id.vendor_rating);

        fileUtils=new FileUtils();
        //   savedInstanceState.putBoolean("SUBMIT_PRESSED", submitPressed);

        /*unique_id=getIntent().getExtras().getString("unique_id");
        System.out.println("unique_id:"+unique_id);*/

        context1=Messaging_User_Activity.this;

        notifyStatus=getIntent().getExtras().getString("notifyStatus");
        System.out.println("notify_status:"+notifyStatus);

        vendor_id=globalVariables.getAccepted_vendor_id();
        System.out.println("global_vendor_id"+vendor_id);

        unique_id=globalVariables.getUnique_id();
        System.out.println("unique_id_global:"+unique_id);

        BackgrndTask task=new BackgrndTask();
        task.execute();

        unique_id1 = RequestBody.create( MediaType.parse("multipart/form-data"), unique_id);
        booking_status1 = RequestBody.create( MediaType.parse("multipart/form-data"), booking_status);
        vendor_id1 = RequestBody.create( MediaType.parse("multipart/form-data"), vendor_id);
        status1 = RequestBody.create( MediaType.parse("multipart/form-data"), status);
        message1 = RequestBody.create( MediaType.parse("multipart/form-data"), "0");

        CHAT_JSON1();

        if (notifyStatus.equalsIgnoreCase("2")){
            System.out.println("amount payble unique_id:"+globalVariables.getUnique_id());

            unique_id=globalVariables.getUnique_id();
            UserCompleteProcess completeProcess=new UserCompleteProcess();
            completeProcess.show(getSupportFragmentManager(),"dd");
        }
        else {

        }

        findViewById(R.id.user_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message="0";
                message1=RequestBody.create( MediaType.parse("multipart/form-data"), message);
                CHAT_JSON1();
            }
        });

        write_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (write_msg.length()>0){
                    System.out.println("write_msg:"+write_msg.getText().toString());
                    message=write_msg.getText().toString();
                }
                System.out.println("message:"+message);
            }
        });

        System.out.println("message:"+message);

        send_msg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                status ="0";
                status1 = RequestBody.create( MediaType.parse("multipart/form-data"), status);
                message1=RequestBody.create( MediaType.parse("multipart/form-data"), message);
                System.out.println("message1:"+message1);
                CHAT_JSON1();
                write_msg.setText("");

            }
        });

        select_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted()) {
                    selectImage();
                    return;
                }
                requestPermission();

            }
        });

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted1()){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 1);
                    return;
                }
                requestPermission1();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

    }

    // for sending files
    private void CHAT_JSON() {
        System.out.println("vendor_id:"+vendor_id);
        System.out.println("message1:"+fileParts);
        System.out.println("vendor_id1:"+vendor_id1);
        System.out.println("status:"+status);
        System.out.println("status1:"+status1);
        System.out.println("unique_id1:"+unique_id);
        System.out.println("booking_status:"+booking_status);
        System.out.println("booking_status1:"+booking_status1);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Msg_Get_Set>> call=apiInterface.notification_vendor_chat_service(vendor_id1,fileParts
                ,status1,unique_id1,booking_status1);
        call.enqueue(new Callback<ArrayList<Msg_Get_Set>>() {

            @Override
            public void onResponse(Call<ArrayList<Msg_Get_Set>> call, Response<ArrayList<Msg_Get_Set>> response) {
                Log.d("URL:",response.toString());

                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    System.out.println("welcome"+ "to chat");
                    if (response.body().size()>0){
                        Picasso.with(Messaging_User_Activity.this).load(response.body().get(0).getProfile_image())
                                .placeholder(R.drawable.man).into(vendor_image);

                        vendor_name.setText(response.body().get(0).getUser_name());
                        str_ven_name=response.body().get(0).getUser_name();


                        ArrayList<Offers_get_set> msg_data = new ArrayList<>();

                        msg_data=response.body().get(0).getUser_msg();
                        System.out.println("response_msg:"+msg_data);

                        if (msg_data.size()>0) {
                         //   message="0";
                            Messaging_List_Adapter adapter = new Messaging_List_Adapter(Messaging_User_Activity.this, msg_data);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Msg_Get_Set>> call, Throwable t) {
                Log.d("FAILURE_ERROR:",t.getMessage());
            }
        });
    }

    // for text message
    private void CHAT_JSON1() {
        System.out.println("message:"+message);
        System.out.println("vendor_id:"+vendor_id);
        System.out.println("message1:"+message1);
        System.out.println("vendor_id1:"+vendor_id1);
        System.out.println("status:"+status);
        System.out.println("status1:"+status1);
        System.out.println("unique_id1:"+unique_id);
        System.out.println("booking_status:"+booking_status);
        System.out.println("booking_status1:"+booking_status1);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Msg_Get_Set>> call=apiInterface.notification_vendor_chat_service1(vendor_id1
                ,message1,status1,unique_id1,booking_status1);
        call.enqueue(new Callback<ArrayList<Msg_Get_Set>>() {

            @Override
            public void onResponse(Call<ArrayList<Msg_Get_Set>> call, Response<ArrayList<Msg_Get_Set>> response) {
                Log.d("URL:",response.toString());

                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    System.out.println("welcome"+ "to chat");
                    if (response.body().size()>0){
                        Picasso.with(Messaging_User_Activity.this).load(response.body().get(0).getProfile_image())
                                .placeholder(R.drawable.man).into(vendor_image);

                        globalVariables.setVendor_name(response.body().get(0).getUser_name());

                        vendor_name.setText(response.body().get(0).getUser_name());

                        employee_name=response.body().get(0).getUser_name();
                        str_ven_name=response.body().get(0).getUser_name();

                        System.out.println("ratings:"+response.body().get(0).getRating());

                        vendor_rating.setText(""+response.body().get(0).getRating());


                        ArrayList<Offers_get_set> msg_data = new ArrayList<>();

                        msg_data=response.body().get(0).getUser_msg();
                        System.out.println("response_msg:"+msg_data);

                        if (msg_data.size()>0) {
                            message="0";
                            //  ArrayList<Msg_Get_Set> datalist = new ArrayList<>(response.body());
                            Messaging_List_Adapter adapter = new Messaging_List_Adapter(Messaging_User_Activity.this, msg_data);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Msg_Get_Set>> call, Throwable t) {
                Log.d("FAILURE_ERROR:",t.getMessage());
            }
        });
    }

    public class Messaging_List_Adapter extends RecyclerView.Adapter<Messaging_List_Adapter.MyViewHolder>
    {
        Context c;
        ArrayList<Offers_get_set> data;

        public Messaging_List_Adapter(Messaging_User_Activity item_accept_reject_list, ArrayList<Offers_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public Messaging_List_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_messaging__list, viewGroup, false);

            return new Messaging_List_Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final Messaging_List_Adapter.MyViewHolder myViewHolder, final int i) {
            String mimeType;

            if (data.size()>0){
                String path=data.get(i).getMessage();
                File file = new File(path);
                System.out.println("file:"+file);

                Uri uri=Uri.fromFile(file);
                System.out.println("file:"+file);

                String ext=file.getName().substring(file.getName().indexOf(".")+1);
                System.out.println("ext:"+ext);

                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ext.equalsIgnoreCase("docx")
                                ||ext.equalsIgnoreCase("pdf")
                                ||ext.equalsIgnoreCase("txt")
                                ||ext.equalsIgnoreCase("jpg")
                                ||ext.equalsIgnoreCase("jpeg")
                                ||ext.equalsIgnoreCase("png"))
                        {

                            downlad_file=new File(Environment.getExternalStorageDirectory(),file.getName());
                            Log.i("downlad_file", downlad_file.toString());

                            Uri contentUri = getUriForFile(Messaging_User_Activity.this, BuildConfig.APPLICATION_ID + ".provider", downlad_file);
                            System.out.println("contentUri:"+contentUri);

                            if(downlad_file.exists()){
                                System.out.println("check:"+"exist");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(Uri.parse(contentUri.toString()), null);
                                startActivity(intent);
                            }
                            else
                            {
                                new DownloadFile().execute(path);
                            }
                        }
                    }
                });


                System.out.println("type:"+data.get(i).getType());
                if(data.get(i).getType().equalsIgnoreCase("user")){
                    if (data.get(i).getMessage().equals("")){
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.GONE);
                    }
                    else if (ext.equalsIgnoreCase("docx")
                            ||ext.equalsIgnoreCase("pdf")
                            ||ext.equalsIgnoreCase("txt")
                            ||ext.equalsIgnoreCase("jpg")
                            ||ext.equalsIgnoreCase("jpeg")
                            ||ext.equalsIgnoreCase("png")){
                        myViewHolder.vendor_message.setVisibility(View.VISIBLE);
                        myViewHolder.user_message.setVisibility(View.GONE);
                        myViewHolder.vendor_message.setText(file.getName());

                    }
                    else {
                        myViewHolder.vendor_message.setVisibility(View.VISIBLE);
                        myViewHolder.user_message.setVisibility(View.GONE);
                        myViewHolder.vendor_message.setText(data.get(i).getMessage());
                        System.out.println("vendor_message:"+data.get(i).getMessage());
                    }

                }

                else if (data.get(i).getType().equalsIgnoreCase("vendor")){
                    if (data.get(i).getMessage().equals("")){
                        myViewHolder.user_message.setVisibility(View.GONE);
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                    }
                    else if (ext.equalsIgnoreCase("docx")
                            ||ext.equalsIgnoreCase("pdf")
                            ||ext.equalsIgnoreCase("txt")
                            ||ext.equalsIgnoreCase("jpg")
                            ||ext.equalsIgnoreCase("jpeg")
                            ||ext.equalsIgnoreCase("png")){
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.VISIBLE);
                        myViewHolder.user_message.setText(file.getName());
                    }
                    else {
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.VISIBLE);
                        myViewHolder.user_message.setText(data.get(i).getMessage());
                        System.out.println("user_message:"+data.get(i).getMessage());
                    }
                }
            }

        }

        @Override
        public int getItemCount()
        {
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
            public TextView vendor_message,user_message;


            public MyViewHolder(View view) {
                super(view);
                vendor_message=view.findViewById(R.id.vendor_message);
                user_message=view.findViewById(R.id.user_message);

            }
        }
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(Messaging_User_Activity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                //  fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator;
                //  folder = Environment.getExternalStorageDirectory() + File.separator + "Download/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("TAG", "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            downlad_file=new File(Environment.getExternalStorageDirectory(),fileName);
            System.out.println("downlad_file:"+downlad_file);

            photoURI = Uri.fromFile(downlad_file);

            Uri contentUri = getUriForFile(Messaging_User_Activity.this, BuildConfig.APPLICATION_ID + ".provider", downlad_file);
            System.out.println("photoURI:"+contentUri);

            Log.i("photoURI", Uri.fromFile(downlad_file).toString());

            String type=getContentResolver().getType(photoURI);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(Uri.parse(contentUri.toString()), type);
            startActivity(intent);

         /*   Toast.makeText(getApplicationContext(),
                    downlad_file.toString(), Toast.LENGTH_LONG).show();*/
        }
    }

    private class BackgrndTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            System.out.println("unique_id:"+unique_id);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ArrayList<Address_get_set>> call = apiInterface.check_request_status(unique_id);
            call.enqueue(new Callback<ArrayList<Address_get_set>>() {
                @Override
                public void onResponse(Call<ArrayList<Address_get_set>> call, Response<ArrayList<Address_get_set>> response) {
                    Log.d("URL::", response.toString());
                   res=response.toString();

                    try {
                        if (response.body().get(0).getError().equalsIgnoreCase("0")) {

                            System.out.println("SUCCESS:::" + response.body().get(0).getMsg());
                            System.out.println("response:"+response.body().get(0).getStatus());

                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("handler:"+response.body().get(0).getStatus());
                                    /*if (response.body().get(0).getStatus().equalsIgnoreCase("0")){
                                              handler.postDelayed(this, 1000);
                                        BackgrndTask task=new BackgrndTask();
                                        task.execute();
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("2")) {
                                        System.out.println("Status:"+"REJECT");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("3")){
                                        System.out.println("Status:"+"RUNNING");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("4")){
                                        System.out.println("Status:"+"COMPLETED");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("5")){
                                        System.out.println("Status:"+"CANCELLED");
                                        handler.postDelayed(this, 1000);
                                    }
                                    else */
                                    if (response.body().get(0).getStatus().equalsIgnoreCase("1")){
                                        System.out.println("Status:"+"ACCEPTED");
                                        BackgrndTask task=new BackgrndTask();
                                        task.execute();
                                    }
                                    else if (response.body().get(0).getStatus().equalsIgnoreCase("6"))
                                    {
                                        System.out.println("Status:"+"AMOUNT ENTERED BY VENDOR");
                                        System.out.println("amount:"+response.body().get(0).getAmount());

                                        globalVariables.setService_amt(response.body().get(0).getAmount());
                                    //    service_amount=response.body().get(0).getAmount();
                                     //   UserCompleteProcess completeProcess=new UserCompleteProcess();
                                   //     completeProcess.show(getSupportFragmentManager(),"dd");
                                     //   submitPressed = true;

                                    }
                                }
                            },1000);

                        }
                    } catch (Exception ex) {
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Address_get_set>> call, Throwable t) {

                }
            });
            return res;
        }

    }// end background task

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            System.out.println("request_code:"+requestCode);

            //for files
            if (requestCode == 1) {
                photoURI = data.getData();
                System.out.println("uri:"+photoURI);
                try {
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(Messaging_User_Activity.this,photoURI));
                    Simage=mPhotoFile.getName().toString();
                    System.out.println("getAbsolutePath:"+mPhotoFile.getAbsolutePath().toString());
                    System.out.println("getName:"+mPhotoFile.getName().toString());
                    System.out.println("getName:"+mPhotoFile.toString());
                 //   doc=mPhotoFile.toString();

                        File source = new File(photoURI.getPath());
                        System.out.println("file_name:"+photoURI.getPath());

                        String filename = photoURI.getLastPathSegment();
                        System.out.println("filename:"+filename);

                        // open the user-picked file for reading:
                        String type=getContentResolver().getType(photoURI);

                        System.out.println("type:"+type);

                    AlertDialog.Builder builder=new AlertDialog.Builder(Messaging_User_Activity.this);
                    if (sessionManager.getlanguage().equalsIgnoreCase("en")){
                        builder.setMessage(getResources().getString(R.string.send_image)+" "+Simage+" "
                                +getResources().getString(R.string.image_to)+" "+str_ven_name+"?");
                    }
                    else if (sessionManager.getlanguage().equalsIgnoreCase("hi")){
                        builder.setMessage(str_ven_name+" "+getResources().getString(R.string.send_image)
                                +" "+Simage+" "+getResources().getString(R.string.image_to)+"?");
                    }
                           builder.setCancelable(false)
                           .setPositiveButton("Send",new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.dismiss();
                                   status="1";
                                   status1 = RequestBody.create( MediaType.parse("multipart/form-data"), status);

                                   MediaType mediaType = MediaType.parse("multipart/form-data");//Based on the Postman logs,it's not specifying Content-Type, this is why I've made this empty content/mediaType

                                   String mime = getMimeType(getApplicationContext(), Uri.fromFile(mPhotoFile));
                                   System.out.println("mime:"+mime);
                                   if (mime.equalsIgnoreCase("image/jpg")
                                           ||mime.equalsIgnoreCase("image/jpeg")
                                           ||mime.equalsIgnoreCase("image/png")
                                           ||mime.equalsIgnoreCase("application/pdf")
                                           ||mime.equalsIgnoreCase("text/plain")
                                           ||mime.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                   ){
                                       RequestBody fileBody = RequestBody.create(mediaType, mPhotoFile);
                                        //Setting the file name as an empty string here causes the same issue, which is sending the request successfully without saving the files in the backend, so don't neglect the file name parameter.
                                       fileParts = MultipartBody.Part.createFormData("user_msg", mPhotoFile.getName(), fileBody);
                                       System.out.println("fileParts:"+fileParts);

                                       CHAT_JSON();
                                   }
                               }
                           })
                           .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.dismiss();
                               }
                           });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                //TODO handle your request here
                System.out.println("file_path:"+ mPhotoFile);
                 pdf= String.valueOf(mPhotoFile);
                System.out.println("pdf:"+ pdf);
            }

            //for camera
            if (requestCode == 4) {
                System.out.println("welcome");

                Uri imageUri= Uri.parse(imagePath);
                Log.d("NewIMAGEURI", String.valueOf(imageUri));

                File file = new File(imageUri.getPath());
             //   imagelist.add(file.toString());
                Simage=file.getName();
                Log.d("New File URL", String.valueOf(file));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(imagePath), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                System.out.println("image_bitmap:"+bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(Messaging_User_Activity.this,
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener()
                        {
                            public void onScanCompleted(String path, Uri uri)
                            { }
                        });

                status="1";
                status1 = RequestBody.create( MediaType.parse("multipart/form-data"), status);

                MediaType mediaType = MediaType.parse("multipart/form-data");//Based on the Postman logs,it's not specifying Content-Type, this is why I've made this empty content/mediaType

                String mime = getMimeType(getApplicationContext(), Uri.fromFile(file));
                System.out.println("mime:"+mime);
                if (mime.equalsIgnoreCase("image/jpg")
                        ||mime.equalsIgnoreCase("image/jpeg")
                        ||mime.equalsIgnoreCase("image/png")){
                    RequestBody fileBody = RequestBody.create(mediaType, file);
                    //Setting the file name as an empty string here causes the same issue, which is sending the request successfully without saving the files in the backend, so don't neglect the file name parameter.
                    fileParts = MultipartBody.Part.createFormData("user_msg", file.getName(), fileBody);
                    System.out.println("fileParts:"+fileParts);

                    CHAT_JSON();
                }
            }
        }
    }

    public String getMimeType(Context c,Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private  File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        System.out.println("filename:"+mFileName);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.println("storageDir:"+storageDir);
        File mFile = File.
                createTempFile(mFileName, ".jpg", storageDir);
        imagePath=mFile.getAbsolutePath();

        System.out.println("FILE::"+mFile);

        return mFile;
    }

    private void selectImage()
    {
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                try {
                    photoURI = getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("photo_URI:" + photoURI);
                mPhotoFile = photoFile;

                System.out.println("camera:" + mPhotoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, 4);

            }
        }
    }

    private boolean permissionAlreadyGranted1() {

        int result = ContextCompat.checkSelfPermission(Messaging_User_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission1() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Messaging_User_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(Messaging_User_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(Messaging_User_Activity.this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Messaging_User_Activity.this, Manifest.permission.CAMERA)) {

        }
        ActivityCompat.requestPermissions(Messaging_User_Activity.this, new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMREA_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.CAMERA );
                if (! showRationale) {
                    openSettingsDialog();
                }
            }
        }
        else if (requestCode == 10) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE );
                if (! showRationale) {
                    openSettingsDialog();
                }
            }
        }
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Messaging_User_Activity.this);
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

}
