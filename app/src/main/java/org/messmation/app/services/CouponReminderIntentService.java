package org.messmation.app.services;

import android.app.IntentService;
import android.content.Intent;

import org.messmation.app.CouponTasks;

public class CouponReminderIntentService extends IntentService {

    public CouponReminderIntentService() {
        super("CouponReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        CouponTasks.executeTask(this, action);
    }
}
