package com.example.tripremenders.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripremenders.R;
import com.example.tripremenders.adapters.UpcomingTripAdapter;
import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.NoteViewModel;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;
import com.example.tripremenders.service.WidgetService;
import com.example.tripremenders.widget.NoteListCustomDialog;
import com.google.android.gms.maps.model.LatLng;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpcomingTripsFragment extends Fragment {

    private ArrayList<TripModel> trips;
    private TripViewModel tripViewModel;
    private NoteViewModel noteViewModel;

    public UpcomingTripsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcoming_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView recyclerView = getActivity().findViewById(R.id.upcoming_trip_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        UpcomingTripAdapter upcomingTripAdapter =
                new UpcomingTripAdapter(getContext(), trips, getActivity().getMenuInflater(),
                        recyclerView);
        recyclerView.setAdapter(upcomingTripAdapter);
        upcomingTripAdapter.setStartTrip = new UpcomingTripAdapter.StartTrip() {
            @Override
            public void onClick(TripModel tripModel) {
                tripModel.setStatus(1);
                tripViewModel.update(tripModel);
                DisplayTrack(tripModel);
            }
        };

        tripViewModel.getAllUpcomingTrips().observe(this, new Observer<List<TripModel>>() {
            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                // Update the cached copy of the words in the adapter.
                upcomingTripAdapter.setTrips((ArrayList<TripModel>) tripModels);
            }
        });
    }

    private void DisplayTrack(TripModel tripModel) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, 106);
        } else {


            noteViewModel.getAllNotesById(tripModel.getId()).observe(this, new Observer<List<NoteModel>>() {
                @Override
                public void onChanged(@Nullable final List<NoteModel> noteModels) {
                    // Update the cached copy of the words in the adapter.
                    Intent floatingService = new Intent(getActivity(), WidgetService.class);
                    floatingService.putExtra("noteModels", (Serializable) noteModels);
                    floatingService.putExtra("tripModel", tripModel);
                    getActivity().startService(floatingService);
                }
            });

            try {
                //when google map installed

                Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + "/" + tripModel.getEndPoint());
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