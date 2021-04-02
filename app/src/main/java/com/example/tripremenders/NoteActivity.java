package com.example.tripremenders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.tripremenders.models.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.example.tripremenders.adapters.NoteAdapter;
import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.widget.NoteCustomDialog;

public class NoteActivity extends AppCompatActivity implements NoteCustomDialog.CustomDialogListener {

    private ArrayList<NoteModel> notes;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private FloatingActionButton fab, save;
    Toolbar toolbar;
    private NoteViewModel noteViewModel;
    private int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("Add Note");
        notes = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.note_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        tripId = getIntent().getIntExtra("tripId", 0);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotesById(tripId)
                .observe(this, new Observer<List<NoteModel>>() {
            @Override
            public void onChanged(@Nullable final List<NoteModel> noteModels) {
                // Update the cached copy of the words in the adapter.
                notes = (ArrayList<NoteModel>) noteModels;
                noteAdapter.setNotes((ArrayList<NoteModel>) noteModels);
            }
        });

        noteAdapter = new NoteAdapter(this, notes);

        recyclerView.setAdapter(noteAdapter);

        fab = findViewById(R.id.fab);
        //save = findViewById(R.id.save);
        fab.setOnClickListener(v -> {
            openDialog();
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                noteViewModel.delete(notes.get(viewHolder.getAdapterPosition()));

            }
        }).attachToRecyclerView(recyclerView);

    }

    @Override
    public void applyText(String note) {

        NoteModel noteModel = new NoteModel(tripId, note);
        noteViewModel.insert(noteModel);

        recyclerView.setAdapter(noteAdapter);
    }

    private void openDialog() {
        NoteCustomDialog noteCustomDialog = new NoteCustomDialog();
        noteCustomDialog.show(getSupportFragmentManager(), "CustomDialogTest");
    }

}