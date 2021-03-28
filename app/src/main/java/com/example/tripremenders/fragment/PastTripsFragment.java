package com.example.tripremenders.fragment;

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
import com.example.tripremenders.adapters.PastTripAdapter;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;

public class PastTripsFragment extends Fragment {

    ArrayList<TripModel> trips;

    private TripViewModel tripViewModel;


    public PastTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        trips = new ArrayList<>();
//
//        trips.add(tripModel);
//
//        trips.add(
//                new TripModel()
//                        .setName("Trip name 2")
//                        .setStartPoint("Al Mandarah QebliMontaza 2, Alexandria Governorate ")
//                        .setEndPoint("Al Mandarah QebliMontaza 2, Alexandria Governorate ")
//                        .setTripType("One Direction")
//                        .setStatus(2)
//
//        );
//
//        trips.add(
//                new TripModel()
//                        .setName("Trip name 3")
//                        .setStartPoint("Al Mandarah QebliMontaza 2, Alexandria Governorate ")
//                        .setEndPoint("Al Mandarah QebliMontaza 2, Alexandria Governorate ")
//                        .setTripType("Round Tripe")
//                        .setStatus(0)
//
//        );
//
//        trips.add(
//                new TripModel()
//                        .setName("Trip name 4")
//                        .setStartPoint("Al Mandarah QebliMontaza 2, Alexandria Governorate ")
//                        .setEndPoint("Al Mandarah QebliMontaza 2, Alexandria Governorate ")
//                        .setTripType("One Direction")
//                        .setStatus(1)
//
//        );
//
//        trips.add(
//                new TripModel()
//                        .setName("Trip name 5")
//                        .setTripType("Round Tripe")
//                        .setStatus(0)
//
//        );

        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        RecyclerView recyclerView = getActivity().findViewById(R.id.past_trip_recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);

        PastTripAdapter pastAdapter = new PastTripAdapter(getContext(), trips, getActivity().getMenuInflater(), recyclerView);

        recyclerView.setAdapter(pastAdapter);

        tripViewModel.getAllPastTrips().observe(this, new Observer<List<TripModel>>() {
            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                // Update the cached copy of the words in the adapter.
                pastAdapter.setTrips((ArrayList<TripModel>) tripModels);
            }
        });
    }
}