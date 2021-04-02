package com.example.tripremenders.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes",
        foreignKeys = {
        @ForeignKey(entity = TripModel.class,
        parentColumns = "id",
        childColumns = "tripId",
        onDelete = ForeignKey.CASCADE)
}, indices = {@Index(value = {"tripId"}, unique = false)})
public class NoteModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "tripId")
    private int tripId;

    @ColumnInfo
    private String note;

    public NoteModel() {
    }

    @Ignore
    public NoteModel(String note) {
        this.note = note;
    }

    @Ignore
    public NoteModel(int tripId, String note) {
        this.tripId = tripId;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
