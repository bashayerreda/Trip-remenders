package com.example.tripremenders.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.tripremenders.DialogMessageActivity;
import com.example.tripremenders.R;
import com.example.tripremenders.broadcast.NotificationReceiver;
import com.example.tripremenders.models.TripModel;

public class NotificationHelper extends ContextWrapper {

    private NotificationManager manager;

    public final String channel1ID = "channel1ID";
    public final String channel1Name = "channel1Name";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel channel = new NotificationChannel(channel1ID,
                channel1Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.primary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManger().createNotificationChannel(channel);
    }

    // who build the not...ch
    public NotificationManager getManger() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getChannel1Notification(TripModel trip, String message, Context context) {
        //for click on Notification
        // id >>
        Intent intent = new Intent(context, DialogMessageActivity.class);
        intent.putExtra("tripId", trip.getId());
        intent.putExtra("sound", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //action off add button
        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("title", trip.getName());
        PendingIntent action = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel1ID)
                .setContentTitle(trip.getName())
                .setContentText(message)
                .setColor(getResources().getColor(R.color.primary))
                .setSmallIcon(R.drawable.ic_note_add)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;

    }

}