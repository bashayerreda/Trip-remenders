package com.example.tripremenders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.tripremenders.models.NoteModel;
import com.example.tripremenders.models.NoteViewModel;
import com.example.tripremenders.models.TripModel;
import com.example.tripremenders.models.TripViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    CircleImageView imageViewProfilePicture;
    FirebaseUser user;
    String profilePicture;
    private long backPressedTime;
    TripsFragmentPagerAdapter tripsFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("Tripo");
        setSupportActionBar(toolbar);
        setPagerAdapter();
        setTabLayout();
        /*user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            profilePicture = user.getPhotoUrl().toString();
            profilePicture += "?type=large";
            Picasso.get().load(profilePicture).fit().placeholder(R.drawable.user_icon).into(imageViewProfilePicture);
        }*/
        //firebaseAuth.getCurrentUser();
        //firebaseUser.getUid();

        final boolean getDataFromFirebase =
                getIntent().getBooleanExtra("getDataFromFirebase", false);

        if (getDataFromFirebase) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReferenceTrips =
                    database.getReference("trips").child(currentUser.getUid());
            DatabaseReference databaseReferenceNotes =
                    database.getReference("notes").child(currentUser.getUid());

            TripViewModel tripViewModel =
                    ViewModelProviders.of(this).get(TripViewModel.class);

            NoteViewModel noteViewModel =
                    ViewModelProviders.of(this).get(NoteViewModel.class);

            databaseReferenceTrips.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        tripViewModel.insert(dataSnapshot.getValue(TripModel.class), null);
                    }
                    databaseReferenceNotes.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                noteViewModel.insert(dataSnapshot.getValue(NoteModel.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


        }
    }

    private void setPagerAdapter() {
        tripsFragmentPagerAdapter = new TripsFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setPageTransformer(true, new ZoomAnimation());
        viewPager.setAdapter(tripsFragmentPagerAdapter);
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Upcoming Trips");
        tabLayout.getTabAt(1).setText("Past Trips");
        tabLayout.getTabAt(2).setText("Settings");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addTrip) {
            //Toast.makeText(this, "add More", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(this, AddTripActivity.class), 100);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestCode &= 0xffff;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}