package com.example.tripremenders;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tripremenders.fragment.PastTripsFragment;
import com.example.tripremenders.fragment.SettingsFragment;
import com.example.tripremenders.fragment.UpcomingTripsFragment;

public class TripsFragmentPagerAdapter extends FragmentPagerAdapter {


    public TripsFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UpcomingTripsFragment();
            case 1:
                return new PastTripsFragment();
            case 2:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
