package com.example.tripremenders.fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tripremenders.MapsActivity;
import com.example.tripremenders.R;
import com.example.tripremenders.RegistrationActivity;
import com.example.tripremenders.broadcast.AlertReceiver;
import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.NoteViewModel;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private TextView textViewUsername;
    private CircleImageView imageViewProfilePicture;
    private List<TripModel>trips;

    public final static String SEND_TRIPS_EXTRA = "trips Data";

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            String profilePicture = user.getPhotoUrl().toString();
            profilePicture += "?type=large";
            Picasso.get().load(profilePicture).fit().placeholder(R.drawable.user_icon).into(imageViewProfilePicture);

        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                String username = user.getDisplayName().toString();
                textViewUsername.setText(username);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageViewProfilePicture = view.findViewById(R.id.user_logo);
        textViewUsername = view.findViewById(R.id.user_name_settings);

        TextView syncButton = view.findViewById(R.id.sync_button);
        syncButton.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReferenceTrips =
                    database.getReference("trips").child(currentUser.getUid());
            DatabaseReference databaseReferenceNotes =
                    database.getReference("notes").child(currentUser.getUid());

            TripViewModel tripViewModel =
                    ViewModelProviders.of(this).get(TripViewModel.class);

            NoteViewModel noteViewModel =
                    ViewModelProviders.of(this).get(NoteViewModel.class);

            LiveData<List<TripModel>> allTrips = tripViewModel.getAllTrips();
            LiveData<List<NoteModel>> allNotes = noteViewModel.getAllNotes();

            Toast.makeText(getActivity(), "Sync Trips", Toast.LENGTH_SHORT).show();

            allTrips.observe(getActivity(), new Observer<List<TripModel>>() {

                @Override
                public void onChanged(@Nullable final List<TripModel> tripModels) {
                    // Update the cached copy of the words in the adapter.
                    databaseReferenceTrips.setValue(tripModels);
                    allTrips.removeObserver(this);
                }
            });

            allNotes.observe(getActivity(), new Observer<List<NoteModel>>() {

                @Override
                public void onChanged(@Nullable final List<NoteModel> notesModels) {
                    // Update the cached copy of the words in the adapter.
                    databaseReferenceNotes.setValue(notesModels);
                    allNotes.removeObserver(this);
                }
            });
        });

        TextView logoutButton = view.findViewById(R.id.Logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.alert_dialog_signout_item);
                dialog.setCancelable(false);
                dialog.show();
                TextView textViewYesLogout = dialog.findViewById(R.id.text_yes_logout);
                TextView textViewNoLogout = dialog.findViewById(R.id.text_no_logout);
                textViewYesLogout.setOnClickListener(new View.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        TripViewModel tripViewModel =
                                ViewModelProviders.of(SettingsFragment.this)
                                        .get(TripViewModel.class);

                        NoteViewModel noteViewModel =
                                ViewModelProviders.of(SettingsFragment.this)
                                        .get(NoteViewModel.class);

                        tripViewModel.deleteAll();
                        noteViewModel.deleteAll();

                        dialog.dismiss();
                        LoginManager.getInstance().logOut();
                        FirebaseAuth.getInstance().signOut();

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                        Intent updateServiceIntent = new Intent(getActivity(), AlertReceiver.class);
                        PendingIntent pendingUpdateIntent = PendingIntent.getService(getActivity(), 0, updateServiceIntent, 0);

                        // Cancel alarms
                        try {
                            alarmManager.cancel(pendingUpdateIntent);
                        } catch (Exception e) {
                            Log.e("AlarmManager", "AlarmManager update was not canceled. " + e.toString());
                        }

                        Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                        startActivity(intent);
                        getActivity().finishAffinity();

                    }
                });
                textViewNoLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }

        });


        TextView btnmap = view.findViewById(R.id.map);
        TripViewModel tripViewModelmap =
                ViewModelProviders.of(SettingsFragment.this).get(TripViewModel.class);
        tripViewModelmap.getAllPastTrips().observe(getActivity(), new Observer<List<TripModel>>() {
            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                trips = tripModels;

            }
        });
        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trips != null) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable(SEND_TRIPS_EXTRA, (Serializable) trips);
                    intent.putExtra(SEND_TRIPS_EXTRA, args);
                    startActivity(intent);
                    //getActivity().finish();
                }



            }

        });

    }
}
