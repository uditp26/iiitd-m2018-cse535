package org.messmation.app.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.messmation.app.ActivitySplash;
import org.messmation.app.CouponTasks;
import org.messmation.app.ManualCoupon;
import org.messmation.app.R;
import org.messmation.app.RegistrationActivity;
import org.messmation.app.homepage;
import org.messmation.app.services.CouponReminderIntentService;

/**
 * Class adapted using teaching material from Udacity Android Course
 */

/**
 * Utility class for creating app notifications
 */
public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int COUPON_DEDUCTION_NOTIFICATION_ID = 1138;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int COUPON_REMINDER_PENDING_INTENT_ID = 3417;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String COUPON_DEDUCTION_REMINDER_CHANNEL_ID = "coupon_reminder_notification_channel";

    private static final int ACTION_COUPON_DEDUCT_PENDING_INTENT_ID = 1; //request code for pending intent related to deducting coupon
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 14; //request code for pending intent related to ignoring coupon deduction notification

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void showCouponReminderNotification(Context context) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    COUPON_DEDUCTION_REMINDER_CHANNEL_ID,
                    context.getString(R.string.coupon_reminder_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, COUPON_DEDUCTION_REMINDER_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher) //TODO change this
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.coupon_reminder_notification_title))
                .setContentText(context.getString(R.string.coupon_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.coupon_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(deductCouponAction(context))
                .addAction(ignoreCouponAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(COUPON_DEDUCTION_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static NotificationCompat.Action deductCouponAction(Context context) {
        Log.d(TAG,"Inside deductCouponAction");
        Intent registrationActivityIntent = new Intent(context, CouponReminderIntentService.class);
        registrationActivityIntent.setAction(CouponTasks.ACTION_DEDUCT_COUPON);
        PendingIntent deductCouponNotifIntent = PendingIntent.getService(
                context,
                ACTION_COUPON_DEDUCT_PENDING_INTENT_ID,
                registrationActivityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        //TODO change this, notification action image
        NotificationCompat.Action deductCouponAction = new NotificationCompat.Action(R.mipmap.ic_launcher,
                "Yes, do it!",
                deductCouponNotifIntent);
        return deductCouponAction;
    }


    private static NotificationCompat.Action ignoreCouponAction(Context context) {
        Intent ignoreReminderIntent = new Intent(context, CouponReminderIntentService.class);
        ignoreReminderIntent.setAction(CouponTasks.ACTION_IGNORE_NOTIFICATION);
        PendingIntent ignoreCouponDeductionNotifIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //TODO change this, notification action image
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.mipmap.ic_launcher,
                "No, thanks.",
                ignoreCouponDeductionNotifIntent);
        return ignoreReminderAction;
    }


    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, homepage.class);
        return PendingIntent.getActivity(
                context,
                COUPON_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher); //TODO change this
        return largeIcon;
    }
}