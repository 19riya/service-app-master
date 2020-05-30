package com.hrproject.Activities.user;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrproject.Activities.Vendor.VendorChatHistory;
import com.hrproject.BuildConfig;
import com.hrproject.GetterSetter.GlobalVariables;
import com.hrproject.GetterSetter.Msg_Get_Set;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.HelperClasses.ConnectionDetector;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.FileProvider.getUriForFile;

public class UserChatHistory extends AppCompatActivity {
    GlobalVariables globalVariables;
    ImageView uchat_toolbar,call_booking;
    RecyclerView uchat_history;
    TextView uno_history,chat_person;
    AlertDialog p;
    UserSessionManager sessionManager;

    FileUtils fileUtils;
    File mPhotoFile;
    private int CAMREA_CODE=1;
    String Simage,str_ven_name="",message="",booking_status="booking";
    TextView vendor_rating;
    Context context;
    String pdf;
    RequestBody status1,message1,vendor_id1,unique_id1,booking_status1;
    MultipartBody.Part fileParts;
    String imagePath="";
    File downlad_file;
    Uri photoURI;
    String status="0",res;

    EditText write_msg;
    ImageView send_msg,select_file,select_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat_history);

        globalVariables= GlobalVariables.getInstance();
        context=UserChatHistory.this;

        fileUtils=new FileUtils();

        sessionManager=new UserSessionManager(context);
        write_msg=findViewById(R.id.write_msg);
        send_msg=findViewById(R.id.send_msg);
        select_file=findViewById(R.id.select_file);
        select_camera=findViewById(R.id.select_camera);


        uchat_history=findViewById(R.id.uchat_history);
        uchat_toolbar=findViewById(R.id.uchat_toolbar);
        uno_history=findViewById(R.id.uno_history);
        call_booking=findViewById(R.id.call_booking);
        chat_person=findViewById(R.id.chat_person);

        chat_person.setText("Chat with "+globalVariables.getVendor_name());
        uchat_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        call_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", globalVariables.getMobile(), null));
                startActivity(intent);
            }
        });

        unique_id1 = RequestBody.create( MediaType.parse("multipart/form-data"), globalVariables.getUnique_id());
        vendor_id1 = RequestBody.create( MediaType.parse("multipart/form-data"), globalVariables.getVendor_id());
        status1 = RequestBody.create( MediaType.parse("multipart/form-data"), status);
        message1 = RequestBody.create( MediaType.parse("multipart/form-data"), "0");
        booking_status1 = RequestBody.create( MediaType.parse("multipart/form-data"), booking_status);

        CHAT_JSON1();

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

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(UserChatHistory.this,
                LinearLayoutManager.VERTICAL,true);
        uchat_history.setLayoutManager(layoutManager);

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




    //    checknet();
    }

    public class UChatList extends RecyclerView.Adapter<UChatList.MyViewHolder>
    {
        Context c;
        ArrayList<Offers_get_set> data;

        public UChatList(FragmentActivity item_accept_reject_list, ArrayList<Offers_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public UChatList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_uchat_list, viewGroup, false);

            return new UChatList.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final UChatList.MyViewHolder myViewHolder, final int i) {


            if (data.get(i).getType().equalsIgnoreCase("vendor")){
                myViewHolder.vendor_msg.setVisibility(View.VISIBLE);
                myViewHolder.vendor_msg.setText(data.get(i).getMessage());
            }

            if (data.get(i).getType().equalsIgnoreCase("user")){
                myViewHolder.user_msg.setVisibility(View.VISIBLE);
                myViewHolder.user_msg.setText(data.get(i).getMessage());

            }

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
            public TextView vendor_msg,user_msg;


            public MyViewHolder(View view) {
                super(view);

                vendor_msg = view.findViewById(R.id.uvendor_msg);
                user_msg = view.findViewById(R.id.uuser_msg);

            }
        }
    }

    private void UCHAT_HISTORY() {
        System.out.println("unique_id:"+globalVariables.getUnique_id());
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Msg_Get_Set>> call=apiInterface.chat_history(globalVariables.getUnique_id());
        call.enqueue(new Callback<ArrayList<Msg_Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Msg_Get_Set>> call, Response<ArrayList<Msg_Get_Set>> response) {
                p.dismiss();
                Log.d("URL:",response.toString());

                try {
                    if (response.body().get(0).getError().equalsIgnoreCase("0")){
                        ArrayList<Offers_get_set> chat_list=new ArrayList<>();
                        chat_list=response.body().get(0).getResult();

                        if (chat_list.size()>0){
                            uno_history.setVisibility(View.GONE);
                            uchat_history.setVisibility(View.VISIBLE);
                            uchat_history.setAdapter(new UChatList(UserChatHistory.this,chat_list));
                        }
                        else{
                            uno_history.setVisibility(View.VISIBLE);
                            uchat_history.setVisibility(View.GONE);
                        }
                    }
                }
                catch (Exception ex){
                    Log.i("UCHAT_HISTORY_ERROR:",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Msg_Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.i("UCHAT_HISTORY_FAILURE:",t.getMessage());
            }
        });
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(UserChatHistory.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        System.out.println("status:::"+isInternetPresent);
        // check for Internet status
        if (isInternetPresent) {

            androidx.appcompat.app.AlertDialog.Builder mBuilder =
                    new androidx.appcompat.app.AlertDialog.Builder(UserChatHistory.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
            UCHAT_HISTORY();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(UserChatHistory.this,SweetAlertDialog.WARNING_TYPE);
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

    // for sending files
    private void CHAT_JSON() {
        System.out.println("vendor_id:"+globalVariables.getVendor_id());
        System.out.println("message1:"+fileParts);
        System.out.println("vendor_id1:"+vendor_id1);
        System.out.println("status:"+status);
        System.out.println("status1:"+status1);
        System.out.println("unique_id1:"+globalVariables.getUnique_id());
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
                    /*    Picasso.with(Messaging_User_Activity.this).load(response.body().get(0).getProfile_image())
                                .placeholder(R.drawable.man).into(vendor_image);

                        vendor_name.setText(response.body().get(0).getUser_name());
                        str_ven_name=response.body().get(0).getUser_name();
*/

                        ArrayList<Offers_get_set> msg_data = new ArrayList<>();

                        msg_data=response.body().get(0).getUser_msg();
                        System.out.println("response_msg:"+msg_data);

                        if (msg_data.size()>0) {
                            //   message="0";
                            Messaging_List adapter = new Messaging_List(UserChatHistory.this
                                    , msg_data);
                            uchat_history.setAdapter(adapter);
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
        System.out.println("vendor_id:"+globalVariables.getVendor_id());
        System.out.println("message1:"+message1);
        System.out.println("vendor_id1:"+vendor_id1);
        System.out.println("status:"+status);
        System.out.println("status1:"+status1);
        System.out.println("unique_id1:"+globalVariables.getUnique_id());
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

                    /*    employee_name=response.body().get(0).getUser_name();
                        str_ven_name=response.body().get(0).getUser_name();
*/
/*
                        System.out.println("ratings:"+response.body().get(0).getRating());

                        vendor_rating.setText(""+response.body().get(0).getRating());
*/


                        ArrayList<Offers_get_set> msg_data = new ArrayList<>();

                        msg_data=response.body().get(0).getUser_msg();
                        System.out.println("response_msg:"+msg_data);

                        if (msg_data.size()>0) {
                            message="0";
                            //  ArrayList<Msg_Get_Set> datalist = new ArrayList<>(response.body());
                            Messaging_List adapter = new Messaging_List(UserChatHistory.this, msg_data);
                            uchat_history.setAdapter(adapter);
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

    public class Messaging_List extends RecyclerView.Adapter<Messaging_List.MyViewHolder>
    {
        Context c;
        ArrayList<Offers_get_set> data;

        public Messaging_List(UserChatHistory item_accept_reject_list, ArrayList<Offers_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public Messaging_List.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_messaging__list, viewGroup, false);

            return new Messaging_List.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final Messaging_List.MyViewHolder myViewHolder, final int i) {
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

                            Uri contentUri = getUriForFile(UserChatHistory.this, BuildConfig.APPLICATION_ID + ".provider", downlad_file);
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
            this.progressDialog = new ProgressDialog(UserChatHistory.this);
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

            Uri contentUri = getUriForFile(UserChatHistory.this, BuildConfig.APPLICATION_ID + ".provider", downlad_file);
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
                    mPhotoFile = new File(fileUtils.getRealPathFromUri(UserChatHistory.this,photoURI));
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

                    AlertDialog.Builder builder=new AlertDialog.Builder(UserChatHistory.this);
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
                MediaScannerConnection.scanFile(UserChatHistory.this,
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

        int result = ContextCompat.checkSelfPermission(UserChatHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission1() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(UserChatHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(UserChatHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(UserChatHistory.this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(UserChatHistory.this, Manifest.permission.CAMERA)) {

        }
        ActivityCompat.requestPermissions(UserChatHistory.this, new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(UserChatHistory.this);
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
