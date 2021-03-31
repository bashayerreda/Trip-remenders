package com.example.tripremenders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;
import com.example.tripremenders.widget.TimeAlertCustomDialog;

import java.util.List;

public class DialogMessageActivity extends AppCompatActivity {
//    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dialog_message);


        int tripId = getIntent().getIntExtra("tripId", 0);

        Log.i("TAG", "onCreate: DialogMessageActivity 1");
        if(tripId != 0) {
            TripViewModel tripViewModel =
                    ViewModelProviders.of(this).get(TripViewModel.class);
            Log.i("TAG", "onCreate: DialogMessageActivity 2");

            LiveData<List<TripModel>> tripById = tripViewModel.getTripById(tripId);
            Log.i("TAG", "onCreate: DialogMessageActivity 3");

            tripById.observe(this, new Observer<List<TripModel>>() {

                @Override
                public void onChanged(@Nullable final List<TripModel> tripModels) {
                    Log.i("TAG", "onCreate: DialogMessageActivity 4");

                    TripModel tripModel = tripModels.get(0);

                    Log.i("TAG", "onCreate: " + tripId);

                    TimeAlertCustomDialog timeAlertCustomDialog = new TimeAlertCustomDialog(tripModel,getIntent().getBooleanExtra("sound",true));
                    timeAlertCustomDialog.show(getSupportFragmentManager(), "DialogTest");
                }
            });
        }

    }

//    public void play(View view) {
//        if ( player == null){
//            player = MediaPlayer.create(this,R.raw);
//        }
//        player.start();
//    }
//
//    public void stop(View view) {
//        if (player !=null){
//            player.release();
//            player = null;
//        }
//    }
}