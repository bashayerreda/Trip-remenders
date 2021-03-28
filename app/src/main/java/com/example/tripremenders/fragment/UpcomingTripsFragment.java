package com.example.tripremenders.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.example.tripremenders.R;
import com.example.tripremenders.adapters.UpcomingTripAdapter;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;

public class UpcomingTripsFragment extends Fragment {

    ArrayList<TripModel> trips;
   UpcomingTripAdapter tripAdapter;
    private TripViewModel tripViewModel;


    public UpcomingTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        tripViewModel.getAllUpcomingTrips().observe(this, new Observer<List<TripModel>>() {
            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                // Update the cached copy of the words in the adapter.
                upcomingTripAdapter.setTrips((ArrayList<TripModel>) tripModels);
            }
        });

    }
    private void DisplayTrack(String sDestination) {
        //if device dosnt have mape installed then redirect it to play store

        try {
            //when google map installed
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + "/" + sDestination);

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

}