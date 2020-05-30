package com.hrproject.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hrproject.Activities.Choose_Option;
import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.Activities.user.User_Welcome;
import com.hrproject.MainActivity;

import java.util.HashMap;

public class UserSessionManager
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID="U_id_mobile";
    public static final String KEY_NAME ="name";
    public static final String KEY_Language ="en";
    public static final String KEY_CHATID="chat_id";
    public static final String KEY_Status="status";
    public static final String KEY_TYPE ="type";
    public static final String KEY_SNAME="sname";
//    public static final String KEY_DESCRIPTION="descrip";

    public UserSessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences("My Prefs", PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String mobile,String name,String type)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, mobile);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TYPE, type);

        editor.commit();
    }

  public void setLanguage(String langcode)
    {
        editor.putString(KEY_Language, langcode);
        editor.commit();
    }

    public void setChatId(String chatId,String service_status,String service_name){
        editor.putString(KEY_CHATID,chatId);
        editor.putString(KEY_Status,service_status);
        editor.putString(KEY_SNAME,service_name);
        editor.commit();
    }

    public HashMap<String, String>getChatId(){
        HashMap<String, String> rating_status = new HashMap<String, String>();
        rating_status.put(KEY_CHATID, pref.getString(KEY_CHATID, ""));
        rating_status.put(KEY_Status, pref.getString(KEY_Status, ""));
        rating_status.put(KEY_SNAME, pref.getString(KEY_SNAME, ""));

        return rating_status;
    }

    public String getlanguage(){
        return pref.getString(KEY_Language, "en");
    }

    public String get_ID(String KEY_ID)
    {
        return KEY_ID;
    }

    public HashMap<String, String>getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID, pref.getString(KEY_ID, null));
      //  user.put(KEY_DESCRIPTION,pref.getString(KEY_DESCRIPTION,null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_TYPE, pref.getString(KEY_TYPE, null));

        return user;
    }

    public void checkLogin()
    {
        if(!this.isLoggedIn())
        {
            Intent intent=new Intent(_context, Choose_Option.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(intent);

        }
        else if(KEY_TYPE.equalsIgnoreCase("user"))
        {
            Log.i("check:","main_acticity");
            Intent i = new Intent(_context, User_Welcome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
           //finish();
        }

        else if(KEY_TYPE.equalsIgnoreCase("vendor"))
        {
            Intent i = new Intent(_context, Vendor_Welcome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("notifyStatus","0");
            // Staring Login Activity
            _context.startActivity(i);
            //finish();
        }
    }
     public void logoutUser()
        {
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, Choose_Option.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


}
