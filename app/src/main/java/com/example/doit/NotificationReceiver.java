package com.example.doit;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "createTask":
                NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(1);
                break;
        }
    }
}
