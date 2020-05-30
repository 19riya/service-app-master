package com.hrproject.FirebaseClasses;


import android.content.Intent;
import android.graphics.BitmapFactory;

import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.HelperClasses.UserSessionManager;
import com.hrproject.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;
import java.util.HashMap;

import static android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION;

public class NotificationExtenderExample extends NotificationExtenderService {
  @Override
  protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
    // Read Properties from result
    OverrideSettings overrideSettings = new OverrideSettings();
    overrideSettings.extender = new NotificationCompat.Extender() {
      @Override
      public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {

        System.out.println("msg:"+receivedResult.payload.body);

          if (receivedResult.payload.body.equalsIgnoreCase("notify")){

            System.out.println("notify from notification extended example");
            UserSessionManager session = new UserSessionManager(getApplicationContext());
            // get user data from session
            if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();
            // type
            String type = user.get(session.KEY_TYPE);

            if (type.equalsIgnoreCase("vendor")) {

              if (receivedResult.isAppInFocus){
                System.out.println("notify from notification extended example when in focus");

              }else{
//                Intent ii =new Intent(NotificationExtenderExample.this, Vendor_Welcome.class);
//                ii.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                ii.putExtra("notify","notify");
//                startActivity(ii);
              }
            }
            }


          }
          else{
            System.out.println("not notify from notification extended example");
          }




        // Sets the background notification color to Red on Android 5.0+ devices.
          return builder.setColor(new BigInteger("1774A3", 16).intValue()).setSmallIcon(R.drawable.ic_notifications_black_24dp).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_black_24dp)).setContentTitle(receivedResult.payload.title).setContentText(receivedResult.payload.body).setAutoCancel(true);
      }
    };

    OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);



    Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

    // Return true to stop the notification from displaying
    return true;
  }
}