package com.example.tripremenders.service;


import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripremenders.R;
import com.example.tripremenders.adapters.NoteAdapter;
import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.TripModel;

import java.util.ArrayList;


public class WidgetService extends Service implements View.OnClickListener {


    private static final String TAG = "MYTAG";
    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    RecyclerView recyclerView;
    NoteAdapter adapter;
    private ArrayList<NoteModel> notes;
    private TripModel tripModel;
    private Button returnTrip ;

    private final IBinder binder = new LocalBinder();


    public WidgetService() { }


    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("WidgetService", true); // Storing boolean - true/false
        editor.apply(); // commit changes

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_item, null);
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);
        recyclerView = expandedView.findViewById(R.id.floatingWidgetRecyclerView);
        returnTrip = expandedView.findViewById(R.id.returnTrip);
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);

        //setting the layout parameters
        WindowManager.LayoutParams params;

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        mWindowManager.addView(mFloatingView, params);

        mFloatingView.findViewById(R.id.layoutCollapsed).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        return false;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);

                        return false;
                }
                return false;
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        tripModel = (TripModel) intent.getSerializableExtra("tripModel");
        notes = (ArrayList<NoteModel>) intent.getSerializableExtra("noteModels");

        //initialize notes list
        //getting the widget layout from xml using layout inflater
        if(!tripModel.getTripType().equals("Round Trip")) {
            returnTrip.setVisibility(View.GONE);
        } else {
            returnTrip.setVisibility(View.VISIBLE);
        }

        returnTrip.setOnClickListener(v -> {
            try {
                //when google map installed

                Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + "/" + tripModel.getEndPoint());
                Intent googleMapIntent = new Intent(Intent.ACTION_VIEW, uri);
                googleMapIntent.setPackage("com.google.android.apps.maps");
                googleMapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(googleMapIntent);

            } catch (ActivityNotFoundException e) {
                //when google map is not initialize
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                Intent googlePlayStoreIntent = new Intent(Intent.ACTION_VIEW, uri);
                //set flag
                googlePlayStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(googlePlayStoreIntent);
            }
        });



        //getting the collapsed and expanded view from the floating view

        adapter = new NoteAdapter(WidgetService.this, notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(WidgetService.this));
        recyclerView.setAdapter(adapter);
        //adding click listener to close button and expanded view
        expandedView.setOnClickListener(this);
        //check when switching between view s
        collapsedView.setOnClickListener(v -> {
            if (collapsedView.getVisibility() == View.VISIBLE && expandedView.getVisibility() == View.VISIBLE) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            } else {
                switch (v.getId()) {
                    case R.id.layoutCollapsed:
                        Log.i(TAG, "collapsed ");
                        //switching views
                        collapsedView.setVisibility(View.VISIBLE);
                        expandedView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


        //adding an touchlistener to make drag movement of the floating widget



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public WidgetService getService() {
            return WidgetService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("WidgetService", false); // Storing boolean - true/false
        editor.apply(); // com
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layoutExpanded:
                //switching views
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;

            case R.id.buttonClose:
                //closing the widget
                stopSelf();
                break;
        }
    }

    public void stopService() {
        stopSelf();
    }
}
