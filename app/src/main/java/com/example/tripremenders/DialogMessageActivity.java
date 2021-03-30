package com.example.tripremenders;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tripremenders.widget.TimeAlertCustomDialog;

public class DialogMessageActivity extends AppCompatActivity {
//    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dialog_message);


        String s = getIntent().getStringExtra("G2");
        Log.i("TAG", "onCreate: " + s);

        TimeAlertCustomDialog noteCustomDialog = new TimeAlertCustomDialog(s);
        noteCustomDialog.show(getSupportFragmentManager(), "DialogTest");

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