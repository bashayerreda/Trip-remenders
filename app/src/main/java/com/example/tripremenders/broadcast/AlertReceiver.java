package com.example.tripremenders.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tripremenders.DialogMessageActivity;
import com.example.tripremenders.models.TripModel;


public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startIntent = new Intent(context, DialogMessageActivity.class);
        startIntent.putExtra("tripId", intent.getIntExtra("tripId", 0));
        startIntent.putExtra("sound", intent.getBooleanExtra("sound", true));
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);

    }
}
