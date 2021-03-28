package com.example.tripremenders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.TripViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.tripremenders.fragment.DatePickerFragment;
import com.example.tripremenders.fragment.TimePickerFragment;
import com.example.tripremenders.models.TripModel;

public class AddTripActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private Spinner spinner;
    private ImageView trip_type_image, timePicker, datePicker;
    private EditText tripName, startPoint, endPoint;
    private EditText time, date;
    private Button save;
    private TripModel model;

    private Calendar calendar;

    private TripViewModel tripViewModel;

    private ArrayList<Integer> idArrayListr;
    private ArrayList<Integer> tripIdArrayList;
    private ArrayList<String> noteArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        final TripModel editTrip = (TripModel) getIntent().getSerializableExtra("trip");

        model = new TripModel();

        calendar = Calendar.getInstance();

        trip_type_image = findViewById(R.id.trip_type_image);
        timePicker = findViewById(R.id.time_picker);
        time = findViewById(R.id.time);
        datePicker = findViewById(R.id.date_picker);
        date = findViewById(R.id.date);
        tripName = findViewById(R.id.trip_name);
        startPoint = findViewById(R.id.trip_start_point);
        endPoint = findViewById(R.id.trip_end_point);
        save = findViewById(R.id.save);

        spinner = findViewById(R.id.trip_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if(editTrip != null) {
            save.setText(getString(R.string.edit));
            tripName.setText(editTrip.getName());
            startPoint.setText(editTrip.getStartPoint());
            endPoint.setText(editTrip.getEndPoint());
            time.setText(editTrip.getTime());
            date.setText(editTrip.getDate());
            if(editTrip.getTripType().equals("One Direction")) {
                spinner.setSelection(0);
            } else {
                spinner.setSelection(1);
            }

            model.setTime(editTrip.getTime());
            model.setDate(editTrip.getDate());
            model.setId(editTrip.getId());
            model.setStartPointLat(editTrip.getStartPointLat());
            model.setStartPointLng(editTrip.getStartPointLng());
            model.setEndPointLat(editTrip.getEndPointLat());
            model.setEndPointLng(editTrip.getEndPointLng());
        }

        Places.initialize(getApplicationContext(), getString(R.string.api_key));

        startPoint.setOnClickListener(v -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(this);
            startActivityForResult(intent, 1);
        });
        endPoint.setOnClickListener(v -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(this);
            startActivityForResult(intent, 2);
        });
        timePicker.setOnClickListener(v -> {
            DialogFragment timeFragment = new TimePickerFragment();
            timeFragment.show(getSupportFragmentManager(), "TIME");
        });
        datePicker.setOnClickListener(v -> {
            DialogFragment dateFragment = new DatePickerFragment();
            dateFragment.show(getSupportFragmentManager(), "DATE");
        });

        save.setOnClickListener(v -> {
            if (tripName.getText().toString().isEmpty()) {
                tripName.setError("Required");
            }
            if (startPoint.getText().toString().isEmpty()) {
                startPoint.setError("Required");
            }
            if (endPoint.getText().toString().isEmpty()) {
                endPoint.setError("Required");
            }
            if (time.getText().toString().isEmpty()) {
                time.setError("Required");
            }
            if (date.getText().toString().isEmpty()) {
                date.setError("Required");
            }
            if (!(tripName.getText().toString().isEmpty() || startPoint.getText().toString().isEmpty() || endPoint.getText().toString().isEmpty() || date.getText().toString().isEmpty() || time.getText().toString().isEmpty())) {

                model.setName(tripName.getText().toString());
                model.setStartPoint(startPoint.getText().toString());
                model.setEndPoint(endPoint.getText().toString());

                Toast.makeText(this, model.getTripType(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this,  String.valueOf(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();

                Log.i("TAG", "onCreate: " + calendar.getTimeInMillis());

//                Date date=new Date(calendar.getTimeInMillis());
//
//                Log.i("TAG", "onCreate: " + date);

                model.setTimestamp(calendar.getTimeInMillis());

                tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

                if(editTrip != null) {
                    tripViewModel.update(model);
                } else {
                    tripViewModel.insert(model);
                }

                finish();


            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        bubblesManager.recycle();
    }

    @Override // google API and list of notes
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, resultCode + "", Toast.LENGTH_SHORT).show();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            startPoint.setText(place.getAddress());
            model.setStartPointLat(place.getLatLng().latitude);
            model.setStartPointLng(place.getLatLng().longitude);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            endPoint.setText(place.getAddress());
            model.setEndPointLat(place.getLatLng().latitude);
            model.setEndPointLng(place.getLatLng().longitude);
        } else if (requestCode == 10 && resultCode == RESULT_OK) {
            idArrayListr = data.getIntegerArrayListExtra("idArrayListr");
            tripIdArrayList = data.getIntegerArrayListExtra("tripIdArrayList");
            noteArrayList = data.getStringArrayListExtra("noteArrayList");
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override // spinner change image
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String s = parent.getItemAtPosition(position).toString();
        model.setTripType(s);
        if (s.equals("One Direction")) {
            trip_type_image.setImageResource(R.drawable.ic_ray_start_arrow);
        } else {
            trip_type_image.setImageResource(R.drawable.ic_multiple_stop);
        }
        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override// spinner
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override // timePicker
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String s = DateFormat.getTimeInstance().format(calendar.getTime());

        model.setTime(s);
        time.setText(s);
        Toast.makeText(this, hourOfDay + "", Toast.LENGTH_SHORT).show();
    }

    @Override // datePicker
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String s = DateFormat.getDateInstance().format(calendar.getTime());
        model.setDate(s);
        date.setText(s);
    }


}