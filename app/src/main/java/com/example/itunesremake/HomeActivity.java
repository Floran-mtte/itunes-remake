package com.example.itunesremake;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    BottomNavigationView bottomNavigationView;
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        bottomNavigationView = findViewById(R.id.navigation);

        pager = findViewById(R.id.viewPager);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(),"SearchFragment");
        adapter.addFragment(new FavoriteFragment(),"FavoriteFragment");
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.navigation_search:
                pager.setCurrentItem(0);
            break;
            case R.id.navigation_favorite:
                pager.setCurrentItem(1);
            break;
            case R.id.navigation_settings:
                pager.setCurrentItem(2);
            break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                    }

                    // Right to left swipe action
                    else
                    {
                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                }
                else
                {
                    Toast.makeText(this, "other",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onTouchEvent(event);
    }


}
