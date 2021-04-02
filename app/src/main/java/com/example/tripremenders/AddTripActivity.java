package com.example.tripremenders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.example.tripremenders.broadcast.AlertReceiver;
import com.example.tripremenders.models.TripViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.example.tripremenders.fragment.DatePickerFragment;
import com.example.tripremenders.fragment.TimePickerFragment;
import com.example.tripremenders.models.TripModel;

public class AddTripActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

//    private final String TAG_TRIP_NAME = "TAG_TRIP_NAME";
//    private final String TAG_START_POINT = "TAG_START_POINT";
//    private final String TAG_END_POINT = "TAG_END_POINT";
//    private final String TAG_DATE = "TAG_DATE";
//    private final String TAG_TIME = "TAG_TIME";
//    private final String TAG_SPINNER = "TAG_SPINNER";

    private Spinner spinner;
    Toolbar toolbar;
    private ImageView trip_type_image, timePicker, datePicker;
    private EditText tripName, startPoint, endPoint;
    private EditText time, date;
    private Button save;
    private TripModel model;
    private Calendar calendar;
    private TripViewModel tripViewModel;


    Handler handler = new Handler(Looper.myLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            long[] ids = bundle.getLongArray("ids");
            int tripId = (int) ids[0];
            Log.i("TAG", "test1: handler: " + calendar.getTimeInMillis() + " tripId: " + tripId);
            startAlarm(calendar, tripId);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("Add Trip");
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

        if (editTrip != null) {
            save.setText(getString(R.string.edit));
            tripName.setText(editTrip.getName());
            startPoint.setText(editTrip.getStartPoint());
            endPoint.setText(editTrip.getEndPoint());
            time.setText(editTrip.getTime());
            date.setText(editTrip.getDate());
            if (editTrip.getTripType().equals("One Direction")) {
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
            model.setTimestamp(editTrip.getTimestamp());
            calendar.setTimeInMillis(editTrip.getTimestamp());
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
            if (calendar.before(Calendar.getInstance())) {
                Toast.makeText(this, "Enter Valid Time", Toast.LENGTH_SHORT).show();
            }
            if (!(tripName.getText().toString().isEmpty() || startPoint.getText().toString().isEmpty() || endPoint.getText().toString().isEmpty() || date.getText().toString().isEmpty() ||calendar.before(Calendar.getInstance())|| time.getText().toString().isEmpty())) {

                model.setName(tripName.getText().toString());
                model.setStartPoint(startPoint.getText().toString());
                model.setEndPoint(endPoint.getText().toString());

//                Toast.makeText(this, model.getTripType(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, String.valueOf(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();

                Log.i("TAG", "onCreate: " + calendar.getTimeInMillis());

//                Date date=new Date(calendar.getTimeInMillis());
//
//                Log.i("TAG", "onCreate: " + date);

                model.setTimestamp(calendar.getTimeInMillis());

                tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

                if (editTrip != null) {
                    Log.i("TAG", "startAlarm: " + calendar.getTimeInMillis());
                    tripViewModel.update(model);
                    startAlarm(calendar, model.getId());
                } else {

                    tripViewModel.insert(model, handler);
                }

                finish();
            }
        });


    }

    @Override // google API and list of notes
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, resultCode + "", Toast.LENGTH_SHORT).show();
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
        calendar.set(Calendar.SECOND, 0);
        String s = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        model.setTime(s);
        time.setText(s);
        Log.i("TAG", "startAlarm: " + calendar.getTimeInMillis());
    }

    @Override // datePicker
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String s = DateFormat.getDateInstance().format(calendar.getTime());
        model.setDate(s);
        date.setText(s);
        Log.i("TAG", "startAlarm: " + calendar.getTimeInMillis());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar calendar, int tripId) {


        Log.i("TAG", "test1: startAlarm: " + calendar.getTimeInMillis() + " tripId: " + tripId);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlertReceiver.class);

        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("tripId", tripId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, tripId, intent, 0);

        /*if (calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }*/

        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("start_Lat", model.getStartPointLat());
        outState.putDouble("start_Lng", model.getStartPointLng());
        outState.putDouble("End_Lat", model.getEndPointLat());
        outState.putDouble("End_Lng", model.getEndPointLng());

        outState.putString("date", model.getDate());
        outState.putString("time", model.getTime());

        outState.putLong("calender", calendar.getTimeInMillis());
//
//        outState.putString(TAG_TRIP_NAME, tripName.getText().toString());
//        outState.putString(TAG_START_POINT, startPoint.getText().toString());
//        outState.putString(TAG_END_POINT, endPoint.getText().toString());
//        outState.putString(TAG_DATE,  date.getText().toString());
//        outState.putString(TAG_TIME, time.getText().toString());
//        outState.putString(TAG_SPINNER, spinner.getSelectedItem().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        model.setStartPointLat(savedInstanceState.getDouble("start_Lat"));
        model.setStartPointLng(savedInstanceState.getDouble("start_Lng"));
        model.setEndPointLat(savedInstanceState.getDouble("End_Lat"));
        model.setEndPointLng(savedInstanceState.getDouble("End_Lng"));

        model.setDate(savedInstanceState.getString("date"));
        model.setTime(savedInstanceState.getString("time"));

        calendar.setTimeInMillis(savedInstanceState.getLong("calender"));
    }
}