package com.example.tripremenders.models;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;

import java.util.List;


public class TripRepository {

    private TripDao tripDao;
    private LiveData<List<TripModel>> allTrips;
    private LiveData<List<TripModel>> upComingTrips;
    private LiveData<List<TripModel>> pastTrips;

    TripRepository(Application application) {
        TripDatabase tripDatabase = TripDatabase.getDatabase(application);
        tripDao = tripDatabase.tripDao();

    }

    public LiveData<List<TripModel>> getAllTrips() {
        allTrips = tripDao.getAllTrips();
        return allTrips;
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips() {
        upComingTrips = tripDao.getAllUpComingTrips();
        return upComingTrips;
    }

    public LiveData<List<TripModel>> getAllPastTrips() {

        pastTrips = tripDao.getAllPastTrips();

        return pastTrips;
    }

    public void update(TripModel tripModel) {
        new UpdateThread(tripDao, tripModel).start();
    }

    public void insert(TripModel tripModel ,Handler handler) {
        new InsertThread(tripDao, tripModel,handler).start();
        //new insertAsyncTask(tripDao).execute(tripModel);
    }

    public void delete(TripModel tripModel) {
        new DeleteTripThread(tripDao, tripModel).start();
    }

    public void deleteAll() {
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
        Handler handler;
        //GetTripIdFromRoom idFromRoom;
        private TripDao tripDao;
        private TripModel trip;

        InsertThread(TripDao tripDao, TripModel trip, Handler handler /*GetTripIdFromRoom idFromRoom*/) {
            super();
            this.tripDao = tripDao;
            this.trip = trip;
            this.handler = handler;
            //this.idFromRoom = idFromRoom;
        }

        @Override
        public void run() {
            long[] ids = tripDao.insert(trip) ;
            if (handler != null) {
                Bundle bundle = new Bundle();
                bundle.putLongArray("ids", ids);
                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);
            }


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
