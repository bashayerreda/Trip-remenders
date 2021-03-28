package com.example.tripremenders.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private final TripRepository tripRepository;

    public TripViewModel(Application application) {
        super(application);

        tripRepository = new TripRepository(application);
    }

    public LiveData<List<TripModel>> getAllTrips() {
        LiveData<List<TripModel>> allTrips = tripRepository.getAllTrips();
        return allTrips;
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips() {
        LiveData<List<TripModel>> upcomingTrips = tripRepository.getAllUpcomingTrips();
        return upcomingTrips;
    }

    public LiveData<List<TripModel>> getAllPastTrips() {
        LiveData<List<TripModel>> pastTrips = tripRepository.getAllPastTrips();
        return pastTrips;
    }

    public void update(TripModel trip) {
        tripRepository.update(trip);
    }

    public void insert(TripModel trip) {
        tripRepository.insert(trip);
    }

    public void delete(TripModel trip) {
        tripRepository.delete(trip);
    }

    public void deleteAll() {
        tripRepository.deleteAll();
    }
}
