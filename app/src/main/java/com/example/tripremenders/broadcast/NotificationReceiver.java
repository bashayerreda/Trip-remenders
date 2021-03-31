package com.example.tripremenders.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String m = intent.getStringExtra("title");
        Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show();
    }
}