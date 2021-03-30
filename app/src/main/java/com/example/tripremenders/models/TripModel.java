package com.example.tripremenders.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "trips")
public class TripModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo(name = "start_point")
    private String startPoint;

    @ColumnInfo(name = "end_point")
    private String endPoint;

    @ColumnInfo
    private Long timestamp;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private String time;

    @ColumnInfo(name = "trip_type")
    private String tripType;

    @ColumnInfo
    private int status;

    @ColumnInfo(name = "start_Lat")
    private double startPointLat;

    @ColumnInfo(name = "start_Lng")
    private double startPointLng;

    @ColumnInfo(name = "end_Lat")
    private double endPointLat;

    @ColumnInfo(name = "end_Lng")
    private double endPointLng;
    //@ColumnInfo(name = "Lng_Lat")
    //private double lnglat;

    public TripModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public double getStartPointLat() {
        return startPointLat;
    }

    public void setStartPointLat(double startPointLat) {
        this.startPointLat = startPointLat;
    }

    public double getStartPointLng() {
        return startPointLng;
    }

    public void setStartPointLng(double startPointLng) {
        this.startPointLng = startPointLng;
    }

    public double getEndPointLat() {
        return endPointLat;
    }

    public void setEndPointLat(double endPointLat) {
        this.endPointLat = endPointLat;
    }

    public double getEndPointLng() {
        return endPointLng;
    }

    public void setEndPointLng(double endPointLng) {
        this.endPointLng = endPointLng;
    }
}
