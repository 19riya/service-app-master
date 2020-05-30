package com.hrproject.Activities.Vendor;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class VendorChatHistory extends AppCompatActivity {
    GlobalVariables globalVariables;
    ImageView vchat_toolbar,vcall_booking;
    TextView vchat_person;
    RecyclerView vchat_history;
    TextView vno_history;
    AlertDialog p;


    EditText write_msg;
    ImageView send_msg,select_file,select_camera;
    FileUtils fileUtils;
    File mPhotoFile;
    String notifyStatus,message="";
    Toolbar vhistory_toolbar;
    Context context;
    private int CAMREA_CODE=1;
    String Simage;
    String status="0",booking_status="booking";
    Uri photoURI;
    String imagePath="";
    File downlad_file;
    RequestBody status1,user_id1,message1,unique_id1,booking_status1;
    MultipartBody.Part fileParts;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_chat_history);
        context=VendorChatHistory.this;

        sessionManager=new UserSessionManager(context);

        globalVariables=GlobalVariables.getInstance();
        vchat_history=findViewById(R.id.vchat_history);
        vchat_toolbar=findViewById(R.id.vchat_toolbar);
        vno_history=findViewById(R.id.vno_history);
        vcall_booking=findViewById(R.id.vcall_booking);
        vchat_person=findViewById(R.id.vchat_person);
        vhistory_toolbar=findViewById(R.id.vhistory_toolbar);

        setSupportActionBar(vhistory_toolbar);
        write_msg=findViewById(R.id.ven_write_msg);
        send_msg=findViewById(R.id.ven_send_msg);
        select_file=findViewById(R.id.ven_select_file);
        select_camera=findViewById(R.id.ven_select_camera);

        vchat_person.setText("Chat with "+globalVariables.getUser_name());

        vchat_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fileUtils=new FileUtils();

        vcall_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", globalVariables.getMobile(), null));
                startActivity(intent);
            }
        });

        unique_id1=RequestBody.create(MediaType.parse("multipart/form-data"),globalVariables.getUnique_id());
        user_id1 = RequestBody.create( MediaType.parse("multipart/form-data"), globalVariables.getUser_id());
        status1 = RequestBody.create( MediaType.parse("multipart/form-data"), status);
        message1 = RequestBody.create( MediaType.parse("multipart/form-data"), "0");
        booking_status1=RequestBody.create(MediaType.parse("multipart/form-data"),booking_status);

        CHAT_JSON1();

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionAlreadyGranted1()){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 1);
                }
                requestPermission1();
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

        send_msg.setOnClickListener(new View.OnClickListener() {
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

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(VendorChatHistory.this,
                LinearLayoutManager.VERTICAL,true);
        vchat_history.setLayoutManager(layoutManager);

       // checknet();
    }

    private boolean permissionAlreadyGranted1() {

        int result = ContextCompat.checkSelfPermission(VendorChatHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission1() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(VendorChatHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(VendorChatHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            System.out.println("request_code:"+requestCode);

            System.out.println("data:"+data);
            //for files
            if (requestCode == 1) {
                photoURI = data.getData();
                System.out.println("uri:"+photoURI);

                try {
                    /*final File file = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath(),photoURI.toString());

                    System.out.println("FILE:"+file);*/

                    mPhotoFile = new File(FileUtils.getRealPathFromUri(VendorChatHistory.this,photoURI));
                    Simage=mPhotoFile.getName().toString();
                    System.out.println("getAbsolutePath:"+mPhotoFile.getAbsolutePath().toString());
                    System.out.println("getName:"+mPhotoFile.getName().toString());
                    System.out.println("getName:"+mPhotoFile.toString());

                    File source = new File(photoURI.getPath());
                    System.out.println("file_name:"+photoURI.getPath());

                    String filename = photoURI.getLastPathSegment();
                    System.out.println("filename:"+filename);

                    // open the user-picked file for reading:
                    //    InputStream in = getContentResolver().openInputStream(photoURI);
                    String type=getContentResolver().getType(photoURI);

                    System.out.println("type:"+type);

                    AlertDialog.Builder builder=new AlertDialog.Builder(VendorChatHistory.this);

                    if (sessionManager.getlanguage().equalsIgnoreCase("en")){
                        builder.setMessage(getResources().getString(R.string.send_image)+" "+Simage+" "
                                +getResources().getString(R.string.image_to)+" "+globalVariables.getUser_name()+"?");
                    }
                    else if (sessionManager.getlanguage().equalsIgnoreCase("hi")){
                        builder.setMessage(globalVariables.getUser_name()+" "+getResources().getString(R.string.send_image)
                                +" "+Simage+" "+getResources().getString(R.string.image_to)+"?");
                    }

                    builder.setCancelable(false)
                            .setPositiveButton(""+getResources().getString(R.string.send),new DialogInterface.OnClickListener() {
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
                                        fileParts = MultipartBody.Part.createFormData("vendor_msg", mPhotoFile.getName(), fileBody);
                                        System.out.println("fileParts:"+fileParts);

                                        CHAT_JSON();
                                    }
                                }
                            })
                            .setNegativeButton(""+getResources().getString(R.string.cancel_changes),new DialogInterface.OnClickListener() {
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
                String pdf= String.valueOf(mPhotoFile);
                System.out.println("pdf:"+ pdf);
            }

            //for camera
            if (requestCode == 4) {
                System.out.println("welcome");

                Uri imageUri= Uri.parse(imagePath);
                Log.d("NewIMAGEURI", String.valueOf(imageUri));

                File file = new File(imageUri.getPath());
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
                MediaScannerConnection.scanFile(VendorChatHistory.this,
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
                    fileParts = MultipartBody.Part.createFormData("vendor_msg", file.getName(), fileBody);
                    System.out.println("fileParts:"+fileParts);
                }

                CHAT_JSON();
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

    //for files
    private void CHAT_JSON() {
        System.out.println("message:"+fileParts);
        System.out.println("user_id:"+globalVariables.getUser_id());
        System.out.println("status:"+status);
        System.out.println("user_id1:"+user_id1);
        System.out.println("status1:"+status1);
        System.out.println("unique_id:"+globalVariables.getUnique_id());
        System.out.println("unique_id1:"+unique_id1);
        System.out.println("booking_status:"+booking_status);
        System.out.println("booking_status1:"+booking_status1);

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Msg_Get_Set>> call=apiInterface.notification_user_chat_service(user_id1,fileParts,status1
                ,unique_id1,booking_status1);
        call.enqueue(new Callback<ArrayList<Msg_Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Msg_Get_Set>> call, Response<ArrayList<Msg_Get_Set>> response) {
                Log.d("URL:",response.toString());

                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    System.out.println("welcome"+ "to chat");

                /*    Picasso.with(Messaging_Vendor_Activity.this).load(response.body().get(0).getProfile_image())
                            .placeholder(R.drawable.man).into(user_image);

                    user_name.setText(response.body().get(0).getUser_name());
                    str_user_name=response.body().get(0).getUser_name();
*/
                    if (response.body().size()>0){
                        ArrayList<Offers_get_set> msg_data = new ArrayList<>();
                        msg_data=response.body().get(0).getUser_msg();

                        System.out.println("response_msg:"+msg_data.toString());

                        if(msg_data.size()>0)
                        {
                            Ven_Messaging_List adapter=new Ven_Messaging_List(
                                    VendorChatHistory.this,msg_data);
                            vchat_history.setAdapter(adapter);
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
        System.out.println("message1:"+message1);
        System.out.println("user_id:"+globalVariables.getUser_id());
        System.out.println("status:"+status);
        System.out.println("message:"+message);
        System.out.println("user_id1:"+user_id1);
        System.out.println("status1:"+status1);
        System.out.println("booking_status:"+booking_status);
        System.out.println("booking_status1:"+booking_status1);

        System.out.println("unique_id:"+globalVariables.getUnique_id());
        System.out.println("unique_id1:"+unique_id1);
        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Msg_Get_Set>> call=apiInterface.notification_user_chat_service1(user_id1,message1,status1
                ,unique_id1,booking_status1);
        call.enqueue(new Callback<ArrayList<Msg_Get_Set>>() {
            @Override
            public void onResponse(Call<ArrayList<Msg_Get_Set>> call, Response<ArrayList<Msg_Get_Set>> response) {
                Log.d("URL:",response.toString());

                if (response.body().get(0).getError().equalsIgnoreCase("0")){
                    System.out.println("welcome"+ "to chat");
                    if (response.body().size()>0){
                    /*    Picasso.with(Messaging_Vendor_Activity.this).load(response.body().get(0).getProfile_image())
                                .placeholder(R.drawable.man).into(user_image);


                        globalVariables.setUser_name(response.body().get(0).getUser_name());
                        user_name.setText(response.body().get(0).getUser_name());
                        str_user_name=response.body().get(0).getUser_name();
*/

                        ArrayList<Offers_get_set> msg_data = new ArrayList<>();
                        msg_data=response.body().get(0).getUser_msg();

                        System.out.println("response_msg:"+msg_data.toString());

                        if(msg_data.size()>0)
                        {
                            message="0";
                            Ven_Messaging_List adapter=new Ven_Messaging_List(
                                    VendorChatHistory.this,msg_data);
                            vchat_history.setAdapter(adapter);
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

    public class Ven_Messaging_List extends RecyclerView.Adapter<Ven_Messaging_List.MyViewHolder>
    {
        Context c;
        ArrayList<Offers_get_set> data;

        public Ven_Messaging_List(VendorChatHistory item_accept_reject_list, ArrayList<Offers_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public Ven_Messaging_List.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_ven_messaging__list, viewGroup, false);

            return new Ven_Messaging_List.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final Ven_Messaging_List.MyViewHolder myViewHolder, final int i) {
            String mimeType;

            if (data.size()>0){
                String path=data.get(i).getMessage();
                File file = new File(path);
                System.out.println("file:"+file);

                Uri uri=Uri.fromFile(file);
                System.out.println("file:"+file);

                String ext=file.getName().substring(file.getName().indexOf(".")+1);
                System.out.println("ext:"+ext);

                System.out.println("file:"+file.getAbsolutePath());
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

                            Uri contentUri = getUriForFile(VendorChatHistory.this, BuildConfig.APPLICATION_ID + ".provider", downlad_file);
                            System.out.println("contentUri:"+contentUri);

                            if(downlad_file.exists()){
                                System.out.println("check:"+"exists");
                                Intent intent=new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(contentUri.toString()),null);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                if(data.get(i).getType().equalsIgnoreCase("user"))
                {
                    if (data.get(i).getMessage().equals(""))
                    {
                        System.out.println("ans:"+"vendor_blank_message");
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.GONE);
                    }
                    else if (ext.equalsIgnoreCase("docx")
                            ||ext.equalsIgnoreCase("pdf")
                            ||ext.equalsIgnoreCase("txt")
                            ||ext.equalsIgnoreCase("jpg")
                            ||ext.equalsIgnoreCase("jpeg")
                            ||ext.equalsIgnoreCase("png"))
                    {
                        System.out.println("ans:"+"doc_file");
                        myViewHolder.vendor_message.setVisibility(View.VISIBLE);
                        myViewHolder.user_message.setVisibility(View.GONE);
                        myViewHolder.vendor_message.setText(file.getName().toString());
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
                        System.out.println("ans:"+"user_blank_message");
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.GONE);
                    }
                    else if (ext.equalsIgnoreCase("docx")
                            ||ext.equalsIgnoreCase("pdf")
                            ||ext.equalsIgnoreCase("txt")
                            ||ext.equalsIgnoreCase("jpg")
                            ||ext.equalsIgnoreCase("jpeg")
                            ||ext.equalsIgnoreCase("png")){
                        System.out.println("ans:"+"doc_file");
                        myViewHolder.user_message.setText(file.getName());

                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        myViewHolder.vendor_message.setVisibility(View.GONE);
                        myViewHolder.user_message.setVisibility(View.VISIBLE);
                        myViewHolder.user_message.setText(data.get(i).getMessage());
                        System.out.println("user_message:"+data.get(i).getMessage());
                    }

                }
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
            public TextView vendor_message,user_message;

            public MyViewHolder(View view) {
                super(view);
                vendor_message=view.findViewById(R.id.ven_vendor_message);
                user_message=view.findViewById(R.id.ven_user_message);

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
            this.progressDialog = new ProgressDialog(VendorChatHistory.this);
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

            Uri contentUri = getUriForFile(VendorChatHistory.this, BuildConfig.APPLICATION_ID + ".provider", downlad_file);
            System.out.println("contentUri:"+contentUri);

            String type=getContentResolver().getType(photoURI);
            System.out.println("type:"+type);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(Uri.parse(contentUri.toString()), type);
            startActivity(intent);

           /* Toast.makeText(getApplicationContext(),
                    downlad_file.toString(), Toast.LENGTH_LONG).show();*/
        }
    }

    private void selectImage() {
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null)
        {
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

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(VendorChatHistory.this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(VendorChatHistory.this, Manifest.permission.CAMERA)) {

        }
        ActivityCompat.requestPermissions(VendorChatHistory.this, new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(VendorChatHistory.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.booking_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();

        if (id==R.id.booking_map){
            // String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
            //    String uri="http://maps.google.com/maps?";

            String geoUri = "http://maps.google.com/maps?q=loc:" + globalVariables.getLatitude() + "," + globalVariables.getLongitude() + " (" + "Alwar" + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            context.startActivity(intent);

        }

        if (id==R.id.booking_otp){
            startActivity(new Intent(VendorChatHistory.this,Accept_Rquest_Order2.class));
            finish();
        }
        switch (item.getItemId()){
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;

         /*   case R.id.action_settings1:
                return true;*/

        }


        return super.onOptionsItemSelected(item);
    }

    public class VChatList extends RecyclerView.Adapter<VChatList.MyViewHolder>
    {
        Context c;
        ArrayList<Offers_get_set> data;

        public VChatList(FragmentActivity item_accept_reject_list, ArrayList<Offers_get_set> moviesList) {
            this.data = moviesList;
            this.c=item_accept_reject_list;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public VChatList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_vchat_list, viewGroup, false);

            return new VChatList.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final VChatList.MyViewHolder myViewHolder, final int i) {


                if (data.get(i).getType().equalsIgnoreCase("vendor")){
                    myViewHolder.vendor_msg.setVisibility(View.VISIBLE);
                    myViewHolder.vendor_msg.setText(data.get(i).getMessage());
                }

                if (data.get(i).getType().equalsIgnoreCase("user")){
                    myViewHolder.user_msg.setVisibility(View.VISIBLE);
                    myViewHolder.user_msg.setText(data.get(i).getMessage());

                }

               /* statusString = "<b>" + getResources().getString(R.string.status) + "</b> " + ": "+
                        "<font color='#000000' >"+data.get(i).getStatus()+"</font>";*/
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

                vendor_msg = view.findViewById(R.id.vvendor_msg);
                user_msg = view.findViewById(R.id.vuser_msg);

            }
        }
    }

    private void VCHAT_HISTORY() {
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
                           vno_history.setVisibility(View.GONE);
                           vchat_history.setVisibility(View.VISIBLE);
                           vchat_history.setAdapter(new VChatList(VendorChatHistory.this,chat_list));
                       }
                       else{
                           vno_history.setVisibility(View.VISIBLE);
                           vchat_history.setVisibility(View.GONE);

                       }
                    }
                }
                catch (Exception ex){
                    Log.i("VCHAT_HISTORY_ERROR:",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Msg_Get_Set>> call, Throwable t) {
                p.dismiss();
                Log.i("VCHAT_HISTORY_FAILURE:",t.getMessage());
            }
        });
    }

    private void checknet() {
        ConnectionDetector cd = new ConnectionDetector(VendorChatHistory.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        System.out.println("status:::"+isInternetPresent);
        // check for Internet status
        if (isInternetPresent) {

            androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(VendorChatHistory.this);
            View mView = getLayoutInflater().inflate(R.layout.loader, null);
            mBuilder.setView(mView);
            p = mBuilder.create();
            p.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            p.setCancelable(false);
            p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            p.setCanceledOnTouchOutside(false);
            p.show();
            VCHAT_HISTORY();
        }
        else{
            SweetAlertDialog ff=new SweetAlertDialog(VendorChatHistory.this,SweetAlertDialog.WARNING_TYPE);
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
}
