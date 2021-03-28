package com.example.tripremenders.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository noteRepository;

    public NoteViewModel(Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
    }

    public LiveData<List<NoteModel>> getAllNotes() {
        LiveData<List<NoteModel>> notes = noteRepository.getAllNotes();
        return notes;
    }

    public LiveData<List<NoteModel>> getAllNotesById(final int tripId) {
        LiveData<List<NoteModel>> notes = noteRepository.getAllNotesById(tripId);
        return notes;
    }

    public void update(NoteModel note) {
        noteRepository.update(note);
    }

    public void insert(NoteModel note) {
        noteRepository.insert(note);
    }

    public void delete(NoteModel note) {
        noteRepository.delete(note);
    }

    public void deleteAll() {
        noteRepository.deleteAll();
    }
}
