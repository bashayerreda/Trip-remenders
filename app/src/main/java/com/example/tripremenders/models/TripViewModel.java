package com.example.tripremenders.models;

import android.app.Application;
import android.os.Handler;

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
        return tripRepository.getAllTrips();
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips() {
        return tripRepository.getAllUpcomingTrips();
    }

    public LiveData<List<TripModel>> getAllPastTrips() {
        return tripRepository.getAllPastTrips();
    }

    public LiveData<List<TripModel>> getTripById(int id) {
        return tripRepository.getTripById(id);
    }

    public void update(TripModel trip) {
        tripRepository.update(trip);
    }

    public void insert(TripModel trip, Handler handler) {
        tripRepository.insert(trip, handler);
    }

    public void delete(TripModel trip) {
        tripRepository.delete(trip);
    }

    public void deleteAll() {
        tripRepository.deleteAll();
    }
}
