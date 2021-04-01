package com.example.tripremenders.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripremenders.R;
import com.example.tripremenders.adapters.NoteAdapter;
import com.example.tripremenders.models.NoteModel;

import java.util.ArrayList;

public class NoteListCustomDialog extends AppCompatDialogFragment {

    RecyclerView recyclerView;
    ArrayList<NoteModel> notes;
    NoteAdapter noteAdapter;

    public NoteListCustomDialog(ArrayList<NoteModel> notes) {
        this.notes = notes ;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(getContext(), notes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.notes_list_dialog, null);
        builder.setView(view);

        recyclerView = view.findViewById(R.id.note_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(noteAdapter);
        return builder.create();
    }


}