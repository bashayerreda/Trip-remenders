package com.example.tripremenders.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;

import com.example.tripremenders.AddTripActivity;
import com.example.tripremenders.NoteActivity;
import com.example.tripremenders.R;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;

import java.util.ArrayList;

public class UpcomingTripAdapter extends RecyclerView.Adapter<UpcomingTripAdapter.ViewHolder> {

    ArrayList<TripModel>  trips;
    Context context;
    MenuInflater menuInflater;
   public RecyclerView recyclerView;
    public StartTrip setStartTrip = null;
   public TripModel tripModel;
    LinearLayout lastLinearLayoutClickOn;
    public interface StartTrip {
        void onClick(TripModel tripModel);
    }
    public UpcomingTripAdapter(){}
    public UpcomingTripAdapter(Context context,
                               ArrayList<TripModel> trips,
                               MenuInflater menuInflater,
                               RecyclerView recyclerView) {
        super();

        this.context = context;
        this.trips = trips;
        this.menuInflater = menuInflater;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.trip_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tripNameTextView.setText(trips.get(position).getName());
        holder.dateTextView.setText(trips.get(position).getDate());
        holder.timeTextView.setText(trips.get(position).getTime());
        holder.startPointTextView.setText(trips.get(position).getStartPoint());
        holder.endPointTextView.setText(trips.get(position).getEndPoint());
        holder.tripTypeTextView.setText(trips.get(position).getTripType());
        if(trips.get(position).getTripType().equals("One Direction")) {
            holder.tripeTypeImageView.setImageResource(R.drawable.ic_ray_start_arrow);
        } else {
            holder.tripeTypeImageView.setImageResource(R.drawable.ic_multiple_stop);
        }

        holder.tripLinearLayout.setOnClickListener(v -> {
            if (trips.get(position).getTripType() != null) {
                if (lastLinearLayoutClickOn != null
                        && lastLinearLayoutClickOn != holder.buttonsLinearLayout) {
                    lastLinearLayoutClickOn.setVisibility(View.GONE);
                }
            }
            lastLinearLayoutClickOn =  holder.buttonsLinearLayout;

            final int visibilityOfButtons = holder.buttonsLinearLayout.getVisibility();

            Transition transition = new Slide(Gravity.TOP);
            transition.setDuration(200);
            transition.addTarget(holder.buttonsLinearLayout);
            transition.addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionStart(@NonNull Transition transition) {
                    super.onTransitionStart(transition);
                    if(visibilityOfButtons == View.GONE) {
                        recyclerView.smoothScrollToPosition(position);
                    }
                }

            });

            TransitionManager.beginDelayedTransition(holder.tripLinearLayout, transition);
            holder.buttonsLinearLayout
                    .setVisibility(
                            holder.buttonsLinearLayout.getVisibility() == View.GONE  ?
                                    View.VISIBLE : View.GONE );
        });
        if (setStartTrip != null) {
            holder.btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStartTrip.onClick(trips.get(position));
                }
            });
        }


        holder.editTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTripActivity.class);
            intent.putExtra("trip", trips.get(position));
            context.startActivity(intent);
        });

        holder.addNoteTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("tripId", trips.get(position).getId());
            context.startActivity(intent);
        });

        holder.deleteTripButton.setOnClickListener(v -> {
            TripViewModel tripViewModel = ViewModelProviders.of((FragmentActivity) context).get(TripViewModel.class);
            tripViewModel.delete(trips.get(position));
        });


    }

    public void setTrips(ArrayList<TripModel> trips){
        this.trips = trips;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (trips != null)
            return trips.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout tripLinearLayout;

        TextView tripNameTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView startPointTextView;
        TextView endPointTextView;
        TextView tripTypeTextView;
        ImageView tripeTypeImageView;

        ImageButton editTripButton;
        ImageButton addNoteTripButton;
        ImageButton deleteTripButton;

        LinearLayout buttonsLinearLayout;

        Button btnStart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripNameTextView = itemView.findViewById(R.id.trip_name);
            dateTextView = itemView.findViewById(R.id.trip_date);
            timeTextView = itemView.findViewById(R.id.trip_time);
            startPointTextView = itemView.findViewById(R.id.trip_start_point);
            endPointTextView = itemView.findViewById(R.id.trip_end_point);
            tripTypeTextView = itemView.findViewById(R.id.trip_type);
            tripeTypeImageView = itemView.findViewById(R.id.trip_type_icon);
            tripLinearLayout = itemView.findViewById(R.id.trip_linear_layout);
            buttonsLinearLayout = itemView.findViewById(R.id.buttons_linear_layout);
            btnStart = itemView.findViewById(R.id.btn_start);
            editTripButton =  itemView.findViewById(R.id.edit_trip);
            addNoteTripButton = itemView.findViewById(R.id.add_note_trip);
            deleteTripButton = itemView.findViewById(R.id.delete_trip);
        }
    }
}
