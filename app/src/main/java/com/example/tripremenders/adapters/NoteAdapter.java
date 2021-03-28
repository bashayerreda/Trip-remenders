package com.example.tripremenders.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.tripremenders.R;
import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.TripModel;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    ArrayList<NoteModel> notes;
    Context context;

    public NoteAdapter(Context context, ArrayList<NoteModel> notes) {
        super();
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_item, parent, false);
        NoteAdapter.ViewHolder viewHolder = new NoteAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        holder.noteTextView.setText(notes.get(position).getNote());
    }

    public void setNotes(ArrayList<NoteModel> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (notes != null)
            return notes.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.note);

        }
    }
}

