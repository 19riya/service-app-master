package com.hrproject.HelperClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector
{
    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager cn=(ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cn!=null)
        {
            NetworkInfo info=cn.getActiveNetworkInfo();
            if(info!=null && info.isConnected())
            {
                return true;
            }
        }
        return false;
    }


}
