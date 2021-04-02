package com.example.tripremenders.models;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;

import java.util.List;


public class TripRepository {

    private final TripDao tripDao;

    public TripRepository(Application application) {
        TripDatabase tripDatabase = TripDatabase.getDatabase(application);
        tripDao = tripDatabase.tripDao();

    }

    public LiveData<List<TripModel>> getAllTrips() {
        return tripDao.getAllTrips();
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips() {
        return tripDao.getAllUpComingTrips();
    }

    public LiveData<List<TripModel>> getAllPastTrips() {
        return tripDao.getAllPastTrips();
    }

    public LiveData<List<TripModel>> getTripById(int id) {
        return tripDao.getTripById(id);
    }

    public void update(TripModel tripModel) {
        new UpdateThread(tripDao, tripModel).start();
    }

    public void insert(TripModel tripModel, Handler handler) {
        new InsertThread(tripDao, tripModel, handler).start();
        //new insertAsyncTask(tripDao).execute(tripModel);
    }

    public void delete(TripModel tripModel) {
        new DeleteTripThread(tripDao, tripModel).start();
    }

    public void deleteAll() {
        new DeleteAllTripsThread(tripDao).start();
    }


    private class UpdateThread extends Thread {

        private final TripDao tripDao;
        private final TripModel trip;

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
        private final Handler handler;
        private final TripDao tripDao;
        private final TripModel trip;

        InsertThread(TripDao tripDao, TripModel trip, Handler handler) {
            super();
            this.tripDao = tripDao;
            this.trip = trip;
            this.handler = handler;
            //this.idFromRoom = idFromRoom;
        }

        @Override
        public void run() {
            long[] ids = tripDao.insert(trip);
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

        private final TripDao tripDao;

        public DeleteAllTripsThread(TripDao tripDao) {

            this.tripDao = tripDao;
        }

        @Override
        public void run() {
            tripDao.deleteAll();
        }
    }

    private static class DeleteTripThread extends Thread {

        private final TripDao tripDao;
        private final TripModel trip;

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
