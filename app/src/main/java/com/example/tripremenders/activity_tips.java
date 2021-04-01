package com.example.tripremenders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_tips extends AppCompatActivity {
    private ViewPager mainViewPager;
    private LinearLayout mDotsLinearLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button previousBtn ;
    private Button nextBtn ;
    private int currentPage ;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            openHomeActivityWithoutExtra();
        } else {

            setContentView(R.layout.activity_tips);

            mainViewPager = (ViewPager)findViewById(R.id.view_pager_id);
            mDotsLinearLayout=(LinearLayout)findViewById(R.id.linear_layout_id);
            previousBtn =(Button)findViewById(R.id.button_previous);
            nextBtn =(Button)findViewById(R.id.button_next);
            sliderAdapter = new SliderAdapter(this);
            mainViewPager.setAdapter(sliderAdapter);
            addDotsIndicator(0);
            mainViewPager.addOnPageChangeListener(viewlistner);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainViewPager.setCurrentItem(currentPage +1);
                }
            });
            previousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainViewPager.setCurrentItem(currentPage -1);
                }
            });
        }
    }

    private void openHomeActivityWithoutExtra() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }



    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotsLinearLayout.removeAllViews();
        for(int i =0 ; i < mDots.length ; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.white));
            mDotsLinearLayout.addView(mDots[i]);
        }
        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }

    }
    ViewPager.OnPageChangeListener viewlistner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            currentPage= i;
            if(i==0){
                nextBtn.setEnabled(true);
                previousBtn.setEnabled(false);
                previousBtn.setVisibility(View.INVISIBLE);
                nextBtn.setText("Next");
                previousBtn.setText("");
            }
            else if (i == mDots.length -1){
                nextBtn.setEnabled(true);
                previousBtn.setEnabled(true);
                previousBtn.setVisibility(View.VISIBLE);
                nextBtn.setText("finish");
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity_tips.this, RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                previousBtn.setText("previous");
            } else {
                nextBtn.setEnabled(true);
                previousBtn.setEnabled(true);
                previousBtn.setVisibility(View.VISIBLE);
                nextBtn.setText("Next");
                previousBtn.setText("previous");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}




