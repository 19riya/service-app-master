package com.hrproject.FirebaseClasses;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.hrproject.Activities.Forgot_Password;
import com.hrproject.Activities.Vendor.Messaging_Vendor_Activity;
import com.hrproject.Activities.Vendor.VendorChatHistory;
import com.hrproject.Activities.Vendor.Vendor_Welcome;
import com.hrproject.Activities.user.Messaging_User_Activity;
import com.hrproject.Activities.user.UserChatHistory;
import com.hrproject.MainActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.HashMap;

public class ExampleApplication extends Application {
    Object activityToLaunch = null;
    String openURL = null;

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.setSubscription(true);
        OneSignal.enableVibrate(true);

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();

        String userID = status.getSubscriptionStatus().getUserId();
        String pushToken = status.getSubscriptionStatus().getPushToken();

        Log.i("TOKENnnn", "PlayerID: " + userID + "\nPushToken: " + pushToken);
    }

    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;

            String customKey;

            /*OSNotificationReceivedResult result1 = null;
            Log.i("msg_notify:", result1.payload.body);*/

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);
            Log.i("data:", String.valueOf(data));

            if (data != null) {

                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            Log.i("msg_data:",result.notification.payload.body);
            Log.i("msg_type:", String.valueOf(result.action.type));
            String customKey;
           // String class_identify="class com.hrproject.Activities.Vendor.Vendor_Welcome";






            Log.i("data1:", String.valueOf(data));
            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = Vendor_Welcome.class;
                } else
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);

            if (result.notification.payload.body.equalsIgnoreCase("You have a new service request from user")){
                activityToLaunch = Vendor_Welcome.class;
                Load_Activity();
            }
            else if (result.notification.payload.body.equalsIgnoreCase("You have 1 new message from User")){
                activityToLaunch = Messaging_Vendor_Activity.class;
                Load_Activity();
            }
            else if (result.notification.payload.body.
                    equalsIgnoreCase("You have 1 new message from requested service")){
                activityToLaunch= Messaging_User_Activity.class;
                Load_Activity();
            }
            else if (result.notification.payload.body.
                    equalsIgnoreCase("You have 1 new booking message from requested service")){
                activityToLaunch= UserChatHistory.class;
                Load_Activity();
            }
            else if (result.notification.payload.body.
                    equalsIgnoreCase("You have 1 new booking message from User")){
                activityToLaunch= VendorChatHistory.class;
                Load_Activity();
            }
            else if (result.notification.payload.body.equalsIgnoreCase("Payble amount decided by Vendor")){
                activityToLaunch=Messaging_User_Activity.class;
                Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("notifyStatus","2");
                intent.putExtra("openURL", openURL);
                Log.i("OneSignalExample", "openURL = " + openURL);
                startActivity(intent);
            }





            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.

            /*
           <application ...>
             <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
           </application>
        */
        }
    }

    private void Load_Activity() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("notifyStatus","1");
        intent.putExtra("openURL", openURL);
        Log.i("OneSignalExample", "openURL = " + openURL);
        startActivity(intent);
    }
}