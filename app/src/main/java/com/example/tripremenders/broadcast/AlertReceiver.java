package com.example.tripremenders.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tripremenders.DialogMessageActivity;


public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String s = intent.getStringExtra("name");
        Intent g2 = new Intent(context, DialogMessageActivity.class);
        g2.putExtra("G2", s);
//        g2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(g2);

    }
}
