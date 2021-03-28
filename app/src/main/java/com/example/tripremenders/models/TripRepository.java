package com.example.tripremenders.models;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TripRepository {

    private TripDao tripDao;
    private final LiveData<List<TripModel>> allTrips;
    private final LiveData<List<TripModel>> upComingTrips;
    private final LiveData<List<TripModel>> pastTrips;

    TripRepository(Application application) {
     TripDatabase tripDatabase = TripDatabase.getDatabase(application);
        tripDao = tripDatabase.tripDao();
        allTrips = tripDao.getAllTrips();
        upComingTrips = tripDao.getAllUpComingTrips();
        pastTrips = tripDao.getAllPastTrips();
    }

    public LiveData<List<TripModel>> getAllTrips() {
        return allTrips;
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips() {
        return upComingTrips;
    }

    public LiveData<List<TripModel>> getAllPastTrips() {
        return pastTrips;
    }

    public void update (TripModel tripModel) {
        new InsertThread(tripDao, tripModel).start();
    }

    public void insert (TripModel tripModel) {
        new InsertThread(tripDao, tripModel).start();
        //new insertAsyncTask(tripDao).execute(tripModel);
    }

    public void delete (TripModel tripModel) {
        new DeleteTripThread(tripDao, tripModel).start();
    }

    public  void deleteAll () {
        new DeleteAllTripsThread(tripDao).start();
    }


    private class UpdateThread extends Thread {

        private TripDao tripDao;
        private TripModel trip;

        UpdateThread(TripDao tripDao, TripModel trip) {
            super();
            this.tripDao = tripDao;
            this.trip = trip;
        }

        @Override
        public void run() {
            tripDao.update(trip);
        }
    }

    private class InsertThread extends Thread {

        private TripDao tripDao;
        private TripModel trip;

        InsertThread(TripDao tripDao, TripModel trip) {
            super();
            this.tripDao = tripDao;
            this.trip = trip;
        }

        @Override
        public void run() {
            tripDao.insert(trip);
        }
    }



    private static class DeleteAllTripsThread extends Thread {

        TripDao tripDao;

        public DeleteAllTripsThread(TripDao tripDao) {

            this.tripDao = tripDao;
        }

        @Override
        public void run() {
            tripDao.deleteAll();
        }
    }

    private static class DeleteTripThread extends Thread {

        TripDao tripDao;
        TripModel trip;

        public DeleteTripThread(TripDao tripDao, TripModel trip) {

            this.tripDao = tripDao;
            this.trip = trip;
        }

        @Override
        public void run() {
            tripDao.delete(trip);
        }
    }
}
