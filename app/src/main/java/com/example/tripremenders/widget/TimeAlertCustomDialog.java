package com.example.tripremenders.widget;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.NotificationCompat;

import com.example.tripremenders.R;
import com.example.tripremenders.models.TripModel;

public class TimeAlertCustomDialog extends AppCompatDialogFragment {

    MediaPlayer player;
    TripModel trip;
    boolean sound;
    NotificationHelper helper;
    TextView tripName;
    Button startButton;
    Button laterButton;
    Button cancelButton;

    public TimeAlertCustomDialog(TripModel trip, boolean sound) {
        this.trip = trip;
        this.sound = sound;
        this.setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.aleart_time_fire, null);

        builder.setView(view);

        if (sound){
            play(view);
        }
        tripName = view.findViewById(R.id.trip_name);
        startButton = view.findViewById(R.id.start);
        laterButton = view.findViewById(R.id.later);
        cancelButton = view.findViewById(R.id.cancel);
        startButton.setOnClickListener(v -> {
            this.dismiss();
            stop(view);
            try {
                //when google map installed
                Uri uri = Uri.parse("geo:0,0?q=" + trip.getEndPointLat() + "," + trip.getEndPointLat());
            /*Uri uri = Uri.parse("https://www.google.com/maps/search/?api&query=" +
                    latLng.longitude + "," + latLng.latitude); */
                //Action view with uri
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                //set flag
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } catch (ActivityNotFoundException e) {
                //when google map is not initialize
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //set flag
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
            getActivity().finish();
        });
        laterButton.setOnClickListener(v -> {
            this.dismiss();
            Toast.makeText(getContext(), "laterButton", Toast.LENGTH_SHORT).show();
            stop(view);
            helper = new NotificationHelper(getActivity());
            sendOnChannel1();
            getActivity().finish();
        });
        cancelButton.setOnClickListener(v -> {
            this.dismiss();
            Toast.makeText(getContext(), "cancelButton", Toast.LENGTH_SHORT).show();
            stop(view);
            getActivity().finish();
        });

        tripName.setText(trip.getName());
        return builder.create();
    }

    public void play(View view) {
        if (player == null) {
            player = MediaPlayer.create(getActivity(), R.raw.alarm_clock);
            player.setOnCompletionListener(mp -> {
                player.start();
            });
        }
        player.start();
    }

    public void stop(View view) {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // Notification
    public void sendOnChannel1() {
        NotificationCompat.Builder builder = helper.getChannel1Notification(trip,
                "Click here to start your trip", getContext());
        helper.getManger().notify(1, builder.build());
    }

}