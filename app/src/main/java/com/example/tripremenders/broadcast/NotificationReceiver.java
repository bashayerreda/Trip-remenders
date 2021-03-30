package com.example.tripremenders.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast. for the button action.
        String m = intent.getStringExtra("?");
        Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show();
    }
}