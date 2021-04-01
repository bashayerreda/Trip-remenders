package com.example.tripremenders.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;
import com.example.tripremenders.widget.NoteListCustomDialog;
import com.google.android.gms.maps.model.LatLng;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.util.ArrayList;
import java.util.List;

public class UpcomingTripsFragment extends Fragment {
    public UpcomingTripAdapter.StartTrip setStartTrip = null;
    ArrayList<TripModel> trips;
    UpcomingTripAdapter upcomingTripAdapter;
    private TripViewModel tripViewModel;
    TripModel tripModel;
    public UpcomingTripAdapter tripAdapter;
    RecyclerView recyclerView;

    private BubblesManager bubblesManager;
    private BubblesManager bubblesTrashManager;

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
        upcomingTripAdapter.setStartTrip = new UpcomingTripAdapter.StartTrip() {
            @Override
            public void onClick(TripModel tripModel) {
//                tripModel.setStatus(1);
//                tripViewModel.update(tripModel);
                LatLng latLng = new LatLng(tripModel.getEndPointLat(), tripModel.getEndPointLng());
                DisplayTrack(latLng);
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

    private void DisplayTrack(LatLng latLng) {
        //if device dosnt have mape installed then redirect it to play store
        //https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393

        show();

        try {
            //when google map installed
            Uri uri = Uri.parse("geo:0,0?q=" + latLng.latitude + "," + latLng.longitude);
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

    private void show() {
        bubblesManager = new BubblesManager.Builder(getActivity())
                .build();
        bubblesManager.initialize();

        bubblesTrashManager = new BubblesManager.Builder(getActivity())
                .setTrashLayout(R.layout.trash_item)
                .build();
        bubblesTrashManager.initialize();

        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater
                .from(getActivity()).inflate(R.layout.floating_widget_item, null);

        bubblesManager.addBubble(bubbleView, 60, 20);

        bubbleView.setOnBubbleClickListener(bubble -> {
            ArrayList<NoteModel> awd = new ArrayList<>();
            NoteModel ddd = new NoteModel();
            ddd.setNote("awdadawdad");
            awd.add(ddd);
            awd.add(ddd);
            awd.add(ddd);
            NoteListCustomDialog noteListCustomDialog = new NoteListCustomDialog(awd);
            noteListCustomDialog.show(getFragmentManager(), "DialogTest");
        });
    }

}