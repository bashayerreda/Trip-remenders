package com.example.tripremenders.widget;

import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.tripremenders.R;
import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripRepository;
import com.example.tripremenders.service.WidgetService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimeAlertCustomDialog extends AppCompatDialogFragment {
    private int count ;
    MediaPlayer player;
    TripModel trip;
    boolean sound;
    NotificationHelper helper;
    TextView tripName;
    Button startButton;
    Button laterButton;
    Button cancelButton;
    ArrayList<NoteModel> noteModels;

    Application application;

    public TimeAlertCustomDialog(TripModel trip, boolean sound, ArrayList<NoteModel> noteModels, Application application) {
        this.count = 0 ;
        this.trip = trip;
        this.sound = sound;
        this.setCancelable(false);
        this.noteModels = noteModels;
        this.application = application;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.aleart_time_fire, null);

        builder.setView(view);

        if (sound) {
            play();
        }

        tripName = view.findViewById(R.id.trip_name);
        startButton = view.findViewById(R.id.start);
        laterButton = view.findViewById(R.id.later);
        cancelButton = view.findViewById(R.id.cancel);
        startButton.setOnClickListener(v -> {
            this.dismiss();
            stop();
            getActivity().finish();
            //DisplayTrack(trip.getEndPoint());

            DisplayTrack();

        });
        laterButton.setOnClickListener(v -> {
            this.dismiss();
            Toast.makeText(getContext(), "laterButton", Toast.LENGTH_SHORT).show();
            stop();
            helper = new NotificationHelper(getActivity());
            sendOnChannel1();
            getActivity().finish();
        });
        cancelButton.setOnClickListener(v -> {

            TripRepository tripRepository = new TripRepository(application);
            trip.setStatus(2);
            tripRepository.update(trip);

            this.dismiss();
            Toast.makeText(getContext(), "cancelButton", Toast.LENGTH_SHORT).show();
            stop();
            getActivity().finish();
        });

        tripName.setText(trip.getName());
        return builder.create();
    }

    public void play() {
        if (player == null) {
            player = MediaPlayer.create(getActivity(), R.raw.alarm_clock);
            player.setOnCompletionListener(mp -> {
                count++;
                if (count < 4) {
                    Log.i("TAG", "play: yes");
                    player.start();
                } else {
                    Log.i("TAG", "play: no");
                    stop();
                }
            });
        }
        player.start();

        Log.i("TAG", "play: ="+count);
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }

    public void stop() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void DisplayTrack(String endPoint) {
        //if device dosnt have mape installed then redirect it to play store
        //https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393

        showBubble();

        try {
            //when google map installed
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + "/" + endPoint);
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
    }

    private void showBubble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, 106);
        } else {
            Intent floatingService = new Intent(getActivity(), WidgetService.class);
            floatingService.putExtra("tripUid", 50);
            getActivity().startService(floatingService);
        }
    }

    // Notification
    public void sendOnChannel1() {
        NotificationCompat.Builder builder = helper.getChannel1Notification(trip,
                "Click here to start your trip", getContext());
        helper.getManger().notify(1, builder.build());
    }

    private void DisplayTrack() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, 106);

        } else {

            Intent floatingService = new Intent(getActivity(), WidgetService.class);
            floatingService.putExtra("noteModels", (Serializable) noteModels);
            floatingService.putExtra("tripModel", trip);
            //getActivity().stopService(floatingService);


            if(trip.getTripType().equals("Round Trip") || noteModels != null) {
                getActivity().startService(floatingService);
            }


            try {
                //when google map installed

                Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + "/" + trip.getEndPoint());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
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

        }
    }
}